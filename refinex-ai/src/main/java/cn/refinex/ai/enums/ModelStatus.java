package cn.refinex.ai.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI模型状态枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ModelStatus {

    /**
     * 启用
     */
    ENABLED(1, "启用"),

    /**
     * 禁用
     */
    DISABLED(0, "禁用"),

    /**
     * 维护中
     */
    MAINTENANCE(2, "维护中"),

    /**
     * 已下线
     */
    OFFLINE(3, "已下线");

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
    public static ModelStatus fromValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (ModelStatus status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否为启用状态
     *
     * @return 是否启用
     */
    public boolean isEnabled() {
        return this == ENABLED;
    }
}

