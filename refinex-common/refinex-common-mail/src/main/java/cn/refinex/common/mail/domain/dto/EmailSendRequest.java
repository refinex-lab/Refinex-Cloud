package cn.refinex.common.mail.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 邮件发送请求 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailSendRequest {

    /**
     * 收件人邮箱（必填）
     */
    @NotBlank(message = "收件人邮箱不能为空")
    @Email(message = "收件人邮箱格式不正确")
    private String recipientEmail;

    /**
     * 收件人姓名
     */
    private String recipientName;

    /**
     * 邮件主题（必填）
     */
    @NotBlank(message = "邮件主题不能为空")
    private String subject;

    /**
     * 邮件内容（必填）
     */
    @NotBlank(message = "邮件内容不能为空")
    private String content;

    /**
     * 是否 HTML 格式（默认 true）
     */
    @Builder.Default
    private Boolean isHtml = true;

    /**
     * 附件列表
     */
    private List<EmailAttachment> attachments;

    /**
     * 优先级（0-9，默认 5）
     */
    @Builder.Default
    private Integer priority = 5;

    /**
     * 定时发送时间
     */
    private LocalDateTime scheduleTime;

    /**
     * SMTP 配置 ID（不指定则使用默认配置）
     */
    private String smtpConfigId;

    /**
     * 模板编码（使用模板发送时必填）
     */
    private String templateCode;

    /**
     * 模板变量（使用模板发送时必填）
     */
    private Map<String, Object> templateVariables;

    /**
     * 邮件附件 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailAttachment {

        /**
         * 附件名称
         */
        @NotBlank(message = "附件名称不能为空")
        private String fileName;

        /**
         * 附件内容（Base64 编码）
         */
        private String fileContent;

        /**
         * 附件 URL（与 fileContent 二选一）
         */
        private String fileUrl;

        /**
         * 附件 MIME 类型
         */
        private String contentType;
    }
}

