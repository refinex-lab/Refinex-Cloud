package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 空间访问类型
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum SpaceAccessType {

    PRIVATE(0, "私有"),
    PUBLIC(1, "公开"),
    PASSWORD(2, "密码访问"),
    ;

    private final Integer value;
    private final String description;
}
