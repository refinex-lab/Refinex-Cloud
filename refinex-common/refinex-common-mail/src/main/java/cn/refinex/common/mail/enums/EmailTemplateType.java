package cn.refinex.common.mail.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 邮件模板类型枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum EmailTemplateType {

    /**
     * HTML 模板
     */
    HTML("HTML", "HTML 模板"),

    /**
     * 纯文本模板
     */
    TEXT("TEXT", "纯文本模板");

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
     * @return 枚举值
     */
    public static EmailTemplateType fromCode(String code) {
        for (EmailTemplateType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的邮件模板类型：" + code);
    }
}

