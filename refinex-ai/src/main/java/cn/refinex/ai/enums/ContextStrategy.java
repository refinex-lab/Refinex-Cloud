package cn.refinex.ai.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI对话上下文策略枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ContextStrategy {

    /**
     * 滑动窗口策略
     * 保留最近N条消息作为上下文
     */
    SLIDING_WINDOW("SLIDING_WINDOW", "滑动窗口"),

    /**
     * 摘要策略
     * 对历史消息进行摘要，保留关键信息
     */
    SUMMARIZE("SUMMARIZE", "智能摘要"),

    /**
     * 截断策略
     * 超出token限制时直接截断早期消息
     */
    TRUNCATE("TRUNCATE", "直接截断"),

    /**
     * 完整保留策略
     * 保留所有历史消息（适用于短对话）
     */
    FULL_CONTEXT("FULL_CONTEXT", "完整保留"),

    /**
     * 关键信息提取策略
     * 提取并保留关键实体和事实
     */
    KEY_EXTRACTION("KEY_EXTRACTION", "关键提取");

    /**
     * 策略代码
     */
    private final String code;

    /**
     * 策略描述
     */
    private final String description;

    /**
     * 根据代码获取枚举
     *
     * @param code 策略代码
     * @return 枚举
     */
    public static ContextStrategy fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (ContextStrategy strategy : values()) {
            if (strategy.getCode().equals(code)) {
                return strategy;
            }
        }
        return SLIDING_WINDOW; // 默认返回滑动窗口策略
    }

    /**
     * 判断是否需要处理历史消息
     *
     * @return 是否需要处理
     */
    public boolean needsProcessing() {
        return this == SUMMARIZE || this == KEY_EXTRACTION;
    }
}

