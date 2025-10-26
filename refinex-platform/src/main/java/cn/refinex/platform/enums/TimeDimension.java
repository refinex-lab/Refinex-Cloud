package cn.refinex.platform.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 时间维度枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TimeDimension {

    @Schema(description = "今日")
    TODAY("today"),

    @Schema(description = "本周")
    WEEK("week"),

    @Schema(description = "本月")
    MONTH("month"),

    @Schema(description = "本年")
    YEAR("year"),

    @Schema(description = "自定义")
    CUSTOM("custom");

    /**
     * 时间维度代码
     */
    private final String value;

    /**
     * 根据时间维度代码获取枚举值
     *
     * @param code 时间维度代码
     * @return 时间维度枚举值
     */
    public static TimeDimension fromCode(String code) {
        for (TimeDimension timeDimension : values()) {
            if (timeDimension.getValue().equals(code)) {
                return timeDimension;
            }
        }
        throw new IllegalArgumentException("Invalid time dimension code: " + code);
    }
}
