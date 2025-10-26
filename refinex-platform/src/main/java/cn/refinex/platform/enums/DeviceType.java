package cn.refinex.platform.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备类型枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum DeviceType {

    PC("PC", "电脑"),
    MOBILE("Mobile", "手机"),
    TABLET("Tablet", "平板");

    private final String code;
    private final String info;

    /**
     * 根据代码获取枚举
     *
     * @param code 代码
     * @return 枚举
     */
    public static DeviceType fromCode(String code) {
        for (DeviceType type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        return PC; // 默认返回 PC
    }
}

