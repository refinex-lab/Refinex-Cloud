package cn.refinex.platform.exception;

import cn.refinex.common.exception.code.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DESC
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PlatformErrorCode implements ErrorCode {

    // ==================== 业务异常（1000-1999）====================

    USERNAME_EXIST("PLATFORM-1000", "用户名已存在"),
    PHONE_EXIST("PLATFORM-1001", "手机号已存在"),
    PASSWORD_STRENGTH("PLATFORM-1002", "密码强度不足，必须包含字母、数字和特殊字符"),

    // ==================== 系统异常（2000-2999） ====================

    ;

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误信息
     */
    private final String message;
}
