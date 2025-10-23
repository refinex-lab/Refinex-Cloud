package cn.refinex.auth.repository;

import cn.refinex.auth.domain.entity.LogLogin;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.utils.object.BeanConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 登录日志数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class LoginLogRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 插入登录日志
     * <p>
     * 说明：
     * 1. 记录用户登录行为
     * 2. 包含登录 IP、设备信息、浏览器、操作系统等
     * 3. 支持成功和失败日志
     * </p>
     *
     * @param logLogin 登录日志实体
     */
    public void insert(LogLogin logLogin) {
        String sql = """
                INSERT INTO log_login (
                    user_id, username, login_type, login_ip, login_location,
                    browser, os, device_type, login_status, fail_reason, create_time
                ) VALUES (
                    :userId, :username, :loginType, :loginIp, :loginLocation,
                    :browser, :os, :deviceType, :loginStatus, :failReason, :createTime
                )
                """;

        Map<String, Object> params = BeanConverter.beanToMap(logLogin, false, false);

        try {
            int rows = jdbcManager.insert(sql, params);
            log.debug("插入登录日志成功，userId: {}, username: {}, loginStatus: {}, rows: {}", logLogin.getUserId(), logLogin.getUsername(), logLogin.getLoginStatus(), rows);
        } catch (Exception e) {
            log.error("插入登录日志失败，userId: {}, username: {}", logLogin.getUserId(), logLogin.getUsername(), e);
        }
    }
}

