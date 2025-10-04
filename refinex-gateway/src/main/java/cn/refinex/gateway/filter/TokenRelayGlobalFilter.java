package cn.refinex.gateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Token 转发全局过滤器
 * <p>
 * 说明：
 * 1. 从请求中获取 Token
 * 2. 将 Token 添加到请求头
 * 3. 转发到下游服务
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
public class TokenRelayGlobalFilter implements GlobalFilter, Ordered {

    /**
     * Token 请求头名称
     */
    private static final String TOKEN_HEADER_NAME = "Authorization";

    /**
     * 过滤器逻辑
     * <p>
     * 说明：
     * 1. 尝试获取 Token
     * 2. 如果 Token 存在，添加到请求头
     * 3. 继续过滤器链
     * </p>
     *
     * @param exchange ServerWebExchange 对象
     * @param chain    GatewayFilterChain 对象
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取 Token
        String token = null;
        try {
            token = StpUtil.getTokenValue();
        } catch (Exception e) {
            log.debug("未获取到 Token：{}", e.getMessage());
        }

        // 如果 Token 存在，添加到请求头
        if (StringUtils.isNotBlank(token)) {
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header(TOKEN_HEADER_NAME, token)
                    .build();
            exchange = exchange.mutate().request(request).build();
            log.debug("Token 已转发到下游服务");
        }

        return chain.filter(exchange);
    }

    /**
     * 过滤器优先级
     * <p>
     * 说明：优先级较高，确保在其他过滤器之前执行
     * </p>
     *
     * @return 优先级值
     */
    @Override
    public int getOrder() {
        return -100;
    }
}

