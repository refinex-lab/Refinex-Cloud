package cn.refinex.auth.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 客户端类型枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ClientTypeEnum {

    WEB_ADMIN("web_admin"),
    MOBILE_ADMIN("mobile_admin");

    private final String code;

    /**
     * 根据代码获取枚举
     *
     * @param code 类型代码
     * @return 客户端类型枚举
     */
    public static ClientTypeEnum fromCode(String code) {
        for (ClientTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的客户端类型: " + code);
    }
}
