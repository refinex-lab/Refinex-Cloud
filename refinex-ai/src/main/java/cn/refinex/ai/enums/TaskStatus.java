package cn.refinex.ai.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI生成任务状态枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TaskStatus {

    /**
     * 待处理（任务已创建，等待执行）
     */
    PENDING("PENDING", "待处理"),

    /**
     * 排队中（任务在队列中等待）
     */
    QUEUED("QUEUED", "排队中"),

    /**
     * 处理中（任务正在执行）
     */
    PROCESSING("PROCESSING", "处理中"),

    /**
     * 已完成（任务成功完成）
     */
    COMPLETED("COMPLETED", "已完成"),

    /**
     * 失败（任务执行失败）
     */
    FAILED("FAILED", "失败"),

    /**
     * 已取消（任务被用户或系统取消）
     */
    CANCELLED("CANCELLED", "已取消"),

    /**
     * 超时（任务执行超时）
     */
    TIMEOUT("TIMEOUT", "超时"),

    /**
     * 部分成功（任务部分完成，部分失败）
     */
    PARTIAL_SUCCESS("PARTIAL_SUCCESS", "部分成功");

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
    public static TaskStatus fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (TaskStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否为终态（不会再变化的状态）
     *
     * @return 是否为终态
     */
    public boolean isFinalState() {
        return this == COMPLETED 
            || this == FAILED 
            || this == CANCELLED 
            || this == TIMEOUT 
            || this == PARTIAL_SUCCESS;
    }

    /**
     * 判断是否为进行中状态
     *
     * @return 是否进行中
     */
    public boolean isInProgress() {
        return this == PENDING || this == QUEUED || this == PROCESSING;
    }

    /**
     * 判断是否为成功状态
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return this == COMPLETED || this == PARTIAL_SUCCESS;
    }
}

