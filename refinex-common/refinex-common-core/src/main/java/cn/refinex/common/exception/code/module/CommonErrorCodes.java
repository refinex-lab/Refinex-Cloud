package cn.refinex.common.exception.code.module;

import cn.refinex.common.exception.code.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * COMMON 模块业务错误码（示例）
 * <p>
 * 采用 MODULE-NNNN 四位数字编码，提供中文提示信息。
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum CommonErrorCodes implements ErrorCode {

    PARAM_ERROR("COMMON-1001", "参数错误"),
    NOT_LOGIN("COMMON-1002", "未登录"),
    NO_AUTH("COMMON-1003", "无权限"),
    RESOURCE_NOT_FOUND("COMMON-2001", "资源不存在"),
    SYSTEM_ERROR("COMMON-5000", "系统内部异常");

    private final String code;
    private final String message;
}