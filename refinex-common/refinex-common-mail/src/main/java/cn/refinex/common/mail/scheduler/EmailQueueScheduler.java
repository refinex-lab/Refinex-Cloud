package cn.refinex.common.mail.scheduler;

import cn.refinex.common.mail.config.properties.MailProperties;
import cn.refinex.common.mail.service.EmailQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 邮件队列调度器
 * <p>
 * 负责定时处理邮件队列中的任务
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "refinex.mail", name = "enabled", havingValue = "true")
public class EmailQueueScheduler {

    private final EmailQueueService queueService;
    private final MailProperties mailProperties;

    /**
     * 处理待发送邮件
     * <p>
     * 定时扫描待发送队列，处理待发送的邮件
     * </p>
     */
    @Scheduled(fixedDelayString = "${refinex.mail.queue.scan-interval-seconds:10}000")
    public void processPendingEmails() {
        if (Boolean.FALSE.equals(mailProperties.getEnabled())) {
            return;
        }

        try {
            log.debug("开始处理待发送邮件任务");
            int processedCount = queueService.processPendingTasks();
            
            if (processedCount > 0) {
                log.info("处理待发送邮件任务完成，处理数量: {}", processedCount);
            }
        } catch (Exception e) {
            log.error("处理待发送邮件任务失败", e);
        }
    }

    /**
     * 处理定时邮件
     * <p>
     * 定时扫描定时发送队列，处理到期的定时邮件
     * </p>
     */
    @Scheduled(fixedDelayString = "${refinex.mail.queue.scheduled-scan-interval-seconds:30}000")
    public void processScheduledEmails() {
        if (Boolean.FALSE.equals(mailProperties.getEnabled())) {
            return;
        }

        try {
            log.debug("开始处理定时邮件任务");
            int processedCount = queueService.processScheduledTasks();
            
            if (processedCount > 0) {
                log.info("处理定时邮件任务完成，处理数量: {}", processedCount);
            }
        } catch (Exception e) {
            log.error("处理定时邮件任务失败", e);
        }
    }

    /**
     * 重试失败邮件
     * <p>
     * 定时扫描失败队列，重试符合条件的失败邮件
     * </p>
     */
    @Scheduled(fixedDelayString = "${refinex.mail.queue.retry-scan-interval-seconds:300}000")
    public void retryFailedEmails() {
        if (Boolean.FALSE.equals(mailProperties.getEnabled())) {
            return;
        }

        try {
            log.debug("开始重试失败邮件任务");
            int retriedCount = queueService.retryEligibleTasks();

            if (retriedCount > 0) {
                log.info("重试失败邮件任务完成，重试数量: {}", retriedCount);
            }
        } catch (Exception e) {
            log.error("重试失败邮件任务失败", e);
        }
    }
}

