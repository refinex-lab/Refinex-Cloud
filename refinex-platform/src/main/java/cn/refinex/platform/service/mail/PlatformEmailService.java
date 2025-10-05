package cn.refinex.platform.service.mail;

import cn.refinex.common.mail.domain.dto.EmailSendRequest;
import cn.refinex.common.mail.domain.dto.EmailSendResult;
import cn.refinex.common.mail.service.EmailQueueService;
import cn.refinex.common.mail.service.EmailSendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Platform 邮件发送服务
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformEmailService {

    private final EmailSendService emailSendService;
    private final EmailQueueService emailQueueService;

    /**
     * 同步发送邮件
     *
     * @param request 发送请求
     * @return 发送结果
     */
    public EmailSendResult sendSync(EmailSendRequest request) {
        log.info("同步发送邮件: email={}, subject={}", request.getRecipientEmail(), request.getSubject());
        return emailSendService.sendSync(request);
    }

    /**
     * 异步发送邮件（入队）
     *
     * @param request 发送请求
     * @return 发送结果
     */
    public EmailSendResult sendAsync(EmailSendRequest request) {
        log.info("异步发送邮件（入队）: email={}, subject={}", request.getRecipientEmail(), request.getSubject());
        return emailQueueService.enqueue(request);
    }

    /**
     * 批量发送邮件
     *
     * @param requests 发送请求列表
     * @return 发送结果列表
     */
    public List<EmailSendResult> sendBatch(List<EmailSendRequest> requests) {
        log.info("批量发送邮件: count={}", requests.size());
        return emailSendService.sendBatch(requests);
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
        log.info("使用模板发送邮件: templateCode={}, email={}", templateCode, email);
        return emailSendService.sendWithTemplate(templateCode, email, variables);
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
        log.info("发送简单邮件: to={}, subject={}", to, subject);
        return emailSendService.sendSimple(to, subject, content);
    }

    /**
     * 取消队列任务
     *
     * @param queueId 队列 ID
     * @return 是否成功
     */
    public boolean cancelTask(String queueId) {
        log.info("取消队列任务: queueId={}", queueId);
        return emailQueueService.cancelTask(queueId);
    }

    /**
     * 重试失败任务
     *
     * @param queueId 队列 ID
     * @return 是否成功
     */
    public boolean retryTask(String queueId) {
        log.info("重试失败任务: queueId={}", queueId);
        return emailQueueService.retryFailedTask(queueId);
    }
}

