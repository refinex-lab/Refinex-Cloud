package cn.refinex.gateway.config.filter;

import cn.refinex.gateway.utils.WebFluxUtils;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 缓存请求过滤器
 * <p>
 * 用于在 Spring Cloud Gateway 环境下缓存 JSON 请求体，解决 WebFlux 模式下请求体（body）只能被读取一次的问题。
 *
 * @author Lion Li
 * @since 1.0.0
 */
@Component
public class WebCacheRequestFilter implements WebFilter, Ordered {

    /**
     * 过滤并缓存 JSON 请求体，避免后续读取失败。
     *
     * @param exchange 当前请求上下文对象（不能为空）
     * @param chain    过滤器链（不能为空）
     * @return 异步响应结果 Mono<Void>
     */
    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        // 非 JSON 请求, 直接过滤
        if (!WebFluxUtils.isJsonRequest(exchange)) {
            return chain.filter(exchange);
        }

        // 对 JSON 请求体进行缓存，使请求体可以重复读取
        return ServerWebExchangeUtils.cacheRequestBody(exchange, serverHttpRequest -> {
            if (serverHttpRequest == exchange.getRequest()) {
                // 未发生请求包装，直接继续过滤
                return chain.filter(exchange);
            }
            // 将缓存后的请求重新设置到 exchange 中
            return chain.filter(exchange.mutate().request(serverHttpRequest).build());
        });
    }

    /**
     * 获取过滤器顺序
     * <p>
     * 此处设置为 {@link Ordered#HIGHEST_PRECEDENCE} + 1，确保在大多数过滤器之前执行，以便尽早缓存请求体。
     *
     * @return 顺序值
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
