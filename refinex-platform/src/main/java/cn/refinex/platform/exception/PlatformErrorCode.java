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
    // 字典相关错误码（1003-1015 保留区间）
    DICT_TYPE_CODE_EXIST("PLATFORM-1003", "字典类型编码已存在"),
    DICT_TYPE_NOT_FOUND("PLATFORM-1004", "字典类型不存在"),
    DICT_TYPE_DISABLED("PLATFORM-1005", "字典类型已停用"),
    DICT_DATA_VALUE_EXIST("PLATFORM-1006", "字典数据值已存在"),
    DICT_DATA_LABEL_EXIST("PLATFORM-1007", "字典数据标签已存在"),
    DICT_DATA_NOT_FOUND("PLATFORM-1008", "字典数据不存在"),
    DICT_DATA_CONFLICT("PLATFORM-1009", "字典数据存在冲突"),
    DICT_TYPE_IN_USE("PLATFORM-1010", "字典类型已被使用，无法删除"),

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
