package cn.refinex.common.mail.exception;

import cn.refinex.common.constants.ModuleConstants;
import cn.refinex.common.exception.BusinessException;

import java.io.Serial;

/**
 * 邮件业务异常
 *
 * @author Refinex
 * @since 1.0.0
 */
public class EmailException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模块标识
     */
    private static final String MODULE = ModuleConstants.MODULE_MAIL;

    /**
     * 构造方法
     *
     * @param errorCode 错误码枚举
     */
    public EmailException(EmailErrorCode errorCode) {
        super(MODULE, errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 构造方法（自定义错误信息）
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误信息
     */
    public EmailException(EmailErrorCode errorCode, String message) {
        super(MODULE, errorCode.getCode(), message);
    }

    /**
     * 构造方法（带原始异常）
     *
     * @param errorCode 错误码枚举
     * @param cause     原始异常
     */
    public EmailException(EmailErrorCode errorCode, Throwable cause) {
        super(MODULE, errorCode.getCode(), errorCode.getMessage(), cause);
    }

    /**
     * 构造方法（自定义错误信息 + 原始异常）
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误信息
     * @param cause     原始异常
     */
    public EmailException(EmailErrorCode errorCode, String message, Throwable cause) {
        super(MODULE, errorCode.getCode(), message, cause);
    }
}

