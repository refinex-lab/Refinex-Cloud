package cn.refinex.ai.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI流控类型枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RateLimitType {

    /**
     * 用户级别流控
     * 限制单个用户的请求频率
     */
    USER("USER", "用户级别"),

    /**
     * 模型级别流控
     * 限制特定模型的总体请求频率
     */
    MODEL("MODEL", "模型级别"),

    /**
     * 全局流控
     * 限制整个系统的请求频率
     */
    GLOBAL("GLOBAL", "全局级别"),

    /**
     * IP级别流控
     * 限制特定IP的请求频率
     */
    IP("IP", "IP级别"),

    /**
     * 租户级别流控
     * 限制特定租户的请求频率（多租户场景）
     */
    TENANT("TENANT", "租户级别"),

    /**
     * API接口级别流控
     * 限制特定API接口的请求频率
     */
    API("API", "接口级别");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据代码获取枚举
     *
     * @param code 类型代码
     * @return 枚举
     */
    public static RateLimitType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (RateLimitType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为细粒度流控
     *
     * @return 是否为细粒度
     */
    public boolean isFineGrained() {
        return this == USER || this == IP || this == TENANT;
    }

    /**
     * 判断是否为粗粒度流控
     *
     * @return 是否为粗粒度
     */
    public boolean isCoarseGrained() {
        return this == MODEL || this == GLOBAL || this == API;
    }
}

