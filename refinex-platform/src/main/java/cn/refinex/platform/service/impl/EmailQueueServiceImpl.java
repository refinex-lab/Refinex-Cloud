package cn.refinex.platform.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.refinex.api.platform.client.email.dto.request.EmailSendRequestDTO;
import cn.refinex.api.platform.client.email.dto.response.EmailSendResponseDTO;
import cn.refinex.common.exception.SystemException;
import cn.refinex.common.json.utils.JsonUtils;
import cn.refinex.common.mail.config.properties.MailProperties;
import cn.refinex.common.utils.algorithm.SnowflakeIdGenerator;
import cn.refinex.platform.constants.EmailErrorMessageConstants;
import cn.refinex.platform.domain.entity.email.EmailSendQueue;
import cn.refinex.platform.enums.EmailSendStatus;
import cn.refinex.platform.repository.email.EmailSendQueueRepository;
import cn.refinex.platform.service.EmailQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 邮件队列服务实现类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailQueueServiceImpl implements EmailQueueService {

    private final EmailSendQueueRepository queueRepository;
    private final EmailSendServiceImpl emailSendService;
    private final MailProperties mailProperties;
    private final SnowflakeIdGenerator idGenerator;

    /**
     * 入队
     *
     * @param request 发送请求
     * @return 发送结果
     */
    @Override
    public EmailSendResponseDTO enqueue(EmailSendRequestDTO request) {
        try {
            // 1. 创建队列任务
            EmailSendQueue queue = new EmailSendQueue();
            queue.setId(idGenerator.nextId());
            queue.setQueueId(RandomUtil.randomString(36));
            queue.setTemplateCode(request.getTemplateCode());
            queue.setRecipientEmail(request.getRecipientEmail());
            queue.setRecipientName(request.getRecipientName());
            queue.setMailSubject(request.getSubject());
            queue.setMailContent(request.getContent());
            queue.setSendStatus(EmailSendStatus.PENDING.getCode());
            queue.setPriority(request.getPriority() != null ? request.getPriority() : 5);
            queue.setRetryCount(0);
            queue.setMaxRetry(mailProperties.getRetry().getMaxAttempts());
            queue.setScheduleTime(request.getScheduleTime());
            queue.setCreateTime(LocalDateTime.now());
            queue.setUpdateTime(LocalDateTime.now());

            // 2. 转换附件列表为 JSON
            if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
                queue.setMailAttachments(JsonUtils.toJson(request.getAttachments()));
            }

            // 3. 插入队列
            int rows = queueRepository.insert(queue);
            if (rows <= 0) {
                log.error("入队失败: email={}", request.getRecipientEmail());
                return EmailSendResponseDTO.failure(
                        request.getRecipientEmail(),
                        request.getSubject(),
                        "入队失败"
                );
            }

            log.info("入队成功: queueId={}, email={}", queue.getQueueId(), request.getRecipientEmail());
            return EmailSendResponseDTO.success(
                    queue.getQueueId(),
                    request.getRecipientEmail(),
                    request.getSubject()
            );
        } catch (Exception e) {
            log.error("入队异常: email={}", request.getRecipientEmail(), e);
            return EmailSendResponseDTO.failure(
                    request.getRecipientEmail(),
                    request.getSubject(),
                    e.getMessage()
            );
        }
    }

    /**
     * 处理待发送任务
     *
     * @return 处理数量
     */
    @Override
    public int processPendingTasks() {
        // 1. 查询待发送任务
        int batchSize = mailProperties.getQueue().getBatchSize();
        List<EmailSendQueue> pendingTasks = queueRepository.findPendingTasks(batchSize);

        if (CollectionUtils.isEmpty(pendingTasks)) {
            log.debug("没有待发送任务");
            return 0;
        }

        log.info("开始处理待发送任务，数量: {}", pendingTasks.size());

        // 2. 逐个处理
        int successCount = 0;
        for (EmailSendQueue task : pendingTasks) {
            try {
                processTask(task);
                successCount++;
            } catch (Exception e) {
                log.error("处理任务失败: queueId={}", task.getQueueId(), e);
            }
        }

        log.info("处理待发送任务完成，成功: {}/{}", successCount, pendingTasks.size());
        return successCount;
    }

    /**
     * 处理定时任务
     *
     * @return 处理数量
     */
    @Override
    public int processScheduledTasks() {
        // 1. 查询定时发送任务
        int batchSize = mailProperties.getQueue().getBatchSize();
        List<EmailSendQueue> scheduledTasks = queueRepository.findScheduledTasks(batchSize);

        if (scheduledTasks.isEmpty()) {
            log.debug("没有定时发送任务");
            return 0;
        }

        log.info("开始处理定时发送任务，数量: {}", scheduledTasks.size());

        // 2. 逐个处理
        int successCount = 0;
        for (EmailSendQueue task : scheduledTasks) {
            try {
                processTask(task);
                successCount++;
            } catch (Exception e) {
                log.error("处理定时任务失败: queueId={}", task.getQueueId(), e);
            }
        }

        log.info("处理定时发送任务完成，成功: {}/{}", successCount, scheduledTasks.size());
        return successCount;
    }

    /**
     * 处理单个任务
     *
     * @param task 队列任务
     */
    private void processTask(EmailSendQueue task) {
        // 1. 更新为发送中状态
        int rows = queueRepository.updateToSending(task.getQueueId());
        if (rows <= 0) {
            log.warn("任务已被其他线程处理: queueId={}", task.getQueueId());
            return;
        }

        // 2. 构建发送请求
        EmailSendRequestDTO request = EmailSendRequestDTO.builder()
                .recipientEmail(task.getRecipientEmail())
                .recipientName(task.getRecipientName())
                .subject(task.getMailSubject())
                .content(task.getMailContent())
                .templateCode(task.getTemplateCode())
                .build();

        // 3. 发送邮件
        EmailSendResponseDTO result = emailSendService.sendSync(request);

        // 4. 更新任务状态
        if (Boolean.TRUE.equals(result.getSuccess())) {
            queueRepository.updateToSent(task.getQueueId(), LocalDateTime.now());
            log.info("任务发送成功: queueId={}", task.getQueueId());
        } else {
            queueRepository.updateToFailed(task.getQueueId(), result.getErrorMessage());
            log.error("任务发送失败: queueId={}, error={}", task.getQueueId(), result.getErrorMessage());
        }
    }

    /**
     * 重试失败任务
     *
     * @param queueId 队列 ID
     * @return 是否成功
     */
    @Override
    public boolean retryFailedTask(String queueId) {
        // 1. 查询任务
        EmailSendQueue task = queueRepository.findByQueueId(queueId);
        if (task == null) {
            log.error("任务不存在: queueId={}", queueId);
            throw new SystemException(EmailErrorMessageConstants.QUEUE_TASK_NOT_FOUND);
        }

        // 2. 检查任务状态
        if (!EmailSendStatus.FAILED.getCode().equals(task.getSendStatus())) {
            log.error("任务状态不是失败: queueId={}, status={}", queueId, task.getSendStatus());
            return false;
        }

        // 3. 检查重试次数
        if (task.getRetryCount() >= task.getMaxRetry()) {
            log.error("任务已达到最大重试次数: queueId={}, retryCount={}", queueId, task.getRetryCount());
            return false;
        }

        // 4. 重置状态为待发送
        int rows = queueRepository.updateStatus(queueId, EmailSendStatus.PENDING.getCode());
        if (rows <= 0) {
            log.error("重置任务状态失败: queueId={}", queueId);
            return false;
        }

        log.info("重试任务成功: queueId={}", queueId);
        return true;
    }

    /**
     * 取消任务
     *
     * @param queueId 队列 ID
     * @return 是否成功
     */
    @Override
    public boolean cancelTask(String queueId) {
        // 1. 查询任务
        EmailSendQueue task = queueRepository.findByQueueId(queueId);
        if (task == null) {
            log.error("任务不存在: queueId={}", queueId);
            throw new SystemException(EmailErrorMessageConstants.QUEUE_TASK_NOT_FOUND);
        }

        // 2. 只能取消待发送的任务
        if (!EmailSendStatus.PENDING.getCode().equals(task.getSendStatus())) {
            log.error("只能取消待发送的任务: queueId={}, status={}", queueId, task.getSendStatus());
            return false;
        }

        // 3. 更新为失败状态
        int rows = queueRepository.updateToFailed(queueId, "任务已取消");
        if (rows <= 0) {
            log.error("取消任务失败: queueId={}", queueId);
            return false;
        }

        log.info("取消任务成功: queueId={}", queueId);
        return true;
    }

    /**
     * 重试符合条件的失败任务
     * <p>
     * 查询满足重试条件的失败任务并重新处理
     * </p>
     *
     * @return 重试数量
     */
    @Override
    public int retryEligibleTasks() {
        // 1. 查询可重试的失败任务
        int batchSize = mailProperties.getQueue().getBatchSize();
        List<EmailSendQueue> retryTasks = queueRepository.findRetryTasks(batchSize);

        if (retryTasks.isEmpty()) {
            log.debug("没有需要重试的失败任务");
            return 0;
        }

        log.info("开始重试失败任务，数量: {}", retryTasks.size());

        // 2. 逐个重试
        int successCount = 0;
        for (EmailSendQueue task : retryTasks) {
            try {
                // 检查重试次数
                if (task.getRetryCount() >= task.getMaxRetry()) {
                    log.warn("任务已达到最大重试次数，跳过: queueId={}, retryCount={}",
                            task.getQueueId(), task.getRetryCount());
                    continue;
                }

                // 重置状态为待发送
                int rows = queueRepository.updateStatus(task.getQueueId(), EmailSendStatus.PENDING.getCode());
                if (rows > 0) {
                    log.info("重试任务: queueId={}, retryCount={}/{}",
                            task.getQueueId(), task.getRetryCount() + 1, task.getMaxRetry());
                    successCount++;
                }
            } catch (Exception e) {
                log.error("重试任务失败: queueId={}", task.getQueueId(), e);
            }
        }

        log.info("重试失败任务完成，成功: {}/{}", successCount, retryTasks.size());
        return successCount;
    }

    /**
     * 统计待发送任务数量
     *
     * @return 任务数量
     */
    @Override
    public int countPendingTasks() {
        return queueRepository.countPendingTasks();
    }

    /**
     * 统计失败任务数量
     *
     * @return 任务数量
     */
    @Override
    public int countFailedTasks() {
        return queueRepository.countFailedTasks();
    }
}

