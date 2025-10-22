package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 内容访问类型
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ContentAccessType {

    INHERIT("0", "继承空间"),
    PRIVATE("1", "自定义私有"),
    PUBLIC("2", "自定义公开"),
    ;

    private final String value;
    private final String description;
}
