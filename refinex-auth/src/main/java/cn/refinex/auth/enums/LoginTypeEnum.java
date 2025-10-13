package cn.refinex.auth.enums;

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
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum LoginTypeEnum {

    PASSWORD(1, "密码登录"),
    EMAIL(2, "邮箱登录");

    /**
     * 登录类型编码
     */
    private final Integer code;

    /**
     * 登录类型描述
     */
    private final String description;

    /**
     * 根据登录类型编码获取枚举
     *
     * @param code 登录类型编码
     * @return 登录类型枚举
     */
    public static LoginTypeEnum fromCode(Integer code) {
        for (LoginTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的登录类型: " + code);
    }
}
