package cn.refinex.ai.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI内容审核状态枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ModerationStatus {

    /**
     * 待审核
     */
    PENDING("PENDING", "待审核"),

    /**
     * 审核通过
     */
    APPROVED("APPROVED", "审核通过"),

    /**
     * 审核拒绝
     */
    REJECTED("REJECTED", "审核拒绝"),

    /**
     * 需要人工审核
     */
    MANUAL_REVIEW("MANUAL_REVIEW", "人工审核");

    /**
     * 状态代码
     */
    private final String code;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据代码获取枚举
     *
     * @param code 状态代码
     * @return 枚举
     */
    public static ModerationStatus fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (ModerationStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return PENDING;
    }

    /**
     * 判断是否通过审核
     *
     * @return 是否通过
     */
    public boolean isApproved() {
        return this == APPROVED;
    }

    /**
     * 判断是否被拒绝
     *
     * @return 是否被拒绝
     */
    public boolean isRejected() {
        return this == REJECTED;
    }

    /**
     * 判断是否需要人工介入
     *
     * @return 是否需要人工介入
     */
    public boolean needsManualReview() {
        return this == MANUAL_REVIEW;
    }
}

