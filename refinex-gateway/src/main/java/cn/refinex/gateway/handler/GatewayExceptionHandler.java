package cn.refinex.gateway.handler;

import cn.refinex.gateway.utils.WebFluxUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关全局异常处理器
 *
 * @author ruoyi
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(-1) // 确保优先级最高
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    /**
     * 处理异常
     * <p>
     * 说明：
     * 1. 判断响应是否已提交
     * 2. 根据异常类型构建响应
     * 3. 写入响应体
     * </p>
     *
     * @param exchange ServerWebExchange 对象
     * @param ex       异常对象
     * @return Mono<Void>
     */
    @NonNull
    @Override
    public Mono<Void> handle(ServerWebExchange exchange,@NonNull Throwable ex) {
        // 获取响应对象
        ServerHttpResponse response = exchange.getResponse();

        // 如果响应已提交，直接返回错误
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // 提取异常信息
        String msg;
        if (ex instanceof NotFoundException) {
            msg = "服务未找到";
        } else if (ex instanceof ResponseStatusException responseStatusException) {
            msg = responseStatusException.getMessage();
        } else {
            msg = "内部服务器错误";
        }

        log.error("[网关异常处理] 请求路径：{}，异常信息：{}", exchange.getRequest().getURI(), ex.getMessage());

        return WebFluxUtils.webFluxResponseWriter(response, msg);
    }
}

