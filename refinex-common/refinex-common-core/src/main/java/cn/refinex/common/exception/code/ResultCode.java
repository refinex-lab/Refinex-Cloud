package cn.refinex.common.exception.code;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用基础状态码（HTTP 风格），承载非业务模块的通用码与提示信息。
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ResultCode implements ErrorCode {

    SUCCESS("200", "操作成功"),
    BAD_REQUEST("400", "请求参数错误"),
    UNAUTHORIZED("401", "未认证或已过期"),
    FORBIDDEN("403", "没有相关权限"),
    NOT_FOUND("404", "资源不存在"),
    METHOD_NOT_ALLOWED("405", "请求方法错误"),
    TOO_MANY_REQUESTS("429", "请求过于频繁，请稍后重试"),
    REPEATED_REQUESTS("409", "存在重复请求"),
    INTERNAL_ERROR("500", "系统内部异常");

    private final String code;
    private final String message;
}