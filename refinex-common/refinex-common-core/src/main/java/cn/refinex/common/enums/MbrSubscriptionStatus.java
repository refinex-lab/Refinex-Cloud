package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会员订阅状态
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum MbrSubscriptionStatus {

    TRIALING(0, "试用中"),
    ACTIVE(1, "正常"),
    EXPIRED(2, "已过期"),
    CANCELLED(3, "已取消"),
    REFUNDED(4, "已退款"),
    ;

    private final Integer value;
    private final String description;

    /**
     * 根据值获取枚举
     *
     * @param value 值
     * @return 枚举
     */
    public static MbrSubscriptionStatus of(Integer value) {
        for (MbrSubscriptionStatus status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
