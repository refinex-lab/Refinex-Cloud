package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 转码状态
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TranscodeStatus {

    PENDING(0, "待转码"),
    PROCESSING(1, "转码中"),
    SUCCESS(2, "转码完成"),
    FAILED(3, "转码失败"),
    ;

    private final Integer value;
    private final String description;
}
