package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 菜单类型
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum MenuType {

    M("M", "目录"),
    C("C", "菜单"),
    F("F", "按钮"),
    ;

    private final String value;
    private final String description;
}
