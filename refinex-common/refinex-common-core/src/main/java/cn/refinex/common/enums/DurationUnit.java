package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 时间单位
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum DurationUnit {

    MONTH("month", "月"),
    QUARTER("quarter", "季"),
    YEAR("year", "年"),
    LIFETIME("lifetime", "终身"),
    ;

    private final String value;
    private final String description;
}
