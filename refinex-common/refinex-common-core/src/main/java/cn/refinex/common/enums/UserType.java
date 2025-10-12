package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户类型
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum UserType {

    /**
     * 后台系统用户
     */
    SYS_USER("sys_user"),

    /**
     * 移动应用用户
     */
    APP_USER("app_user");

    /**
     * 用户类型代码
     */
    private final String code;

    /**
     * 根据代码获取枚举
     *
     * @param code 类型代码
     * @return 用户类型枚举
     */
    public static UserType fromCode(String code) {
        for (UserType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的用户类型: " + code);
    }

}
