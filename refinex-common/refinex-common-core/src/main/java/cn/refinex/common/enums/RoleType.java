package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色类型
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum RoleType {

    FRONTEND(0, "前台角色"),
    BACKEND(1, "后台角色"),
    ;

    private final Integer value;
    private final String description;
}
