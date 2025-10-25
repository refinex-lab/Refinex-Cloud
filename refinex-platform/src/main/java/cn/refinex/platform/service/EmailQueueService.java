package cn.refinex.platform.service;

import cn.refinex.platform.controller.email.dto.request.EmailSendRequestDTO;
import cn.refinex.platform.controller.email.dto.response.EmailSendResponseDTO;

/**
 * 邮件队列服务
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface EmailQueueService {

    /**
     * 入队
     *
     * @param request 发送请求
     * @return 发送结果
     */
    EmailSendResponseDTO enqueue(EmailSendRequestDTO request);

    /**
     * 处理待发送任务
     *
     * @return 处理数量
     */
    int processPendingTasks();

    /**
     * 处理定时任务
     *
     * @return 处理数量
     */
    int processScheduledTasks();

    /**
     * 重试失败任务
     *
     * @param queueId 队列 ID
     * @return 是否成功
     */
    boolean retryFailedTask(String queueId);

    /**
     * 取消任务
     *
     * @param queueId 队列 ID
     * @return 是否成功
     */
    boolean cancelTask(String queueId);

    /**
     * 重试符合条件的失败任务
     * <p>
     * 查询满足重试条件的失败任务并重新处理
     * </p>
     *
     * @return 重试数量
     */
    int retryEligibleTasks();

    /**
     * 统计待发送任务数量
     *
     * @return 任务数量
     */
    int countPendingTasks();

    /**
     * 统计失败任务数量
     *
     * @return 任务数量
     */
    int countFailedTasks();
}
