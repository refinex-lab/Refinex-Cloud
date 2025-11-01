package cn.refinex.ai.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI消息角色枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum MessageRole {

    /**
     * 系统消息（用于设置AI行为和上下文）
     */
    SYSTEM("SYSTEM", "系统"),

    /**
     * 用户消息
     */
    USER("USER", "用户"),

    /**
     * AI助手消息
     */
    ASSISTANT("ASSISTANT", "助手"),

    /**
     * 函数调用消息
     */
    FUNCTION("FUNCTION", "函数"),

    /**
     * 工具调用消息
     */
    TOOL("TOOL", "工具");

    /**
     * 角色代码
     */
    private final String code;

    /**
     * 角色描述
     */
    private final String description;

    /**
     * 根据代码获取枚举
     *
     * @param code 角色代码
     * @return 枚举
     */
    public static MessageRole fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (MessageRole role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }

    /**
     * 判断是否为用户消息
     *
     * @return 是否为用户消息
     */
    public boolean isUser() {
        return this == USER;
    }

    /**
     * 判断是否为AI消息
     *
     * @return 是否为AI消息
     */
    public boolean isAssistant() {
        return this == ASSISTANT;
    }

    /**
     * 判断是否为系统消息
     *
     * @return 是否为系统消息
     */
    public boolean isSystem() {
        return this == SYSTEM;
    }
}

