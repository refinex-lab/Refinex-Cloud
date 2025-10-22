package cn.refinex.auth.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserStatusEnum {

    PENDING_ACTIVATION(0, "待激活"),
    NORMAL(1, "正常"),
    FROZEN(2, "冻结"),
    LOGGED_OUT(3, "注销");

    private final Integer value;
    private final String description;

    /**
     * 根据状态码获取枚举
     *
     * @param code 状态码
     * @return 枚举
     */
    public static UserStatusEnum fromCode(Integer code) {
        for (UserStatusEnum status : values()) {
            if (status.getValue().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
