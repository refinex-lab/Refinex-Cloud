package cn.refinex.ai.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务回调状态枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CallbackStatus {

    /**
     * 待回调
     */
    PENDING("PENDING", "待回调"),

    /**
     * 回调成功
     */
    SUCCESS("SUCCESS", "回调成功"),

    /**
     * 回调失败
     */
    FAILED("FAILED", "回调失败"),

    /**
     * 无需回调
     */
    NOT_REQUIRED("NOT_REQUIRED", "无需回调");

    /**
     * 状态代码
     */
    private final String code;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据代码获取枚举
     *
     * @param code 状态代码
     * @return 枚举
     */
    public static CallbackStatus fromCode(String code) {
        if (code == null) {
            return NOT_REQUIRED;
        }
        for (CallbackStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return NOT_REQUIRED;
    }

    /**
     * 判断是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return this == SUCCESS;
    }

    /**
     * 判断是否需要重试
     *
     * @return 是否需要重试
     */
    public boolean needsRetry() {
        return this == PENDING || this == FAILED;
    }
}

