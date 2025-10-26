package cn.refinex.platform.repository.log;

import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.controller.logger.dto.request.LogOperationQueryRequestDTO;
import cn.refinex.platform.entity.log.LogOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 操作日志数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class LogOperationRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 保存操作日志
     *
     * @param logOperation 操作日志
     */
    public void saveLogOperation(LogOperation logOperation) {
        String sql = """
                INSERT INTO log_operation(
                    user_id, username, operation_module, operation_type, operation_desc, request_method, request_url,
                    request_params, response_result, operation_ip, operation_location, browser, os, operation_status,
                    error_message, execution_time, create_time, request_body, application_name
                )
                VALUES
                (
                    :userId, :username, :operationModule, :operationType, :operationDesc, :requestMethod, :requestUrl,
                    :requestParams, :responseResult, :operationIp, :operationLocation, :browser, :os, :operationStatus,
                    :errorMessage, :executionTime, :createTime, :requestBody, :applicationName
                )
                """;

        Map<String, Object> params = BeanConverter.beanToMap(logOperation, false, false);
        jdbcManager.insert(sql, params);
    }

    /**
     * 根据ID获取操作日志
     *
     * @param id 日志ID
     * @return 操作日志
     */
    public LogOperation getById(Long id) {
        String sql = """
                SELECT id, user_id, username, application_name, operation_module, operation_type, operation_desc,
                       request_method, request_url, request_params, request_body, response_result, operation_ip,
                       operation_location, browser, os, operation_status, error_message, execution_time, create_time
                FROM log_operation
                WHERE id = :id
                """;

        Map<String, Object> params = Map.of("id", id);
        return jdbcManager.queryObject(sql, params, LogOperation.class);
    }

    /**
     * 分页查询操作日志
     *
     * @param queryRequest 查询条件
     * @param pageRequest  分页请求
     * @return 分页结果
     */
    public PageResult<LogOperation> pageQuery(LogOperationQueryRequestDTO queryRequest, PageRequest pageRequest) {
        // 构建查询条件
        StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
        Map<String, Object> params = new java.util.HashMap<>();

        if (queryRequest.getUsername() != null && !queryRequest.getUsername().trim().isEmpty()) {
            whereSql.append(" AND username LIKE :username ");
            params.put("username", "%" + queryRequest.getUsername().trim() + "%");
        }

        if (queryRequest.getApplicationName() != null && !queryRequest.getApplicationName().trim().isEmpty()) {
            whereSql.append(" AND application_name = :applicationName ");
            params.put("applicationName", queryRequest.getApplicationName());
        }

        if (queryRequest.getOperationModule() != null && !queryRequest.getOperationModule().trim().isEmpty()) {
            whereSql.append(" AND operation_module = :operationModule ");
            params.put("operationModule", queryRequest.getOperationModule());
        }

        if (queryRequest.getOperationType() != null && !queryRequest.getOperationType().trim().isEmpty()) {
            whereSql.append(" AND operation_type = :operationType ");
            params.put("operationType", queryRequest.getOperationType());
        }

        if (queryRequest.getOperationStatus() != null) {
            whereSql.append(" AND operation_status = :operationStatus ");
            params.put("operationStatus", queryRequest.getOperationStatus());
        }

        if (queryRequest.getOperationIp() != null && !queryRequest.getOperationIp().trim().isEmpty()) {
            whereSql.append(" AND operation_ip = :operationIp ");
            params.put("operationIp", queryRequest.getOperationIp());
        }

        if (queryRequest.getStartTime() != null) {
            whereSql.append(" AND create_time >= :startTime ");
            params.put("startTime", queryRequest.getStartTime());
        }

        if (queryRequest.getEndTime() != null) {
            whereSql.append(" AND create_time <= :endTime ");
            params.put("endTime", queryRequest.getEndTime());
        }

        // 构建基础查询SQL（不包含分页语句，JdbcTemplateManager会自动处理）
        String baseSql = """
                SELECT id, user_id, username, application_name, operation_module, operation_type, operation_desc,
                       request_method, request_url, request_params, request_body, response_result, operation_ip,
                       operation_location, browser, os, operation_status, error_message, execution_time, create_time
                FROM log_operation
                """ + whereSql + """
                ORDER BY create_time DESC
                """;

        return jdbcManager.queryPage(baseSql, params, pageRequest, LogOperation.class);
    }

    /**
     * 获取基础统计信息（一条 SQL 完成）
     *
     * @param startTime       开始时间
     * @param endTime         结束时间
     * @param applicationName 应用名称（可选）
     * @param operationModule 操作模块（可选）
     * @param username        用户名（可选）
     * @return 基础统计信息
     */
    public Map<String, Object> getBasicStatistics(LocalDateTime startTime, LocalDateTime endTime, String applicationName, String operationModule, String username) {
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

        if (applicationName != null && !applicationName.trim().isEmpty()) {
            whereSql.append(" AND application_name = :applicationName ");
            params.put("applicationName", applicationName);
        }

        if (operationModule != null && !operationModule.trim().isEmpty()) {
            whereSql.append(" AND operation_module = :operationModule ");
            params.put("operationModule", operationModule);
        }

        if (username != null && !username.trim().isEmpty()) {
            whereSql.append(" AND username = :username ");
            params.put("username", username);
        }

        // 一条 SQL 完成所有基础统计
        String statisticsSql = """
                SELECT
                    COUNT(*) as total_count,
                    SUM(CASE WHEN operation_status = 0 THEN 1 ELSE 0 END) as success_count,
                    SUM(CASE WHEN operation_status = 1 THEN 1 ELSE 0 END) as failure_count,
                    ROUND(AVG(execution_time), 2) as avg_execution_time,
                    MAX(execution_time) as max_execution_time,
                    MIN(execution_time) as min_execution_time
                FROM log_operation
                """ + whereSql;

        Map<String, Object> result = jdbcManager.queryMap(statisticsSql, params);
        return new CaseInsensitiveMap<>(result);
    }

    /**
     * 按操作类型分组统计
     *
     * @param startTime       开始时间
     * @param endTime         结束时间
     * @param applicationName 应用名称（可选）
     * @param operationModule 操作模块（可选）
     * @param username        用户名（可选）
     * @return 分组统计结果
     */
    public List<Map<String, Object>> getStatisticsByType(LocalDateTime startTime, LocalDateTime endTime, String applicationName, String operationModule, String username) {
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

        if (applicationName != null && !applicationName.trim().isEmpty()) {
            whereSql.append(" AND application_name = :applicationName ");
            params.put("applicationName", applicationName);
        }

        if (operationModule != null && !operationModule.trim().isEmpty()) {
            whereSql.append(" AND operation_module = :operationModule ");
            params.put("operationModule", operationModule);
        }

        if (username != null && !username.trim().isEmpty()) {
            whereSql.append(" AND username = :username ");
            params.put("username", username);
        }

        String sql = """
                SELECT
                    operation_type as group_name,
                    COUNT(*) as count
                FROM log_operation
                """ + whereSql + """
                GROUP BY operation_type
                ORDER BY count DESC
                """;

        return jdbcManager.queryList(sql, params);
    }

    /**
     * 按操作模块分组统计
     *
     * @param startTime       开始时间
     * @param endTime         结束时间
     * @param applicationName 应用名称（可选）
     * @param username        用户名（可选）
     * @return 分组统计结果
     */
    public List<Map<String, Object>> getStatisticsByModule(LocalDateTime startTime, LocalDateTime endTime, String applicationName, String username) {

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

        if (applicationName != null && !applicationName.trim().isEmpty()) {
            whereSql.append(" AND application_name = :applicationName ");
            params.put("applicationName", applicationName);
        }

        if (username != null && !username.trim().isEmpty()) {
            whereSql.append(" AND username = :username ");
            params.put("username", username);
        }

        String sql = """
                SELECT
                    operation_module as group_name,
                    COUNT(*) as count
                FROM log_operation
                """ + whereSql + """
                GROUP BY operation_module
                ORDER BY count DESC
                """;

        return jdbcManager.queryList(sql, params);
    }

    /**
     * 获取 Top 操作用户
     *
     * @param startTime       开始时间
     * @param endTime         结束时间
     * @param applicationName 应用名称（可选）
     * @param operationModule 操作模块（可选）
     * @param limit           返回数量
     * @return Top 用户列表
     */
    public List<Map<String, Object>> getTopUsers(LocalDateTime startTime, LocalDateTime endTime, String applicationName, String operationModule, Integer limit) {

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

        if (applicationName != null && !applicationName.trim().isEmpty()) {
            whereSql.append(" AND application_name = :applicationName ");
            params.put("applicationName", applicationName);
        }

        if (operationModule != null && !operationModule.trim().isEmpty()) {
            whereSql.append(" AND operation_module = :operationModule ");
            params.put("operationModule", operationModule);
        }

        params.put("limit", limit);

        String sql = """
                SELECT
                    username,
                    COUNT(*) as count,
                    SUM(CASE WHEN operation_status = 0 THEN 1 ELSE 0 END) as success_count,
                    SUM(CASE WHEN operation_status = 1 THEN 1 ELSE 0 END) as failure_count
                FROM log_operation
                """ + whereSql + """
                GROUP BY username
                ORDER BY count DESC
                LIMIT :limit
                """;

        return jdbcManager.queryList(sql, params);
    }

    /**
     * 获取趋势数据（按天）
     *
     * @param startTime       开始时间
     * @param endTime         结束时间
     * @param applicationName 应用名称（可选）
     * @param operationModule 操作模块（可选）
     * @param username        用户名（可选）
     * @return 趋势数据点列表
     */
    public List<Map<String, Object>> getTrendDataByDay(LocalDateTime startTime, LocalDateTime endTime, String applicationName, String operationModule, String username) {
        StringBuilder whereSql = new StringBuilder(" WHERE create_time >= :startTime AND create_time <= :endTime ");
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);

        if (applicationName != null && !applicationName.trim().isEmpty()) {
            whereSql.append(" AND application_name = :applicationName ");
            params.put("applicationName", applicationName);
        }

        if (operationModule != null && !operationModule.trim().isEmpty()) {
            whereSql.append(" AND operation_module = :operationModule ");
            params.put("operationModule", operationModule);
        }

        if (username != null && !username.trim().isEmpty()) {
            whereSql.append(" AND username = :username ");
            params.put("username", username);
        }

        String sql = """
                SELECT
                    DATE(create_time) as time_point,
                    COUNT(*) as total_count,
                    SUM(CASE WHEN operation_status = 0 THEN 1 ELSE 0 END) as success_count,
                    SUM(CASE WHEN operation_status = 1 THEN 1 ELSE 0 END) as failure_count,
                    ROUND(AVG(execution_time), 2) as avg_execution_time
                FROM log_operation
                """ + whereSql + """
                GROUP BY DATE(create_time)
                ORDER BY time_point ASC
                """;

        return jdbcManager.queryList(sql, params);
    }

    /**
     * 获取趋势数据（按小时）
     *
     * @param startTime       开始时间
     * @param endTime         结束时间
     * @param applicationName 应用名称（可选）
     * @param operationModule 操作模块（可选）
     * @param username        用户名（可选）
     * @return 趋势数据点列表
     */
    public List<Map<String, Object>> getTrendDataByHour(LocalDateTime startTime, LocalDateTime endTime, String applicationName, String operationModule, String username) {
        StringBuilder whereSql = new StringBuilder(" WHERE create_time >= :startTime AND create_time <= :endTime ");
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);

        if (applicationName != null && !applicationName.trim().isEmpty()) {
            whereSql.append(" AND application_name = :applicationName ");
            params.put("applicationName", applicationName);
        }

        if (operationModule != null && !operationModule.trim().isEmpty()) {
            whereSql.append(" AND operation_module = :operationModule ");
            params.put("operationModule", operationModule);
        }

        if (username != null && !username.trim().isEmpty()) {
            whereSql.append(" AND username = :username ");
            params.put("username", username);
        }

        String sql = """
                SELECT
                    DATE_FORMAT(create_time, '%Y-%m-%d %H:00:00') as time_point,
                    COUNT(*) as total_count,
                    SUM(CASE WHEN operation_status = 0 THEN 1 ELSE 0 END) as success_count,
                    SUM(CASE WHEN operation_status = 1 THEN 1 ELSE 0 END) as failure_count,
                    ROUND(AVG(execution_time), 2) as avg_execution_time
                FROM log_operation
                """ + whereSql + """
                GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d %H:00:00')
                ORDER BY time_point ASC
                """;

        return jdbcManager.queryList(sql, params);
    }

    /**
     * 获取趋势数据（按周）
     *
     * @param startTime       开始时间
     * @param endTime         结束时间
     * @param applicationName 应用名称（可选）
     * @param operationModule 操作模块（可选）
     * @param username        用户名（可选）
     * @return 趋势数据点列表
     */
    public List<Map<String, Object>> getTrendDataByWeek(LocalDateTime startTime, LocalDateTime endTime, String applicationName, String operationModule, String username) {
        StringBuilder whereSql = new StringBuilder(" WHERE create_time >= :startTime AND create_time <= :endTime ");
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);

        if (applicationName != null && !applicationName.trim().isEmpty()) {
            whereSql.append(" AND application_name = :applicationName ");
            params.put("applicationName", applicationName);
        }

        if (operationModule != null && !operationModule.trim().isEmpty()) {
            whereSql.append(" AND operation_module = :operationModule ");
            params.put("operationModule", operationModule);
        }

        if (username != null && !username.trim().isEmpty()) {
            whereSql.append(" AND username = :username ");
            params.put("username", username);
        }

        String sql = """
                SELECT
                    DATE_FORMAT(create_time, '%Y-%u') as time_point,
                    COUNT(*) as total_count,
                    SUM(CASE WHEN operation_status = 0 THEN 1 ELSE 0 END) as success_count,
                    SUM(CASE WHEN operation_status = 1 THEN 1 ELSE 0 END) as failure_count,
                    ROUND(AVG(execution_time), 2) as avg_execution_time
                FROM log_operation
                """ + whereSql + """
                GROUP BY DATE_FORMAT(create_time, '%Y-%u')
                ORDER BY time_point ASC
                """;

        return jdbcManager.queryList(sql, params);
    }

    /**
     * 获取趋势数据（按月）
     *
     * @param startTime       开始时间
     * @param endTime         结束时间
     * @param applicationName 应用名称（可选）
     * @param operationModule 操作模块（可选）
     * @param username        用户名（可选）
     * @return 趋势数据点列表
     */
    public List<Map<String, Object>> getTrendDataByMonth(LocalDateTime startTime, LocalDateTime endTime, String applicationName, String operationModule, String username) {
        StringBuilder whereSql = new StringBuilder(" WHERE create_time >= :startTime AND create_time <= :endTime ");
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);

        if (applicationName != null && !applicationName.trim().isEmpty()) {
            whereSql.append(" AND application_name = :applicationName ");
            params.put("applicationName", applicationName);
        }

        if (operationModule != null && !operationModule.trim().isEmpty()) {
            whereSql.append(" AND operation_module = :operationModule ");
            params.put("operationModule", operationModule);
        }

        if (username != null && !username.trim().isEmpty()) {
            whereSql.append(" AND username = :username ");
            params.put("username", username);
        }

        String sql = """
                SELECT
                    DATE_FORMAT(create_time, '%Y-%m') as time_point,
                    COUNT(*) as total_count,
                    SUM(CASE WHEN operation_status = 0 THEN 1 ELSE 0 END) as success_count,
                    SUM(CASE WHEN operation_status = 1 THEN 1 ELSE 0 END) as failure_count,
                    ROUND(AVG(execution_time), 2) as avg_execution_time
                FROM log_operation
                """ + whereSql + """
                GROUP BY DATE_FORMAT(create_time, '%Y-%m')
                ORDER BY time_point ASC
                """;

        return jdbcManager.queryList(sql, params);
    }

}
