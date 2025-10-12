package cn.refinex.common.satoken.core.handler;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.exception.code.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Sa-Token 异常处理器
 *
 * @author Lion Li
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class SaTokenExceptionHandler {

    @ExceptionHandler(NotPermissionException.class)
    public ApiResult<Void> handleNotPermissionException(NotPermissionException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址: {}, 权限校验失败: {}", requestURI, e.getMessage());
        return ApiResult.failure(ResultCode.FORBIDDEN);
    }

    @ExceptionHandler(NotRoleException.class)
    public ApiResult<Void> handleNotRoleException(NotRoleException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址: {}, 角色校验失败: {}", requestURI, e.getMessage());
        return ApiResult.failure(ResultCode.FORBIDDEN);
    }

    @ExceptionHandler(NotLoginException.class)
    public ApiResult<Void> handleNotLoginException(NotLoginException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址: {}, 登录认证失败: {}", requestURI, e.getMessage());
        return ApiResult.failure(ResultCode.UNAUTHORIZED);
    }

    @ExceptionHandler(DisableServiceException.class)
    public ApiResult<Void> handleDisableServiceException(DisableServiceException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址: {}, 服务禁用: {}", requestURI, e.getMessage());
        return ApiResult.failure(ResultCode.FORBIDDEN.getCode(), "服务已禁用");
    }
}
