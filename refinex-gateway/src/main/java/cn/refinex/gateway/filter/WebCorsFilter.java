package cn.refinex.gateway.filter;

import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 跨域请求过滤器
 *
 * @author Lion Li
 * @since 1.0.0
 */
@Component
public class WebCorsFilter implements WebFilter, Ordered {

    /**
     * 允许的请求头，可以根据实际情况进行调整
     */
    private static final String ALLOWED_HEADERS =
            "X-Requested-With, Content-Language, Content-Type, " +
                    "Authorization, clientid, credential, X-XSRF-TOKEN, " +
                    "isToken, token, Admin-Token, App-Token, Encrypt-Key, isEncrypt";

    /**
     * 跨域请求过滤器
     *
     * @param exchange 服务器Web交换
     * @param chain    网关过滤器链
     * @return 响应 Mono
     */
    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 如果是跨域请求, 则直接过滤
        if (CorsUtils.isCorsRequest(request)) {
            ServerHttpResponse response = exchange.getResponse();
            HttpHeaders headers = response.getHeaders();

            // 添加允许的请求头
            headers.add("Access-Control-Allow-Headers", ALLOWED_HEADERS);
            // 添加允许的请求方法
            headers.add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,HEAD");
            // 添加允许的请求来源
            headers.add("Access-Control-Allow-Origin", "*");
            // 添加暴露的请求头
            headers.add("Access-Control-Expose-Headers", "*");
            // 添加预检请求的缓存时间, 单位: 秒, 这里设置为 18000 秒, 即 5 小时
            headers.add("Access-Control-Max-Age", "18000L");
            // 添加是否允许携带认证信息
            headers.add("Access-Control-Allow-Credentials", "true");

            // 处理预检请求的 OPTIONS 方法，直接返回成功状态码
            if (request.getMethod() == HttpMethod.OPTIONS) {
                response.setStatusCode(HttpStatus.OK);
                return Mono.empty();
            }
        }

        // 非跨域请求, 则继续过滤
        return chain.filter(exchange);
    }

    /**
     * 获取过滤器顺序
     *
     * @return 顺序值
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
