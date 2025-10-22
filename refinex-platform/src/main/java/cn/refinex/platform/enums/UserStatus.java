package cn.refinex.platform.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserStatus {

    PENDING_ACTIVATION(0, "待激活"),
    NORMAL(1, "正常"),
    FROZEN(2, "冻结"),
    LOGGED_OUT(3, "注销"),
    ;

    private final Integer value;
    private final String description;
}
