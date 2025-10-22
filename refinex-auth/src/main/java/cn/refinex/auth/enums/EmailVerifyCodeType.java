package cn.refinex.auth.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 邮箱验证码类型枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum EmailVerifyCodeType {

    REGISTER("REGISTER", "注册验证码"),
    LOGIN("LOGIN", "登录验证码"),
    RESET_PASSWORD("RESET_PASSWORD", "重置密码验证码"),
    CHANGE_EMAIL("CHANGE_EMAIL", "更换邮箱验证码");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型描述
     */
    private final String description;
}
