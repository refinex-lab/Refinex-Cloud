package cn.refinex.platform.repository.log;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.entity.log.LogLogin;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 登录日志数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class LogLoginRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 保存登录日志
     *
     * @param logLogin 登录日志
     */
    public void saveLogLogin(LogLogin logLogin) {
        String sql = """
                INSERT INTO log_login(
                    user_id, username, login_type, login_ip, login_location, browser, os,
                    device_type, login_status, fail_reason, create_time
                )
                VALUES
                (
                    :userId, :username, :loginType, :loginIp, :loginLocation, :browser, :os,
                    :deviceType, :loginStatus, :failReason, :createTime
                )
                """;

        Map<String, Object> params = BeanConverter.beanToMap(logLogin, false, false);
        jdbcManager.insert(sql, params);
    }

    /**
     * 根据ID获取登录日志
     *
     * @param id 日志ID
     * @return 登录日志
     */
    public LogLogin getById(Long id) {
        String sql = """
                SELECT id, user_id, username, login_type, login_ip, login_location, browser, os,
                       device_type, login_status, fail_reason, create_time
                FROM log_login
                WHERE id = :id
                """;

        Map<String, Object> params = Map.of("id", id);
        return jdbcManager.queryObject(sql, params, LogLogin.class);
    }

    /**
     * 分页查询登录日志
     *
     * @param username    用户名（模糊查询）
     * @param loginType   登录方式
     * @param loginStatus 登录状态
     * @param loginIp     登录IP
     * @param deviceType  设备类型
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    public PageResult<LogLogin> pageQuery(String username, String loginType, Integer loginStatus, String loginIp, String deviceType, LocalDateTime startTime, LocalDateTime endTime, PageRequest pageRequest) {
        // 构建查询条件
        StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
        Map<String, Object> params = new java.util.HashMap<>();

        if (username != null && !username.trim().isEmpty()) {
            whereSql.append(" AND username LIKE :username ");
            params.put("username", "%" + username.trim() + "%");
        }

        if (loginType != null && !loginType.trim().isEmpty()) {
            whereSql.append(" AND login_type = :loginType ");
            params.put("loginType", loginType);
        }

        if (loginStatus != null) {
            whereSql.append(" AND login_status = :loginStatus ");
            params.put("loginStatus", loginStatus);
        }

        if (loginIp != null && !loginIp.trim().isEmpty()) {
            whereSql.append(" AND login_ip = :loginIp ");
            params.put("loginIp", loginIp);
        }

        if (deviceType != null && !deviceType.trim().isEmpty()) {
            whereSql.append(" AND device_type = :deviceType ");
            params.put("deviceType", deviceType);
        }

        if (startTime != null) {
            whereSql.append(" AND create_time >= :startTime ");
            params.put("startTime", startTime);
        }

        if (endTime != null) {
            whereSql.append(" AND create_time <= :endTime ");
            params.put("endTime", endTime);
        }

        // 构建基础查询SQL
        String baseSql = """
                SELECT id, user_id, username, login_type, login_ip, login_location, browser, os,
                       device_type, login_status, fail_reason, create_time
                FROM log_login
                """ + whereSql + """
                ORDER BY create_time DESC
                """;

        return jdbcManager.queryPage(baseSql, params, pageRequest, LogLogin.class);
    }

    /**
     * 获取基础统计信息
     *
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param username   用户名（可选）
     * @param loginType  登录方式（可选）
     * @param deviceType 设备类型（可选）
     * @return 基础统计信息
     */
    public Map<String, Object> getBasicStatistics(LocalDateTime startTime, LocalDateTime endTime, String username, String loginType, String deviceType) {
        StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
        Map<String, Object> params = new java.util.HashMap<>();

        if (startTime != null) {
            whereSql.append(" AND create_time >= :startTime ");
            params.put("startTime", startTime);
        }

        if (endTime != null) {
            whereSql.append(" AND create_time <= :endTime ");
            params.put("endTime", endTime);
        }

        if (username != null && !username.trim().isEmpty()) {
            whereSql.append(" AND username = :username ");
            params.put("username", username);
        }

        if (loginType != null && !loginType.trim().isEmpty()) {
            whereSql.append(" AND login_type = :loginType ");
            params.put("loginType", loginType);
        }

        if (deviceType != null && !deviceType.trim().isEmpty()) {
            whereSql.append(" AND device_type = :deviceType ");
            params.put("deviceType", deviceType);
        }

        // 一条 SQL 完成所有基础统计
        String statisticsSql = """
                SELECT
                    COUNT(*) as total_count,
                    COALESCE(SUM(CASE WHEN login_status = 0 THEN 1 ELSE 0 END), 0) as success_count,
                    COALESCE(SUM(CASE WHEN login_status = 1 THEN 1 ELSE 0 END), 0) as failure_count,
                    COUNT(DISTINCT user_id) as unique_user_count,
                    COUNT(DISTINCT login_ip) as unique_ip_count
                FROM log_login
                """ + whereSql;

        Map<String, Object> result = jdbcManager.queryMap(statisticsSql, params);
        return new CaseInsensitiveMap<>(result);
    }

    /**
     * 按登录方式分组统计
     *
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param username   用户名（可选）
     * @param deviceType 设备类型（可选）
     * @return 分组统计结果
     */
    public List<Map<String, Object>> getStatisticsByLoginType(LocalDateTime startTime, LocalDateTime endTime, String username, String deviceType) {
        StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
        Map<String, Object> params = new java.util.HashMap<>();

        if (startTime != null) {
            whereSql.append(" AND create_time >= :startTime ");
            params.put("startTime", startTime);
        }

        if (endTime != null) {
            whereSql.append(" AND create_time <= :endTime ");
            params.put("endTime", endTime);
        }

        if (username != null && !username.trim().isEmpty()) {
            whereSql.append(" AND username = :username ");
            params.put("username", username);
        }

        if (deviceType != null && !deviceType.trim().isEmpty()) {
            whereSql.append(" AND device_type = :deviceType ");
            params.put("deviceType", deviceType);
        }

        String sql = """
                SELECT
                    login_type as group_name,
                    COUNT(*) as count,
                    COALESCE(SUM(CASE WHEN login_status = 0 THEN 1 ELSE 0 END), 0) as success_count,
                    COALESCE(SUM(CASE WHEN login_status = 1 THEN 1 ELSE 0 END), 0) as failure_count
                FROM log_login
                """ + whereSql + """
                GROUP BY login_type
                ORDER BY count DESC
                """;

        return jdbcManager.queryList(sql, params);
    }

    /**
     * 按设备类型分组统计
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param username  用户名（可选）
     * @param loginType 登录方式（可选）
     * @return 分组统计结果
     */
    public List<Map<String, Object>> getStatisticsByDeviceType(LocalDateTime startTime, LocalDateTime endTime, String username, String loginType) {
        StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
        Map<String, Object> params = new java.util.HashMap<>();

        if (startTime != null) {
            whereSql.append(" AND create_time >= :startTime ");
            params.put("startTime", startTime);
        }

        if (endTime != null) {
            whereSql.append(" AND create_time <= :endTime ");
            params.put("endTime", endTime);
        }

        if (username != null && !username.trim().isEmpty()) {
            whereSql.append(" AND username = :username ");
            params.put("username", username);
        }

        if (loginType != null && !loginType.trim().isEmpty()) {
            whereSql.append(" AND login_type = :loginType ");
            params.put("loginType", loginType);
        }

        String sql = """
                SELECT
                    device_type as group_name,
                    COUNT(*) as count,
                    COALESCE(SUM(CASE WHEN login_status = 0 THEN 1 ELSE 0 END), 0) as success_count,
                    COALESCE(SUM(CASE WHEN login_status = 1 THEN 1 ELSE 0 END), 0) as failure_count
                FROM log_login
                """ + whereSql + """
                GROUP BY device_type
                ORDER BY count DESC
                """;

        return jdbcManager.queryList(sql, params);
    }

    /**
     * 获取趋势数据（按天）
     *
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param username   用户名（可选）
     * @param loginType  登录方式（可选）
     * @param deviceType 设备类型（可选）
     * @return 趋势数据点列表
     */
    public List<Map<String, Object>> getTrendDataByDay(LocalDateTime startTime, LocalDateTime endTime, String username, String loginType, String deviceType) {
        StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
        Map<String, Object> params = new java.util.HashMap<>();
        
        // 如果没有指定时间范围，默认查询最近30天
        if (startTime == null && endTime == null) {
            startTime = LocalDateTime.now().minusDays(30);
            endTime = LocalDateTime.now();
        }
        
        if (startTime != null) {
            whereSql.append(" AND create_time >= :startTime ");
            params.put("startTime", startTime);
        }
        
        if (endTime != null) {
            whereSql.append(" AND create_time <= :endTime ");
            params.put("endTime", endTime);
        }

        if (username != null && !username.trim().isEmpty()) {
            whereSql.append(" AND username = :username ");
            params.put("username", username);
        }

        if (loginType != null && !loginType.trim().isEmpty()) {
            whereSql.append(" AND login_type = :loginType ");
            params.put("loginType", loginType);
        }

        if (deviceType != null && !deviceType.trim().isEmpty()) {
            whereSql.append(" AND device_type = :deviceType ");
            params.put("deviceType", deviceType);
        }

        String sql = """
                SELECT
                    DATE(create_time) as time_point,
                    COUNT(*) as total_count,
                    COALESCE(SUM(CASE WHEN login_status = 0 THEN 1 ELSE 0 END), 0) as success_count,
                    COALESCE(SUM(CASE WHEN login_status = 1 THEN 1 ELSE 0 END), 0) as failure_count,
                    COUNT(DISTINCT user_id) as unique_user_count
                FROM log_login
                """ + whereSql + """
                GROUP BY DATE(create_time)
                ORDER BY time_point ASC
                """;

        return jdbcManager.queryList(sql, params);
    }

    /**
     * 获取 Top 登录用户
     *
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param loginType  登录方式（可选）
     * @param deviceType 设备类型（可选）
     * @param limit      返回数量
     * @return Top 用户列表
     */
    public List<Map<String, Object>> getTopUsers(LocalDateTime startTime, LocalDateTime endTime, String loginType, String deviceType, Integer limit) {
        StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
        Map<String, Object> params = new java.util.HashMap<>();

        if (startTime != null) {
            whereSql.append(" AND create_time >= :startTime ");
            params.put("startTime", startTime);
        }

        if (endTime != null) {
            whereSql.append(" AND create_time <= :endTime ");
            params.put("endTime", endTime);
        }

        if (loginType != null && !loginType.trim().isEmpty()) {
            whereSql.append(" AND login_type = :loginType ");
            params.put("loginType", loginType);
        }

        if (deviceType != null && !deviceType.trim().isEmpty()) {
            whereSql.append(" AND device_type = :deviceType ");
            params.put("deviceType", deviceType);
        }

        params.put("limit", limit);

        String sql = """
                SELECT
                    username,
                    COUNT(*) as count,
                    COALESCE(SUM(CASE WHEN login_status = 0 THEN 1 ELSE 0 END), 0) as success_count,
                    COALESCE(SUM(CASE WHEN login_status = 1 THEN 1 ELSE 0 END), 0) as failure_count,
                    MAX(create_time) as last_login_time
                FROM log_login
                """ + whereSql + """
                GROUP BY username
                ORDER BY count DESC
                LIMIT :limit
                """;

        return jdbcManager.queryList(sql, params);
    }

    /**
     * 获取 Top 登录IP
     *
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param loginType  登录方式（可选）
     * @param deviceType 设备类型（可选）
     * @param limit      返回数量
     * @return Top IP列表
     */
    public List<Map<String, Object>> getTopIps(LocalDateTime startTime, LocalDateTime endTime, String loginType, String deviceType, Integer limit) {
        StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
        Map<String, Object> params = new java.util.HashMap<>();

        if (startTime != null) {
            whereSql.append(" AND create_time >= :startTime ");
            params.put("startTime", startTime);
        }

        if (endTime != null) {
            whereSql.append(" AND create_time <= :endTime ");
            params.put("endTime", endTime);
        }

        if (loginType != null && !loginType.trim().isEmpty()) {
            whereSql.append(" AND login_type = :loginType ");
            params.put("loginType", loginType);
        }

        if (deviceType != null && !deviceType.trim().isEmpty()) {
            whereSql.append(" AND device_type = :deviceType ");
            params.put("deviceType", deviceType);
        }

        params.put("limit", limit);

        String sql = """
                SELECT
                    login_ip,
                    login_location,
                    COUNT(*) as count,
                    COALESCE(SUM(CASE WHEN login_status = 0 THEN 1 ELSE 0 END), 0) as success_count,
                    COALESCE(SUM(CASE WHEN login_status = 1 THEN 1 ELSE 0 END), 0) as failure_count,
                    COUNT(DISTINCT username) as unique_user_count
                FROM log_login
                """ + whereSql + """
                GROUP BY login_ip, login_location
                ORDER BY count DESC
                LIMIT :limit
                """;

        return jdbcManager.queryList(sql, params);
    }
}

