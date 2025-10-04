package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 视频质量等级
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum QualityLevel {

    Q360("360P"),
    Q480("480P"),
    Q720("720P"),
    Q1080("1080P"),
    Q4K("4K"),
    ;

    private final String value;
}
