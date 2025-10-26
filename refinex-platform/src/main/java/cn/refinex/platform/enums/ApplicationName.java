package cn.refinex.platform.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应用名称枚举
 * 对应系统中各个微服务的名称
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ApplicationName {

    @Schema(description = "认证服务")
    AUTH_SERVICE("refinex-auth"),

    @Schema(description = "平台服务")
    PLATFORM_SERVICE("refinex-platform"),

    @Schema(description = "知识库服务")
    KB_SERVICE("refinex-kb"),

    @Schema(description = "AI服务")
    AI_SERVICE("refinex-ai");

    /**
     * 应用名称代码
     */
    private final String value;

    /**
     * 根据应用名称代码获取枚举值
     *
     * @param code 应用名称代码
     * @return 应用名称枚举值
     */
    public static ApplicationName fromCode(String code) {
        for (ApplicationName applicationName : values()) {
            if (applicationName.getValue().equals(code)) {
                return applicationName;
            }
        }
        throw new IllegalArgumentException("Invalid application name code: " + code);
    }

    /**
     * 检查应用名称代码是否有效
     *
     * @param code 应用名称代码
     * @return 是否有效
     */
    public static boolean isValid(String code) {
        for (ApplicationName applicationName : values()) {
            if (applicationName.getValue().equals(code)) {
                return true;
            }
        }
        return false;
    }
}

