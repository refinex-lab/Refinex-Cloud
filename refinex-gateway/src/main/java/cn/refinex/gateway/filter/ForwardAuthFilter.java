package cn.refinex.gateway.filter;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.same.SaSameUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 转发认证过滤器 (内部服务外网隔离)
 *
 * @author Lion Li
 * @since 1.0.0
 */
@Component
public class ForwardAuthFilter implements GlobalFilter, Ordered {

    /**
     * 转发认证过滤器
     *
     * @param exchange 服务器Web交换
     * @param chain    网关过滤器链
     * @return 响应 Mono
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 检查是否开启了相同 Token 校验
        Boolean checkSameToken = SaManager.getConfig().getCheckSameToken();

        // 如果未开启相同 Token 校验，则直接放行
        if (Boolean.FALSE.equals(checkSameToken)) {
            return chain.filter(exchange);
        }

        // 构造一个新的请求，为其追加 Same-Token 参数
        ServerHttpRequest newRequest = exchange
                .getRequest()
                .mutate()
                // 在请求头追加 Same-Token 参数
                .header(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken())
                .build();

        // 用新的请求创建一个新的交换对象，继续向下传递
        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
        return chain.filter(newExchange);
    }

    /**
     * 获取过滤器顺序
     *
     * @return 顺序值
     */
    @Override
    public int getOrder() {
        return -100;
    }
}
