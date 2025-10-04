package cn.refinex.gateway.handler;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.exception.code.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关全局异常处理器
 * <p>
 * 说明：
 * 1. 处理 Sa-Token 异常
 * 2. 返回统一的 JSON 响应
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(-1) // 确保优先级最高
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

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
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        // 如果响应已提交，直接返回错误
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // 设置响应头
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 构建响应体
        ApiResult<?> result;

        if (ex instanceof NotLoginException notLoginException) {
            // 未登录异常
            log.warn("未登录或登录已过期：{}", notLoginException.getMessage());
            result = ApiResult.failure(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage());
        } else if (ex instanceof NotPermissionException notPermissionException) {
            // 权限不足异常
            log.warn("权限不足：{}", notPermissionException.getMessage());
            result = ApiResult.failure(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage());
        } else if (ex instanceof NotRoleException notRoleException) {
            // 角色不足异常
            log.warn("角色权限不足：{}", notRoleException.getMessage());
            result = ApiResult.failure(ResultCode.FORBIDDEN.getCode(), "角色权限不足");
        } else if (ex instanceof DisableServiceException disableServiceException) {
            // 服务封禁异常
            log.warn("账号已被封禁：{}", disableServiceException.getMessage());
            result = ApiResult.failure(ResultCode.FORBIDDEN.getCode(), "账号已被封禁");
        } else {
            // 其他异常
            log.error("网关异常", ex);
            result = ApiResult.failure(ResultCode.INTERNAL_ERROR.getCode(), ResultCode.INTERNAL_ERROR.getMessage());
        }

        // 写入响应
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            try {
                byte[] bytes = objectMapper.writeValueAsBytes(result);
                return bufferFactory.wrap(bytes);
            } catch (Exception e) {
                log.error("响应写入失败", e);
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }
}

