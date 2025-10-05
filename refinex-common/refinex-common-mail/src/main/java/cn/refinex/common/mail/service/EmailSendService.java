package cn.refinex.common.mail.service;

import cn.hutool.core.util.RandomUtil;
import cn.refinex.common.mail.config.properties.MailProperties;
import cn.refinex.common.mail.config.properties.SmtpConfig;
import cn.refinex.common.mail.domain.dto.EmailSendRequest;
import cn.refinex.common.mail.domain.dto.EmailSendResult;
import cn.refinex.common.mail.domain.entity.EmailSendLog;
import cn.refinex.common.mail.domain.entity.EmailTemplate;
import cn.refinex.common.mail.enums.EmailSendStatus;
import cn.refinex.common.mail.exception.EmailErrorCode;
import cn.refinex.common.mail.exception.EmailException;
import cn.refinex.common.mail.repository.EmailSendLogRepository;
import cn.refinex.common.utils.Fn;
import cn.refinex.common.utils.algorithm.SnowflakeIdGenerator;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 邮件发送服务
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSendService {

    private final Map<String, JavaMailSender> javaMailSenderMap;
    private final MailProperties mailProperties;
    private final EmailTemplateService templateService;
    private final EmailSendLogRepository sendLogRepository;
    private final SnowflakeIdGenerator idGenerator;

    /**
     * 同步发送邮件
     *
     * @param request 发送请求
     * @return 发送结果
     */
    public EmailSendResult sendSync(EmailSendRequest request) {
        long startTime = System.currentTimeMillis();
        String queueId = Fn.getUuid32();

        try {
            // 1. 获取 JavaMailSender
            String smtpConfigId = request.getSmtpConfigId() != null 
                    ? request.getSmtpConfigId() 
                    : mailProperties.getDefaultSmtp();
            JavaMailSender mailSender = javaMailSenderMap.get(smtpConfigId);
            if (Objects.isNull(mailSender)) {
                log.error("SMTP 配置不存在: {}", smtpConfigId);
                throw new EmailException(EmailErrorCode.SMTP_CONFIG_NOT_FOUND);
            }

            // 2. 准备邮件内容
            String subject = request.getSubject();
            String content = request.getContent();

            // 3. 如果使用模板，渲染模板
            if (StringUtils.isNotBlank(request.getTemplateCode())) {
                EmailTemplate template = templateService.getTemplateByCode(request.getTemplateCode());
                subject = template.getTemplateSubject();
                content = templateService.renderTemplateContent(template.getTemplateContent(), request.getTemplateVariables());
            }

            // 4. 创建邮件消息
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            // 5. 设置发件人
            SmtpConfig smtpConfig = getSmtpConfig(smtpConfigId);
            String from = smtpConfig.getFromName() != null 
                    ? smtpConfig.getFromName() + " <" + smtpConfig.getFrom() + ">"
                    : smtpConfig.getFrom();
            helper.setFrom(from);

            // 6. 设置收件人
            helper.setTo(request.getRecipientEmail());

            // 7. 设置主题和内容
            helper.setSubject(subject);
            helper.setText(content, request.getIsHtml());

            // 8. 发送邮件
            mailSender.send(message);

            // 9. 计算耗时
            long duration = System.currentTimeMillis() - startTime;

            // 10. 记录发送日志
            saveSendLog(queueId, request, smtpConfig, true, null, duration);

            log.info("邮件发送成功: email={}, subject={}, duration={}ms", request.getRecipientEmail(), subject, duration);

            return EmailSendResult.builder()
                    .success(true)
                    .queueId(queueId)
                    .recipientEmail(request.getRecipientEmail())
                    .subject(subject)
                    .sendStatus(EmailSendStatus.SENT.getCode())
                    .sendTime(LocalDateTime.now())
                    .processDuration(duration)
                    .smtpServer(smtpConfig.getHost())
                    .build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("邮件发送失败: email={}", request.getRecipientEmail(), e);

            // 记录失败日志
            saveSendLog(queueId, request, null, false, e.getMessage(), duration);

            return EmailSendResult.builder()
                    .success(false)
                    .queueId(queueId)
                    .recipientEmail(request.getRecipientEmail())
                    .subject(request.getSubject())
                    .sendStatus(EmailSendStatus.FAILED.getCode())
                    .errorMessage(e.getMessage())
                    .sendTime(LocalDateTime.now())
                    .processDuration(duration)
                    .build();
        }
    }

    /**
     * 批量发送邮件
     *
     * @param requests 发送请求列表
     * @return 发送结果列表
     */
    public List<EmailSendResult> sendBatch(List<EmailSendRequest> requests) {
        log.info("批量发送邮件: count={}", requests.size());
        return requests.stream()
                .map(this::sendSync)
                .toList();
    }

    /**
     * 使用模板发送邮件
     *
     * @param templateCode 模板编码
     * @param email        收件人邮箱
     * @param variables    模板变量
     * @return 发送结果
     */
    public EmailSendResult sendWithTemplate(String templateCode, String email, Map<String, Object> variables) {
        EmailSendRequest request = EmailSendRequest.builder()
                .recipientEmail(email)
                .templateCode(templateCode)
                .templateVariables(variables)
                .build();

        return sendSync(request);
    }

    /**
     * 发送简单邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     * @return 发送结果
     */
    public EmailSendResult sendSimple(String to, String subject, String content) {
        EmailSendRequest request = EmailSendRequest.builder()
                .recipientEmail(to)
                .subject(subject)
                .content(content)
                .isHtml(false)
                .build();

        return sendSync(request);
    }

    /**
     * 获取 SMTP 配置
     *
     * @param smtpConfigId SMTP 配置 ID
     * @return SMTP 配置
     */
    private SmtpConfig getSmtpConfig(String smtpConfigId) {
        return mailProperties.getSmtpConfigs().stream()
                .filter(config -> config.getConfigId().equals(smtpConfigId))
                .findFirst()
                .orElseThrow(() -> new EmailException(EmailErrorCode.SMTP_CONFIG_NOT_FOUND));
    }

    /**
     * 保存发送日志
     *
     * @param queueId      队列 ID
     * @param request      发送请求
     * @param smtpConfig   SMTP 配置
     * @param success      是否成功
     * @param errorMessage 错误信息
     * @param duration     耗时
     */
    private void saveSendLog(String queueId, EmailSendRequest request, SmtpConfig smtpConfig, boolean success, String errorMessage, long duration) {
        try {
            EmailSendLog log = new EmailSendLog();
            log.setId(idGenerator.nextId());
            log.setQueueId(queueId);
            log.setTemplateCode(request.getTemplateCode());
            log.setRecipientEmail(request.getRecipientEmail());
            log.setMailSubject(request.getSubject());
            log.setSendStatus(success ? EmailSendStatus.SENT.getCode() : EmailSendStatus.FAILED.getCode());
            log.setSmtpServer(Objects.nonNull(smtpConfig) ? smtpConfig.getHost() : null);
            log.setErrorMessage(errorMessage);
            log.setSendTime(LocalDateTime.now());
            log.setProcessDuration(Fn.getInt(duration, 0));
            log.setCreateTime(LocalDateTime.now());

            sendLogRepository.insert(log);
        } catch (Exception e) {
            log.error("保存发送日志失败", e);
        }
    }
}

