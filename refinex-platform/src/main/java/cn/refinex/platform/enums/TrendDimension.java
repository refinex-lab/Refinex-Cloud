package cn.refinex.platform.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 趋势维度枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TrendDimension {

    @Schema(description = "按小时")
    HOUR("hour"),

    @Schema(description = "按天")
    DAY("day"),

    @Schema(description = "按周")
    WEEK("week"),

    @Schema(description = "按月")
    MONTH("month"),

    @Schema(description = "按年")
    YEAR("year"),
    ;

    /**
     * 趋势维度代码
     */
    private final String value;

    /**
     * 根据趋势维度代码获取枚举值
     *
     * @param code 趋势维度代码
     * @return 趋势维度枚举值
     */
    public static TrendDimension fromCode(String code) {
        for (TrendDimension trendDimension : values()) {
            if (trendDimension.getValue().equals(code)) {
                return trendDimension;
            }
        }

        throw new IllegalArgumentException("Invalid trend dimension code: " + code);
    }
}
