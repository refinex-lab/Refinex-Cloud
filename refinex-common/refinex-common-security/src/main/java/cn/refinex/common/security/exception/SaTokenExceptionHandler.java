package cn.refinex.common.security.exception;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.security.enums.SecurityErrorCode;
import cn.refinex.common.security.util.ExceptionLogUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Sa-Token 异常处理器
 * <p>
 * 统一处理 Sa-Token 抛出的各类异常，返回统一的响应格式
 * </p>
 *
 * @author Refinex
 * @since 2025-10-04
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SaTokenExceptionHandler {

    @Autowired(required = false)
    private HttpServletRequest request;

    /**
     * 处理未登录异常
     *
     * @param e 未登录异常
     * @return 统一响应
     */
    @ExceptionHandler(NotLoginException.class)
    public ApiResult<Void> handleNotLoginException(NotLoginException e) {
        ExceptionLogUtils.logSecurityException(e, request);

        // 根据不同的未登录类型返回不同的提示
        SecurityErrorCode errorCode = switch (e.getType()) {
            case NotLoginException.NOT_TOKEN -> SecurityErrorCode.NOT_TOKEN;
            case NotLoginException.INVALID_TOKEN -> SecurityErrorCode.INVALID_TOKEN;
            case NotLoginException.TOKEN_TIMEOUT -> SecurityErrorCode.TOKEN_TIMEOUT;
            case NotLoginException.BE_REPLACED -> SecurityErrorCode.BE_REPLACED;
            case NotLoginException.KICK_OUT -> SecurityErrorCode.KICK_OUT;
            default -> SecurityErrorCode.NOT_LOGIN;
        };

        return ApiResult.failure(errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 处理权限不足异常
     *
     * @param e 权限不足异常
     * @return 统一响应
     */
    @ExceptionHandler(NotPermissionException.class)
    public ApiResult<Void> handleNotPermissionException(NotPermissionException e) {
        ExceptionLogUtils.logSecurityException(e, request);

        String permission = e.getPermission();
        String message = String.format("权限不足，需要权限: %s", permission);

        log.warn("权限不足异常: 缺少权限 [{}]", permission);
        return ApiResult.failure(SecurityErrorCode.NOT_PERMISSION.getCode(), message);
    }

    /**
     * 处理角色不足异常
     *
     * @param e 角色不足异常
     * @return 统一响应
     */
    @ExceptionHandler(NotRoleException.class)
    public ApiResult<Void> handleNotRoleException(NotRoleException e) {
        ExceptionLogUtils.logSecurityException(e, request);

        String role = e.getRole();
        String message = String.format("角色权限不足，需要角色: %s", role);

        log.warn("角色不足异常: 缺少角色 [{}]", role);
        return ApiResult.failure(SecurityErrorCode.NOT_ROLE.getCode(), message);
    }

    /**
     * 处理账号封禁异常
     *
     * @param e 账号封禁异常
     * @return 统一响应
     */
    @ExceptionHandler(DisableServiceException.class)
    public ApiResult<Void> handleDisableServiceException(DisableServiceException e) {
        ExceptionLogUtils.logSecurityException(e, request);

        long disableTime = e.getDisableTime();
        String message;

        if (disableTime == -1) {
            message = "账号已被永久封禁";
        } else {
            long minutes = disableTime / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (days > 0) {
                message = String.format("账号已被封禁，剩余时间: %d天", days);
            } else if (hours > 0) {
                message = String.format("账号已被封禁，剩余时间: %d小时", hours);
            } else if (minutes > 0) {
                message = String.format("账号已被封禁，剩余时间: %d分钟", minutes);
            } else {
                message = String.format("账号已被封禁，剩余时间: %d秒", disableTime);
            }
        }

        log.warn("账号封禁异常: 服务 [{}] 已被封禁，封禁等级 [{}]，剩余时间 [{}]秒", e.getService(), e.getLevel(), disableTime);

        return ApiResult.failure(SecurityErrorCode.DISABLE_SERVICE.getCode(), message);
    }

    /**
     * 处理 Sa-Token 通用异常
     *
     * @param e Sa-Token 异常
     * @return 统一响应
     */
    @ExceptionHandler(SaTokenException.class)
    public ApiResult<Void> handleSaTokenException(SaTokenException e) {
        ExceptionLogUtils.logSecurityExceptionWithStackTrace(e, request);

        log.error("Sa-Token 异常", e);
        return ApiResult.failure(SecurityErrorCode.AUTH_SERVICE_ERROR.getCode(), "认证服务异常: " + e.getMessage());
    }

    /**
     * 处理认证业务异常
     *
     * @param e 认证业务异常
     * @return 统一响应
     */
    @ExceptionHandler(AuthenticationException.class)
    public ApiResult<Void> handleAuthenticationException(AuthenticationException e) {
        ExceptionLogUtils.logSecurityException(e, request);

        log.warn("认证业务异常: {}", e.getMessage());
        return ApiResult.failure(e.getErrorCode(), e.getErrorMessage());
    }

    /**
     * 处理授权业务异常
     *
     * @param e 授权业务异常
     * @return 统一响应
     */
    @ExceptionHandler(AuthorizationException.class)
    public ApiResult<Void> handleAuthorizationException(AuthorizationException e) {
        ExceptionLogUtils.logSecurityException(e, request);

        log.warn("授权业务异常: {}", e.getMessage());
        return ApiResult.failure(e.getErrorCode(), e.getErrorMessage());
    }
}

