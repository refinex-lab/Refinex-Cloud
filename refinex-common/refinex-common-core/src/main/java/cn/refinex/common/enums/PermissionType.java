package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 权限类型
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PermissionType {

    MENU("menu", "菜单"),
    BUTTON("button", "按钮"),
    API("api", "接口"),
    ;

    private final String value;
    private final String description;
}
