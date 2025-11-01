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
     * 健康（模型正常运行）
     */
    HEALTHY("HEALTHY", "健康"),

    /**
     * 亚健康（模型可用但性能下降）
     */
    DEGRADED("DEGRADED", "亚健康"),

    /**
     * 不健康（模型不可用）
     */
    UNHEALTHY("UNHEALTHY", "不健康"),

    /**
     * 未知（未进行健康检查或检查失败）
     */
    UNKNOWN("UNKNOWN", "未知");

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
    public static HealthStatus fromCode(String code) {
        if (code == null) {
            return UNKNOWN;
        }
        for (HealthStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return UNKNOWN;
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
        return this == HEALTHY || this == DEGRADED;
    }

    /**
     * 判断是否需要告警
     *
     * @return 是否需要告警
     */
    public boolean needsAlert() {
        return this == UNHEALTHY || this == DEGRADED;
    }
}

