package cn.refinex.ai.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI模型健康状态枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum HealthStatus {

    /**
     * 不健康（模型不可用）
     */
    UNHEALTHY(0, "不健康"),

    /**
     * 健康（模型正常运行）
     */
    HEALTHY(1, "健康");

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
    public static HealthStatus fromValue(Integer value) {
        if (value == null) {
            return HEALTHY; // 默认为健康
        }
        for (HealthStatus status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return HEALTHY;
    }

    /**
     * 判断是否健康
     *
     * @return 是否健康
     */
    public boolean isHealthy() {
        return this == HEALTHY;
    }

    /**
     * 判断是否可用
     *
     * @return 是否可用
     */
    public boolean isAvailable() {
        return this == HEALTHY;
    }

    /**
     * 判断是否需要告警
     *
     * @return 是否需要告警
     */
    public boolean needsAlert() {
        return this == UNHEALTHY;
    }
}

