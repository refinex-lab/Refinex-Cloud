package cn.refinex.platform.repository.email;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.platform.domain.entity.email.EmailSendLog;
import cn.refinex.common.utils.object.BeanConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 邮件发送日志 Repository
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class EmailSendLogRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 插入发送日志
     *
     * @param log 发送日志
     * @return 影响行数
     */
    public int insert(EmailSendLog log) {
        String sql = """
            INSERT INTO email_send_log (
                id, queue_id, template_code, recipient_email, mail_subject,
                send_status, smtp_server, smtp_response, error_message,
                send_time, process_duration, create_time
            ) VALUES (
                :id, :queueId, :templateCode, :recipientEmail, :mailSubject,
                :sendStatus, :smtpServer, :smtpResponse, :errorMessage,
                :sendTime, :processDuration, :createTime
            )
            """;

        Map<String, Object> params = BeanConverter.beanToMap(log, false, false);
        return jdbcManager.insert(sql, params);
    }

    /**
     * 根据队列 ID 查询发送日志列表
     *
     * @param queueId 队列 ID
     * @return 发送日志列表
     */
    public List<EmailSendLog> findByQueueId(String queueId) {
        String sql = """
            SELECT * FROM email_send_log
            WHERE queue_id = :queueId
            ORDER BY create_time DESC
            """;

        Map<String, Object> params = Map.of("queueId", queueId);
        return jdbcManager.queryList(sql, params, EmailSendLog.class);
    }

    /**
     * 根据收件人邮箱查询发送日志
     *
     * @param recipientEmail 收件人邮箱
     * @param limit          查询数量
     * @return 发送日志列表
     */
    public List<EmailSendLog> findByRecipientEmail(String recipientEmail, int limit) {
        String sql = """
            SELECT * FROM email_send_log
            WHERE recipient_email = :recipientEmail
            ORDER BY create_time DESC
            LIMIT :limit
            """;

        Map<String, Object> params = Map.of("recipientEmail", recipientEmail, "limit", limit);
        return jdbcManager.queryList(sql, params, EmailSendLog.class);
    }

    /**
     * 查询指定时间范围内的发送日志
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 发送日志列表
     */
    public List<EmailSendLog> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = """
            SELECT * FROM email_send_log
            WHERE create_time >= :startTime AND create_time <= :endTime
            ORDER BY create_time DESC
            """;

        Map<String, Object> params = Map.of("startTime", startTime, "endTime", endTime);
        return jdbcManager.queryList(sql, params, EmailSendLog.class);
    }

    /**
     * 查询发送成功的日志
     *
     * @param limit 查询数量
     * @return 发送日志列表
     */
    public List<EmailSendLog> findSuccessLogs(int limit) {
        String sql = """
            SELECT * FROM email_send_log
            WHERE send_status = 1
            ORDER BY create_time DESC
            LIMIT :limit
            """;

        Map<String, Object> params = Map.of("limit", limit);
        return jdbcManager.queryList(sql, params, EmailSendLog.class);
    }

    /**
     * 查询发送失败的日志
     *
     * @param limit 查询数量
     * @return 发送日志列表
     */
    public List<EmailSendLog> findFailedLogs(int limit) {
        String sql = """
            SELECT * FROM email_send_log
            WHERE send_status = 2
            ORDER BY create_time DESC
            LIMIT :limit
            """;

        Map<String, Object> params = Map.of("limit", limit);
        return jdbcManager.queryList(sql, params, EmailSendLog.class);
    }

    /**
     * 统计指定时间范围内的发送成功数量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 成功数量
     */
    public int countSuccessByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = """
            SELECT COUNT(*) FROM email_send_log
            WHERE send_status = 1
              AND create_time >= :startTime AND create_time <= :endTime
            """;

        Map<String, Object> params = Map.of("startTime", startTime, "endTime", endTime);
        Integer count = jdbcManager.queryObject(sql, params, Integer.class);
        return count != null ? count : 0;
    }

    /**
     * 统计指定时间范围内的发送失败数量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 失败数量
     */
    public int countFailedByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = """
            SELECT COUNT(*) FROM email_send_log
            WHERE send_status = 2
              AND create_time >= :startTime AND create_time <= :endTime
            """;

        Map<String, Object> params = Map.of("startTime", startTime, "endTime", endTime);
        Integer count = jdbcManager.queryObject(sql, params, Integer.class);
        return count != null ? count : 0;
    }

    /**
     * 统计指定收件人的发送次数
     *
     * @param recipientEmail 收件人邮箱
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @return 发送次数
     */
    public int countByRecipientEmail(String recipientEmail, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = """
            SELECT COUNT(*) FROM email_send_log
            WHERE recipient_email = :recipientEmail
              AND create_time >= :startTime AND create_time <= :endTime
            """;

        Map<String, Object> params = Map.of(
                "recipientEmail", recipientEmail,
                "startTime", startTime,
                "endTime", endTime
        );
        Integer count = jdbcManager.queryObject(sql, params, Integer.class);
        return count != null ? count : 0;
    }

    /**
     * 删除历史日志（超过指定天数）
     *
     * @param days 保留天数
     * @return 影响行数
     */
    public int deleteOldLogs(int days) {
        String sql = """
            DELETE FROM email_send_log
            WHERE create_time < DATE_SUB(NOW(), INTERVAL :days DAY)
            """;

        Map<String, Object> params = Map.of("days", days);
        return jdbcManager.update(sql, params);
    }

    /**
     * 查询平均处理耗时（指定时间范围）
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 平均耗时（毫秒）
     */
    public Double getAverageProcessDuration(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = """
            SELECT AVG(process_duration) FROM email_send_log
            WHERE send_status = 1
              AND create_time >= :startTime AND create_time <= :endTime
              AND process_duration IS NOT NULL
            """;

        Map<String, Object> params = Map.of("startTime", startTime, "endTime", endTime);
        return jdbcManager.queryObject(sql, params, Double.class);
    }
}

