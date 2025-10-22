package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录类型枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum LoginType {

    PASSWORD(1, "密码登录"),
    EMAIL(2, "邮箱登录");

    private final Integer code;
    private final String info;

    /**
     * 根据状态码获取枚举
     *
     * @param code 状态码
     * @return 枚举
     */
    public static LoginType fromCode(Integer code) {
        for (LoginType status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的登录类型：" + code);
    }
}
