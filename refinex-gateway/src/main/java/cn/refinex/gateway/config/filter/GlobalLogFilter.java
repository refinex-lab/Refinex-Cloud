package cn.refinex.gateway.config.filter;

import cn.hutool.core.map.MapUtil;
import cn.refinex.common.json.utils.JsonUtils;
import cn.refinex.gateway.config.propertirs.ApiDecryptProperties;
import cn.refinex.gateway.config.propertirs.CustomGatewayProperties;
import cn.refinex.gateway.utils.WebFluxUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 全局日志过滤器
 *
 * @author Lion Li
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalLogFilter implements GlobalFilter, Ordered {

    private final CustomGatewayProperties customGatewayProperties;
    private final ApiDecryptProperties apiDecryptProperties;

    private static final String START_TIME = "startTime";

    /**
     * 全局日志过滤器
     *
     * @param exchange 服务器 Web 交换
     * @param chain    网关筛选器链
     * @return 空 Mono
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 检查是否开启了请求日志
        if (!customGatewayProperties.getRequestLog()) {
            return chain.filter(exchange);
        }

        // 获取原始请求信息
        ServerHttpRequest request = exchange.getRequest();
        String path = WebFluxUtils.getOriginalRequestUrl(exchange);
        String url = request.getMethod().name() + " " + path;

        // 如果已经是 JSON 请求，直接记录参数
        if (WebFluxUtils.isJsonRequest(exchange)) {
            // 加密开关
            Boolean apiDecryptPropertiesEnabled = apiDecryptProperties.getEnabled();
            // 头部标识
            String headerFlag = request.getHeaders().getFirst(apiDecryptProperties.getHeaderFlag());

            // 如果开启了加密开关且头部标识存在，不记录参数
            if (Boolean.TRUE.equals(apiDecryptPropertiesEnabled) && StringUtils.isNotBlank(headerFlag)) {
                log.info("开始请求 => URL[{}], 参数类型[encrypt]", url);
            } else {
                // 解析 JSON 参数进行记录
                String jsonParam = WebFluxUtils.resolveBodyFromCacheRequest(exchange);
                log.info("开始请求 => URL[{}], 参数类型[json], 参数:[{}]", url, jsonParam);
            }
        } else {
            // 如果不是 JSON 请求，就提取查询参数进行记录
            MultiValueMap<String, String> parameterMap = request.getQueryParams();
            if (MapUtil.isNotEmpty(parameterMap)) {
                String parameters = JsonUtils.toJson(parameterMap);
                log.info("开始请求 => URL[{}], 参数类型[param], 参数:[{}]", url, parameters);
            } else {
                log.info("开始请求 => URL[{}], 无参数", url);
            }
        }

        // 在请求属性中记录开始时间，用于后续计算耗时
        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());

        // 继续处理请求链，完成后记录耗时
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute(START_TIME);
            if (Objects.nonNull(startTime)) {
                long executeTime = (System.currentTimeMillis() - startTime);
                log.info("结束请求 => URL[{}], 耗时:[{}]毫秒", url, executeTime);
            }
        }));
    }

    /**
     * 获取过滤器顺序
     * <p>
     * 日志处理器在负载均衡器之后执行, 负载均衡器会导致线程切换从而无法获取上下文内容
     * 如需在日志内操作线程上下文, 例如获取登录用户数据等, 可以使用下述 Order:
     *
     * <pre>{@code
     * return ReactiveLoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER - 1;
     * }</pre>
     *
     * @return 顺序值
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
