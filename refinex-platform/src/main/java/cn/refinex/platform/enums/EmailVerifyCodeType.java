package cn.refinex.platform.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 验证码类型枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum EmailVerifyCodeType {

    /**
     * 注册验证码
     */
    REGISTER("REGISTER", "注册验证码"),

    /**
     * 登录验证码
     */
    LOGIN("LOGIN", "登录验证码"),

    /**
     * 重置密码验证码
     */
    RESET_PASSWORD("RESET_PASSWORD", "重置密码验证码"),

    /**
     * 更换邮箱验证码
     */
    CHANGE_EMAIL("CHANGE_EMAIL", "更换邮箱验证码");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据代码获取枚举
     *
     * @param code 类型代码
     * @return 枚举值
     */
    public static EmailVerifyCodeType fromCode(String code) {
        for (EmailVerifyCodeType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的验证码类型：" + code);
    }
}

