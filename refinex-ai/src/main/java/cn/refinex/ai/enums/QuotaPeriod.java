package cn.refinex.ai.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 配额周期枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum QuotaPeriod {

    /**
     * 日配额（每天重置）
     */
    DAILY("DAILY", "日配额", 1),

    /**
     * 周配额（每周重置）
     */
    WEEKLY("WEEKLY", "周配额", 7),

    /**
     * 月配额（每月重置）
     */
    MONTHLY("MONTHLY", "月配额", 30),

    /**
     * 季度配额（每季度重置）
     */
    QUARTERLY("QUARTERLY", "季度配额", 90),

    /**
     * 年配额（每年重置）
     */
    YEARLY("YEARLY", "年配额", 365),

    /**
     * 永久配额（不重置）
     */
    UNLIMITED("UNLIMITED", "永久配额", -1);

    /**
     * 周期代码
     */
    private final String code;

    /**
     * 周期描述
     */
    private final String description;

    /**
     * 周期天数（-1表示永久）
     */
    private final Integer days;

    /**
     * 根据代码获取枚举
     *
     * @param code 周期代码
     * @return 枚举
     */
    public static QuotaPeriod fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (QuotaPeriod period : values()) {
            if (period.getCode().equals(code)) {
                return period;
            }
        }
        return MONTHLY; // 默认月配额
    }

    /**
     * 判断是否为永久配额
     *
     * @return 是否为永久
     */
    public boolean isUnlimited() {
        return this == UNLIMITED;
    }

    /**
     * 判断是否需要定期重置
     *
     * @return 是否需要重置
     */
    public boolean needsReset() {
        return this != UNLIMITED;
    }
}

