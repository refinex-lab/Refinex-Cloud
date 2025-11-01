package cn.refinex.ai.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI配额类型枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum QuotaType {

    /**
     * Token配额（按Token数量计费）
     */
    TOKEN("TOKEN", "Token配额", "tokens"),

    /**
     * 请求次数配额（按API调用次数计费）
     */
    REQUEST("REQUEST", "请求次数", "次"),

    /**
     * 金额配额（按消费金额计费）
     */
    AMOUNT("AMOUNT", "金额配额", "元"),

    /**
     * 时长配额（按使用时长计费，如语音合成的分钟数）
     */
    DURATION("DURATION", "时长配额", "分钟"),

    /**
     * 图片生成配额（按生成图片数量计费）
     */
    IMAGE("IMAGE", "图片配额", "张"),

    /**
     * 视频生成配额（按生成视频数量或时长计费）
     */
    VIDEO("VIDEO", "视频配额", "个"),

    /**
     * 存储空间配额（向量数据库存储空间）
     */
    STORAGE("STORAGE", "存储配额", "GB");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 单位
     */
    private final String unit;

    /**
     * 根据代码获取枚举
     *
     * @param code 类型代码
     * @return 枚举
     */
    public static QuotaType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (QuotaType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return TOKEN; // 默认返回Token配额
    }

    /**
     * 判断是否为计数型配额
     *
     * @return 是否为计数型
     */
    public boolean isCountable() {
        return this == TOKEN || this == REQUEST || this == IMAGE || this == VIDEO;
    }

    /**
     * 判断是否为金额型配额
     *
     * @return 是否为金额型
     */
    public boolean isMonetary() {
        return this == AMOUNT;
    }
}

