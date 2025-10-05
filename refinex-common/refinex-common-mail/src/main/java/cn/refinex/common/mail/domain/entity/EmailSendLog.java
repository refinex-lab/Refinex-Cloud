package cn.refinex.common.mail.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 邮件发送日志实体
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
public class EmailSendLog {

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 关联的队列任务 ID
     */
    private String queueId;

    /**
     * 使用的模板编码
     */
    private String templateCode;

    /**
     * 收件人邮箱
     */
    private String recipientEmail;

    /**
     * 邮件主题
     */
    private String mailSubject;

    /**
     * 发送状态（0发送中、1成功、2失败）
     */
    private Integer sendStatus;

    /**
     * SMTP 服务器
     */
    private String smtpServer;

    /**
     * SMTP 响应信息
     */
    private String smtpResponse;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 处理耗时（单位：毫秒）
     */
    private Integer processDuration;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

