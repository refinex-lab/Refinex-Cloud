package cn.refinex.platform.domain.entity.email;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 邮件发送队列实体
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
public class EmailSendQueue {

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 队列任务 ID（UUID 格式）
     */
    private String queueId;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 收件人邮箱
     */
    private String recipientEmail;

    /**
     * 收件人姓名
     */
    private String recipientName;

    /**
     * 邮件主题
     */
    private String mailSubject;

    /**
     * 邮件内容（HTML 格式）
     */
    private String mailContent;

    /**
     * 附件列表（JSON 格式）
     */
    private String mailAttachments;

    /**
     * 发送状态（0待发送、1发送中、2已发送、3失败）
     */
    private Integer sendStatus;

    /**
     * 优先级（0-9）
     */
    private Integer priority;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    private Integer maxRetry;

    /**
     * 定时发送时间
     */
    private LocalDateTime scheduleTime;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 实际发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 创建人 ID
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人 ID
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

