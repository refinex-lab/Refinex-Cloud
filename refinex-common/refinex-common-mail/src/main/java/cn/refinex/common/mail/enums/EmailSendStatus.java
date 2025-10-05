package cn.refinex.common.mail.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 邮件发送状态枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum EmailSendStatus {

    /**
     * 待发送
     */
    PENDING(0, "待发送"),

    /**
     * 发送中
     */
    SENDING(1, "发送中"),

    /**
     * 已发送
     */
    SENT(2, "已发送"),

    /**
     * 发送失败
     */
    FAILED(3, "发送失败");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据状态码获取枚举
     *
     * @param code 状态码
     * @return 枚举
     */
    public static EmailSendStatus fromCode(Integer code) {
        for (EmailSendStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}

