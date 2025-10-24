package cn.refinex.api.platform.client.email.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 邮件发送结果 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailSendResponseDTO {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 队列任务 ID
     */
    private String queueId;

    /**
     * 收件人邮箱
     */
    private String recipientEmail;

    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 发送状态（0待发送、1发送中、2已发送、3失败）
     */
    private Integer sendStatus;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 处理耗时（单位：毫秒）
     */
    private Long processDuration;

    /**
     * SMTP 服务器
     */
    private String smtpServer;

    /**
     * SMTP 响应信息
     */
    private String smtpResponse;

    /**
     * 创建成功结果
     *
     * @param queueId        队列任务 ID
     * @param recipientEmail 收件人邮箱
     * @param subject        邮件主题
     * @return EmailSendResult
     */
    public static EmailSendResponseDTO success(String queueId, String recipientEmail, String subject) {
        return EmailSendResponseDTO.builder()
                .success(true)
                .queueId(queueId)
                .recipientEmail(recipientEmail)
                .subject(subject)
                .sendStatus(0)
                .sendTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败结果
     *
     * @param recipientEmail 收件人邮箱
     * @param subject        邮件主题
     * @param errorMessage   错误信息
     * @return EmailSendResult
     */
    public static EmailSendResponseDTO failure(String recipientEmail, String subject, String errorMessage) {
        return EmailSendResponseDTO.builder()
                .success(false)
                .recipientEmail(recipientEmail)
                .subject(subject)
                .sendStatus(3)
                .errorMessage(errorMessage)
                .sendTime(LocalDateTime.now())
                .build();
    }
}

