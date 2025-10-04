package cn.refinex.common.jdbc.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 日志格式类型
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum LogFormatType {

    /**
     * 文本格式
     */
    TEXT("text"),

    /**
     * JSON格式
     */
    JSON("json"),

    ;

    private final String value;
}
