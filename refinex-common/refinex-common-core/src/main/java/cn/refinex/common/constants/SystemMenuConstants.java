package cn.refinex.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 系统菜单常量类
 *
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SystemMenuConstants {

    /**
     * 是否菜单外链(是)
     */
    public static final String YES_FRAME = "0";

    /**
     * 是否菜单外链(否)
     */
    public static final String NO_FRAME = "1";

    /**
     * 菜单类型(目录)
     */
    public static final String TYPE_DIR = "M";

    /**
     * 菜单类型(菜单)
     */
    public static final String TYPE_MENU = "C";

    /**
     * 菜单类型(按钮)
     */
    public static final String TYPE_BUTTON = "F";
}
