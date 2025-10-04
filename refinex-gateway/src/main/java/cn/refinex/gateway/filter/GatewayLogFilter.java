package cn.refinex.gateway.filter;

import cn.refinex.gateway.properties.GatewaySecurityProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关日志过滤器
 * <p>
 * 说明：
 * 1. 记录请求日志
 * 2. 记录响应日志
 * 3. 记录请求耗时
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayLogFilter implements GlobalFilter, Ordered {

    private final GatewaySecurityProperties securityProperties;

    /**
     * 过滤器逻辑
     * <p>
     * 说明：
     * 1. 如果日志未启用，直接跳过
     * 2. 记录请求信息
     * 3. 记录响应信息和耗时
     * </p>
     *
     * @param exchange ServerWebExchange 对象
     * @param chain    GatewayFilterChain 对象
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 如果日志未启用，直接跳过
        if (Boolean.FALSE.equals(securityProperties.getLogEnabled())) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();

        log.info("Gateway 请求：{} {}", method, path);

        long startTime = System.currentTimeMillis();

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            ServerHttpResponse response = exchange.getResponse();
            log.info("Gateway 响应：{} {} - {} - {}ms", method, path, response.getStatusCode(), duration);
        }));
    }

    /**
     * 过滤器优先级
     * <p>
     * 说明：最高优先级，确保最先执行
     * </p>
     *
     * @return 优先级值
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

