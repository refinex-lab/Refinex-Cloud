package cn.refinex.common.satoken.core.handler;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.enums.HttpStatusCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Sa-Token 异常处理器
 * <p>
 * 处理 Sa-Token 框架抛出的认证和授权相关异常，统一返回标准的 API 响应格式。
 *
 * @author Lion Li
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class SaTokenExceptionHandler {

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public ApiResult<Void> handleNotPermissionException(NotPermissionException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.warn("请求地址: {}, 权限校验失败: {}", requestURI, e.getMessage());
        return ApiResult.failure(HttpStatusCode.FORBIDDEN, "权限不足，无法访问该资源");
    }

    /**
     * 处理角色校验失败异常
     */
    @ExceptionHandler(NotRoleException.class)
    public ApiResult<Void> handleNotRoleException(NotRoleException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.warn("请求地址: {}, 角色校验失败: {}", requestURI, e.getMessage());
        return ApiResult.failure(HttpStatusCode.FORBIDDEN, "角色权限不足，无法访问该资源");
    }

    /**
     * 处理未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    public ApiResult<Void> handleNotLoginException(NotLoginException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.warn("请求地址: {}, 登录认证失败: {}", requestURI, e.getMessage());
        
        // 根据不同的登录异常类型返回不同的提示
        String message = switch (e.getType()) {
            case NotLoginException.NOT_TOKEN -> "未提供登录凭证，请先登录";
            case NotLoginException.INVALID_TOKEN -> "登录凭证无效，请重新登录";
            case NotLoginException.TOKEN_TIMEOUT -> "登录已过期，请重新登录";
            case NotLoginException.BE_REPLACED -> "账号已在其他设备登录，请重新登录";
            case NotLoginException.KICK_OUT -> "账号已被强制下线，请重新登录";
            default -> "身份验证失败，请先登录";
        };
        
        return ApiResult.failure(HttpStatusCode.UNAUTHORIZED, message);
    }

    /**
     * 处理账号被禁用异常
     */
    @ExceptionHandler(DisableServiceException.class)
    public ApiResult<Void> handleDisableServiceException(DisableServiceException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.warn("请求地址: {}, 账号被禁用: {}", requestURI, e.getMessage());
        return ApiResult.failure(HttpStatusCode.FORBIDDEN, "账号已被禁用，请联系管理员");
    }
}
