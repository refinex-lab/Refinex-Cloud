package cn.refinex.ai.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI对话状态枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ConversationStatus {

    /**
     * 进行中
     */
    ACTIVE(1, "进行中"),

    /**
     * 已归档
     */
    ARCHIVED(2, "已归档"),

    /**
     * 已删除
     */
    DELETED(3, "已删除"),

    /**
     * 已过期
     */
    EXPIRED(4, "已过期");

    /**
     * 状态值
     */
    private final Integer value;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据状态值获取枚举
     *
     * @param value 状态值
     * @return 枚举
     */
    public static ConversationStatus fromValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (ConversationStatus status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否为活跃状态
     *
     * @return 是否活跃
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * 判断是否可以继续对话
     *
     * @return 是否可以继续对话
     */
    public boolean canContinue() {
        return this == ACTIVE;
    }
}

