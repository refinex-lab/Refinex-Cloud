package cn.refinex.common.security.exception;

import cn.refinex.common.constants.ModuleConstants;
import cn.refinex.common.exception.BaseException;
import cn.refinex.common.exception.code.ErrorCode;
import cn.refinex.common.security.enums.SecurityErrorCode;

import java.io.Serial;

/**
 * 授权业务异常
 * <p>
 * 用于授权相关的业务异常，如权限不足、角色不足等
 * </p>
 *
 * @author Refinex
 * @since 2025-10-04
 */
public class AuthorizationException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模块标识
     */
    private static final String MODULE = ModuleConstants.MODULE_SECURITY;

    /**
     * 构造函数（使用异常码枚举）
     *
     * @param errorCode 异常码枚举
     */
    public AuthorizationException(ErrorCode errorCode) {
        super(MODULE, errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 构造函数（使用 SecurityErrorCode 枚举）
     *
     * @param errorCode 安全异常码枚举
     */
    public AuthorizationException(SecurityErrorCode errorCode) {
        super(MODULE, errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 构造函数（自定义异常码和消息）
     *
     * @param errorCode    异常码
     * @param errorMessage 异常信息
     */
    public AuthorizationException(String errorCode, String errorMessage) {
        super(MODULE, errorCode, errorMessage);
    }

    /**
     * 构造函数（使用异常码枚举 + 原因异常）
     *
     * @param errorCode 异常码枚举
     * @param cause     原因异常
     */
    public AuthorizationException(ErrorCode errorCode, Throwable cause) {
        super(MODULE, errorCode.getCode(), errorCode.getMessage(), cause);
    }

    /**
     * 构造函数（使用 SecurityErrorCode 枚举 + 原因异常）
     *
     * @param errorCode 安全异常码枚举
     * @param cause     原因异常
     */
    public AuthorizationException(SecurityErrorCode errorCode, Throwable cause) {
        super(MODULE, errorCode.getCode(), errorCode.getMessage(), cause);
    }

    /**
     * 构造函数（自定义异常码、消息 + 原因异常）
     *
     * @param errorCode    异常码
     * @param errorMessage 异常信息
     * @param cause        原因异常
     */
    public AuthorizationException(String errorCode, String errorMessage, Throwable cause) {
        super(MODULE, errorCode, errorMessage, cause);
    }
}

