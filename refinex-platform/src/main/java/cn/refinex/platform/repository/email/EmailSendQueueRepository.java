package cn.refinex.platform.repository.email;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.platform.entity.email.EmailSendQueue;
import cn.refinex.common.utils.object.BeanConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 邮件发送队列 Repository
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class EmailSendQueueRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 插入队列任务
     *
     * @param queue 队列任务
     * @return 影响行数
     */
    public int insert(EmailSendQueue queue) {
        String sql = """
            INSERT INTO email_send_queue (
                id, queue_id, template_code, recipient_email, recipient_name,
                mail_subject, mail_content, mail_attachments, send_status, priority,
                retry_count, max_retry, schedule_time, error_message, send_time,
                create_by, create_time, update_by, update_time
            ) VALUES (
                :id, :queueId, :templateCode, :recipientEmail, :recipientName,
                :mailSubject, :mailContent, :mailAttachments, :sendStatus, :priority,
                :retryCount, :maxRetry, :scheduleTime, :errorMessage, :sendTime,
                :createBy, :createTime, :updateBy, :updateTime
            )
            """;

        Map<String, Object> params = BeanConverter.beanToMap(queue, false, false);
        return jdbcManager.insert(sql, params);
    }

    /**
     * 更新队列任务
     *
     * @param queue 队列任务
     * @return 影响行数
     */
    public int update(EmailSendQueue queue) {
        String sql = """
            UPDATE email_send_queue
            SET send_status = :sendStatus, retry_count = :retryCount,
                error_message = :errorMessage, send_time = :sendTime,
                update_time = :updateTime
            WHERE id = :id
            """;

        Map<String, Object> params = BeanConverter.beanToMap(queue, false, false);
        return jdbcManager.update(sql, params);
    }

    /**
     * 根据队列 ID 查询任务
     *
     * @param queueId 队列 ID
     * @return 队列任务
     */
    public EmailSendQueue findByQueueId(String queueId) {
        String sql = """
            SELECT * FROM email_send_queue
            WHERE queue_id = :queueId
            """;

        Map<String, Object> params = Map.of("queueId", queueId);
        return jdbcManager.queryObject(sql, params, EmailSendQueue.class);
    }

    /**
     * 查询待发送的队列任务（按优先级排序）
     *
     * @param limit 查询数量
     * @return 队列任务列表
     */
    public List<EmailSendQueue> findPendingTasks(int limit) {
        String sql = """
            SELECT * FROM email_send_queue
            WHERE send_status = 0 AND (schedule_time IS NULL OR schedule_time <= NOW())
            ORDER BY priority DESC, create_time ASC
            LIMIT :limit
            """;

        Map<String, Object> params = Map.of("limit", limit);
        return jdbcManager.queryList(sql, params, EmailSendQueue.class);
    }

    /**
     * 查询需要重试的失败任务
     *
     * @param limit 查询数量
     * @return 队列任务列表
     */
    public List<EmailSendQueue> findRetryTasks(int limit) {
        String sql = """
            SELECT * FROM email_send_queue
            WHERE send_status = 3
              AND retry_count < max_retry
            ORDER BY priority DESC, update_time ASC
            LIMIT :limit
            """;

        Map<String, Object> params = Map.of("limit", limit);
        return jdbcManager.queryList(sql, params, EmailSendQueue.class);
    }

    /**
     * 查询定时发送任务
     *
     * @param limit 查询数量
     * @return 队列任务列表
     */
    public List<EmailSendQueue> findScheduledTasks(int limit) {
        String sql = """
            SELECT * FROM email_send_queue
            WHERE send_status = 0
              AND schedule_time IS NOT NULL
              AND schedule_time <= NOW()
            ORDER BY schedule_time ASC, priority DESC
            LIMIT :limit
            """;

        Map<String, Object> params = Map.of("limit", limit);
        return jdbcManager.queryList(sql, params, EmailSendQueue.class);
    }

    /**
     * 更新任务状态
     *
     * @param queueId    队列 ID
     * @param sendStatus 发送状态
     * @return 影响行数
     */
    public int updateStatus(String queueId, Integer sendStatus) {
        String sql = """
            UPDATE email_send_queue
            SET send_status = :sendStatus, update_time = NOW()
            WHERE queue_id = :queueId
            """;

        Map<String, Object> params = Map.of("queueId", queueId, "sendStatus", sendStatus);
        return jdbcManager.update(sql, params);
    }

    /**
     * 更新任务为发送中状态
     *
     * @param queueId 队列 ID
     * @return 影响行数
     */
    public int updateToSending(String queueId) {
        String sql = """
            UPDATE email_send_queue
            SET send_status = 1, update_time = NOW()
            WHERE queue_id = :queueId AND send_status = 0
            """;

        Map<String, Object> params = Map.of("queueId", queueId);
        return jdbcManager.update(sql, params);
    }

    /**
     * 更新任务为发送成功状态
     *
     * @param queueId  队列 ID
     * @param sendTime 发送时间
     * @return 影响行数
     */
    public int updateToSent(String queueId, LocalDateTime sendTime) {
        String sql = """
            UPDATE email_send_queue
            SET send_status = 2, send_time = :sendTime, update_time = NOW()
            WHERE queue_id = :queueId
            """;

        Map<String, Object> params = Map.of("queueId", queueId, "sendTime", sendTime);
        return jdbcManager.update(sql, params);
    }

    /**
     * 更新任务为发送失败状态
     *
     * @param queueId      队列 ID
     * @param errorMessage 错误信息
     * @return 影响行数
     */
    public int updateToFailed(String queueId, String errorMessage) {
        String sql = """
            UPDATE email_send_queue
            SET send_status = 3, retry_count = retry_count + 1,
                error_message = :errorMessage, update_time = NOW()
            WHERE queue_id = :queueId
            """;

        Map<String, Object> params = Map.of("queueId", queueId, "errorMessage", errorMessage);
        return jdbcManager.update(sql, params);
    }

    /**
     * 统计待发送任务数量
     *
     * @return 任务数量
     */
    public int countPendingTasks() {
        String sql = """
            SELECT COUNT(*) FROM email_send_queue
            WHERE send_status = 0
            """;

        Integer count = jdbcManager.queryObject(sql, Map.of(), Integer.class);
        return count != null ? count : 0;
    }

    /**
     * 统计失败任务数量
     *
     * @return 任务数量
     */
    public int countFailedTasks() {
        String sql = """
            SELECT COUNT(*) FROM email_send_queue
            WHERE send_status = 3
            """;

        Integer count = jdbcManager.queryObject(sql, Map.of(), Integer.class);
        return count != null ? count : 0;
    }

    /**
     * 删除已发送的历史任务（超过指定天数）
     *
     * @param days 保留天数
     * @return 影响行数
     */
    public int deleteOldSentTasks(int days) {
        String sql = """
            DELETE FROM email_send_queue
            WHERE send_status = 2
              AND send_time < DATE_SUB(NOW(), INTERVAL :days DAY)
            """;

        Map<String, Object> params = Map.of("days", days);
        return jdbcManager.update(sql, params);
    }
}

