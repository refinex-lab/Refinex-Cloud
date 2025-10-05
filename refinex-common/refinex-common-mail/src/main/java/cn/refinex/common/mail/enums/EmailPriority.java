package cn.refinex.common.mail.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 邮件优先级枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum EmailPriority {

    /**
     * 紧急（优先级 9）
     */
    URGENT(9, "紧急"),

    /**
     * 高（优先级 7）
     */
    HIGH(7, "高"),

    /**
     * 普通（优先级 5）
     */
    NORMAL(5, "普通"),

    /**
     * 低（优先级 3）
     */
    LOW(3, "低");

    /**
     * 优先级值（0-9，数字越大优先级越高）
     */
    private final Integer value;

    /**
     * 优先级描述
     */
    private final String description;

    /**
     * 根据值获取枚举
     *
     * @param value 优先级值
     * @return 枚举值
     */
    public static EmailPriority fromValue(Integer value) {
        for (EmailPriority priority : values()) {
            if (priority.getValue().equals(value)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("未知的邮件优先级：" + value);
    }
}

