package cn.refinex.platform.repository.email;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.platform.entity.email.EmailVerifyCode;
import cn.refinex.common.utils.object.BeanConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 邮箱验证码 Repository
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class EmailVerifyCodeRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 插入验证码
     *
     * @param verifyCode 验证码
     * @return 影响行数
     */
    public int insert(EmailVerifyCode verifyCode) {
        String sql = """
            INSERT INTO email_verify_code (
                id, email, verify_code, code_type, verify_scene,
                is_used, use_time, expire_time, client_ip, status, create_time
            ) VALUES (
                :id, :email, :verifyCode, :codeType, :verifyScene,
                :isUsed, :useTime, :expireTime, :clientIp, :status, :createTime
            )
            """;

        Map<String, Object> params = BeanConverter.beanToMap(verifyCode, false, false);
        return jdbcManager.insert(sql, params);
    }

    /**
     * 查询有效的验证码
     *
     * @param email    邮箱地址
     * @param codeType 验证码类型
     * @return 验证码
     */
    public EmailVerifyCode findValidCode(String email, String codeType) {
        String sql = """
            SELECT * FROM email_verify_code
            WHERE email = :email
              AND code_type = :codeType
              AND status = 0
              AND expire_time > NOW()
            ORDER BY create_time DESC
            LIMIT 1
            """;

        Map<String, Object> params = Map.of("email", email, "codeType", codeType);
        return jdbcManager.queryObject(sql, params, EmailVerifyCode.class);
    }

    /**
     * 查询验证码（包括已使用和已过期）
     *
     * @param email      邮箱地址
     * @param verifyCode 验证码
     * @param codeType   验证码类型
     * @return 验证码
     */
    public EmailVerifyCode findByEmailAndCode(String email, String verifyCode, String codeType) {
        String sql = """
            SELECT * FROM email_verify_code
            WHERE email = :email
              AND verify_code = :verifyCode
              AND code_type = :codeType
            ORDER BY create_time DESC
            LIMIT 1
            """;

        Map<String, Object> params = Map.of(
                "email", email,
                "verifyCode", verifyCode,
                "codeType", codeType
        );
        return jdbcManager.queryObject(sql, params, EmailVerifyCode.class);
    }

    /**
     * 标记验证码为已使用
     *
     * @param id 验证码 ID
     * @return 影响行数
     */
    public int markAsUsed(Long id) {
        String sql = """
            UPDATE email_verify_code
            SET is_used = 1, use_time = NOW(), status = 1
            WHERE id = :id
            """;

        Map<String, Object> params = Map.of("id", id);
        return jdbcManager.update(sql, params);
    }

    /**
     * 标记验证码为已失效
     *
     * @param id 验证码 ID
     * @return 影响行数
     */
    public int markAsInvalid(Long id) {
        String sql = """
            UPDATE email_verify_code
            SET status = 3
            WHERE id = :id
            """;

        Map<String, Object> params = Map.of("id", id);
        return jdbcManager.update(sql, params);
    }

    /**
     * 使同一邮箱同一类型的其他验证码失效
     *
     * @param email    邮箱地址
     * @param codeType 验证码类型
     * @param excludeId 排除的验证码 ID
     * @return 影响行数
     */
    public int invalidateOtherCodes(String email, String codeType, Long excludeId) {
        String sql = """
            UPDATE email_verify_code
            SET status = 3
            WHERE email = :email
              AND code_type = :codeType
              AND id != :excludeId
              AND status = 0
            """;

        Map<String, Object> params = Map.of(
                "email", email,
                "codeType", codeType,
                "excludeId", excludeId
        );
        return jdbcManager.update(sql, params);
    }

    /**
     * 统计指定时间范围内的验证码发送次数（按邮箱）
     *
     * @param email     邮箱地址
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 发送次数
     */
    public int countByEmail(String email, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = """
            SELECT COUNT(*) FROM email_verify_code
            WHERE email = :email
              AND create_time >= :startTime AND create_time <= :endTime
            """;

        Map<String, Object> params = Map.of(
                "email", email,
                "startTime", startTime,
                "endTime", endTime
        );
        Integer count = jdbcManager.queryInt(sql, params);
        return count != null ? count : 0;
    }

    /**
     * 统计指定时间范围内的验证码发送次数（按 IP）
     *
     * @param clientIp  客户端 IP
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 发送次数
     */
    public int countByClientIp(String clientIp, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = """
            SELECT COUNT(*) FROM email_verify_code
            WHERE client_ip = :clientIp
              AND create_time >= :startTime AND create_time <= :endTime
            """;

        Map<String, Object> params = Map.of(
                "clientIp", clientIp,
                "startTime", startTime,
                "endTime", endTime
        );
        Integer count = jdbcManager.queryInt(sql, params);
        return count != null ? count : 0;
    }

    /**
     * 更新过期验证码的状态
     *
     * @return 影响行数
     */
    public int updateExpiredCodes() {
        String sql = """
            UPDATE email_verify_code
            SET status = 2
            WHERE status = 0
              AND expire_time <= NOW()
            """;

        return jdbcManager.update(sql, Map.of());
    }

    /**
     * 删除历史验证码（超过指定天数）
     *
     * @param days 保留天数
     * @return 影响行数
     */
    public int deleteOldCodes(int days) {
        String sql = """
            DELETE FROM email_verify_code
            WHERE create_time < DATE_SUB(NOW(), INTERVAL :days DAY)
            """;

        Map<String, Object> params = Map.of("days", days);
        return jdbcManager.update(sql, params);
    }

    /**
     * 查询指定邮箱的验证码历史
     *
     * @param email 邮箱地址
     * @param limit 查询数量
     * @return 验证码列表
     */
    public List<EmailVerifyCode> findByEmail(String email, int limit) {
        String sql = """
            SELECT * FROM email_verify_code
            WHERE email = :email
            ORDER BY create_time DESC
            LIMIT :limit
            """;

        Map<String, Object> params = Map.of("email", email, "limit", limit);
        return jdbcManager.queryList(sql, params, EmailVerifyCode.class);
    }

    /**
     * 统计验证码使用率（指定时间范围）
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 使用率（0-1）
     */
    public Double getUsageRate(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = """
            SELECT
                IFNULL(SUM(CASE WHEN is_used = 1 THEN 1 ELSE 0 END) / COUNT(*), 0) as usage_rate
            FROM email_verify_code
            WHERE create_time >= :startTime AND create_time <= :endTime
            """;

        Map<String, Object> params = Map.of("startTime", startTime, "endTime", endTime);
        return jdbcManager.queryObject(sql, params, Double.class);
    }
}

