package cn.refinex.platform.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 验证码状态枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum EmailVerifyCodeStatus {

    /**
     * 未使用
     */
    UNUSED(0, "未使用"),

    /**
     * 已使用
     */
    USED(1, "已使用"),

    /**
     * 已过期
     */
    EXPIRED(2, "已过期"),

    /**
     * 已失效（主动作废）
     */
    INVALID(3, "已失效");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据状态码获取枚举
     *
     * @param code 状态码
     * @return 枚举值
     */
    public static EmailVerifyCodeStatus fromCode(Integer code) {
        for (EmailVerifyCodeStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的验证码状态：" + code);
    }
}

