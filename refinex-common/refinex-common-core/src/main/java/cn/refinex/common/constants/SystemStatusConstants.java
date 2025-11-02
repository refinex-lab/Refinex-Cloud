package cn.refinex.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 系统状态常量类
 *
 * @author Lion Li
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SystemStatusConstants {

    /**
     * 正常状态
     */
    public static final String NORMAL = "1";
    public static final Integer NORMAL_VALUE = 1;

    /**
     * 异常状态
     */
    public static final String DISABLE = "0";
    public static final Integer DISABLE_VALUE = 0;

    /**
     * 是否为系统默认(是)
     */
    public static final String YES = "Y";
}
