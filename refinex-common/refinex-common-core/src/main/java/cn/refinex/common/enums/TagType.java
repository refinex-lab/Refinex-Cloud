package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 标签类型
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TagType {

    SYSTEM(0, "系统标签"),
    CUSTOM(1, "用户自定义标签"),
    ;

    private final Integer value;
    private final String description;
}
