package cn.refinex.auth.repository;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SysUserRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 更新用户最后登录信息
     * <p>
     * 说明：
     * 1. 更新最后登录时间和 IP
     * 2. 同时更新 update_time 字段
     * </p>
     *
     * @param userId    用户 ID
     * @param loginTime 登录时间
     * @param loginIp   登录 IP
     */
    public void updateLastLoginInfo(Long userId, LocalDateTime loginTime, String loginIp) {
        String sql = """
                UPDATE sys_user
                SET last_login_time = :loginTime,
                    last_login_ip = :loginIp,
                    update_time = NOW()
                WHERE id = :userId
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("loginTime", loginTime);
        params.put("loginIp", loginIp);

        try {
            int rows = jdbcManager.update(sql, params, true);
            log.debug("更新用户最后登录信息成功，userId: {}, rows: {}", userId, rows);
        } catch (Exception e) {
            log.error("更新用户最后登录信息失败，userId: {}", userId, e);
        }
    }
}

