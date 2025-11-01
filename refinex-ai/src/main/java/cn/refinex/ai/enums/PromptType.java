package cn.refinex.ai.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 提示词类型枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PromptType {

    /**
     * 系统提示词（内置）
     */
    SYSTEM("SYSTEM", "系统提示词"),

    /**
     * 用户自定义提示词
     */
    USER("USER", "用户自定义"),

    /**
     * 官方推荐提示词
     */
    OFFICIAL("OFFICIAL", "官方推荐"),

    /**
     * 社区分享提示词
     */
    COMMUNITY("COMMUNITY", "社区分享");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据代码获取枚举
     *
     * @param code 类型代码
     * @return 枚举
     */
    public static PromptType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (PromptType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return USER;
    }

    /**
     * 判断是否为系统提示词
     *
     * @return 是否为系统提示词
     */
    public boolean isSystem() {
        return this == SYSTEM;
    }

    /**
     * 判断是否为用户自定义
     *
     * @return 是否为用户自定义
     */
    public boolean isUser() {
        return this == USER;
    }

    /**
     * 判断是否可编辑
     *
     * @return 是否可编辑
     */
    public boolean isEditable() {
        return this == USER;
    }
}

