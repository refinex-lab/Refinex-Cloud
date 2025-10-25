package cn.refinex.platform.repository.log;

import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.controller.logger.dto.request.LogOperationQueryRequestDTO;
import cn.refinex.platform.controller.logger.dto.response.LogOperationStatisticsDTO;
import cn.refinex.platform.entity.log.LogOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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

        if (queryRequest.getUserId() != null) {
            whereSql.append(" AND user_id = :userId ");
            params.put("userId", queryRequest.getUserId());
        }

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

        if (queryRequest.getOperationDesc() != null && !queryRequest.getOperationDesc().trim().isEmpty()) {
            whereSql.append(" AND operation_desc LIKE :operationDesc ");
            params.put("operationDesc", "%" + queryRequest.getOperationDesc().trim() + "%");
        }

        if (queryRequest.getRequestMethod() != null && !queryRequest.getRequestMethod().trim().isEmpty()) {
            whereSql.append(" AND request_method = :requestMethod ");
            params.put("requestMethod", queryRequest.getRequestMethod());
        }

        if (queryRequest.getRequestUrl() != null && !queryRequest.getRequestUrl().trim().isEmpty()) {
            whereSql.append(" AND request_url LIKE :requestUrl ");
            params.put("requestUrl", "%" + queryRequest.getRequestUrl().trim() + "%");
        }

        if (queryRequest.getOperationIp() != null && !queryRequest.getOperationIp().trim().isEmpty()) {
            whereSql.append(" AND operation_ip = :operationIp ");
            params.put("operationIp", queryRequest.getOperationIp());
        }

        if (queryRequest.getOperationStatus() != null) {
            whereSql.append(" AND operation_status = :operationStatus ");
            params.put("operationStatus", queryRequest.getOperationStatus());
        }

        if (Boolean.TRUE.equals(queryRequest.getHasError())) {
            whereSql.append(" AND error_message IS NOT NULL AND error_message != '' ");
        }

        if (queryRequest.getMinExecutionTime() != null) {
            whereSql.append(" AND execution_time >= :minExecutionTime ");
            params.put("minExecutionTime", queryRequest.getMinExecutionTime());
        }

        if (queryRequest.getMaxExecutionTime() != null) {
            whereSql.append(" AND execution_time <= :maxExecutionTime ");
            params.put("maxExecutionTime", queryRequest.getMaxExecutionTime());
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
     * 获取操作日志统计信息
     *
     * @param queryRequest 查询条件
     * @return 统计信息
     */
    public LogOperationStatisticsDTO getStatistics(LogOperationQueryRequestDTO queryRequest) {
        // 构建查询条件
        StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
        Map<String, Object> params = new java.util.HashMap<>();

        if (queryRequest.getUserId() != null) {
            whereSql.append(" AND user_id = :userId ");
            params.put("userId", queryRequest.getUserId());
        }

        if (queryRequest.getOperationModule() != null && !queryRequest.getOperationModule().trim().isEmpty()) {
            whereSql.append(" AND operation_module = :operationModule ");
            params.put("operationModule", queryRequest.getOperationModule());
        }

        if (queryRequest.getStartTime() != null) {
            whereSql.append(" AND create_time >= :startTime ");
            params.put("startTime", queryRequest.getStartTime());
        }

        if (queryRequest.getEndTime() != null) {
            whereSql.append(" AND create_time <= :endTime ");
            params.put("endTime", queryRequest.getEndTime());
        }

        // 统计SQL
        String statisticsSql = """
                SELECT
                    COUNT(*) as totalCount,
                    SUM(CASE WHEN operation_status = 0 THEN 1 ELSE 0 END) as successCount,
                    SUM(CASE WHEN operation_status = 1 THEN 1 ELSE 0 END) as failureCount,
                    AVG(execution_time) as avgExecutionTime,
                    MAX(execution_time) as maxExecutionTime,
                    MIN(execution_time) as minExecutionTime
                FROM log_operation
                """ + whereSql;

        Map<String, Object> result = jdbcManager.queryMap(statisticsSql, params);

        long totalCount = ((Number) result.get("totalCount")).longValue();
        Long successCount = ((Number) result.get("successCount")).longValue();
        Long failureCount = ((Number) result.get("failureCount")).longValue();

        Double successRate = totalCount > 0 ? (double) successCount / totalCount * 100 : 0.0;
        Double avgExecutionTime = result.get("avgExecutionTime") != null ? ((Number) result.get("avgExecutionTime")).doubleValue() : 0.0;
        Integer maxExecutionTime = result.get("maxExecutionTime") != null ? ((Number) result.get("maxExecutionTime")).intValue() : 0;
        Integer minExecutionTime = result.get("minExecutionTime") != null ? ((Number) result.get("minExecutionTime")).intValue() : 0;

        return LogOperationStatisticsDTO.builder()
                .totalCount(totalCount)
                .successCount(successCount)
                .failureCount(failureCount)
                .successRate(successRate)
                .avgExecutionTime(avgExecutionTime)
                .maxExecutionTime(maxExecutionTime)
                .minExecutionTime(minExecutionTime)
                .todayCount(0L) // 将在Service层计算
                .weekCount(0L)  // 将在Service层计算
                .monthCount(0L) // 将在Service层计算
                .build();
    }

    /**
     * 根据条件统计数量
     *
     * @param queryRequest 查询条件
     * @return 统计数量
     */
    public Long countByCondition(LogOperationQueryRequestDTO queryRequest) {
        // 构建查询条件
        StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
        Map<String, Object> params = new java.util.HashMap<>();

        if (queryRequest.getUserId() != null) {
            whereSql.append(" AND user_id = :userId ");
            params.put("userId", queryRequest.getUserId());
        }

        if (queryRequest.getUsername() != null && !queryRequest.getUsername().trim().isEmpty()) {
            whereSql.append(" AND username LIKE :username ");
            params.put("username", "%" + queryRequest.getUsername().trim() + "%");
        }

        if (queryRequest.getOperationModule() != null && !queryRequest.getOperationModule().trim().isEmpty()) {
            whereSql.append(" AND operation_module = :operationModule ");
            params.put("operationModule", queryRequest.getOperationModule());
        }

        if (queryRequest.getOperationStatus() != null) {
            whereSql.append(" AND operation_status = :operationStatus ");
            params.put("operationStatus", queryRequest.getOperationStatus());
        }

        if (queryRequest.getStartTime() != null) {
            whereSql.append(" AND create_time >= :startTime ");
            params.put("startTime", queryRequest.getStartTime());
        }

        if (queryRequest.getEndTime() != null) {
            whereSql.append(" AND create_time <= :endTime ");
            params.put("endTime", queryRequest.getEndTime());
        }

        String countSql = "SELECT COUNT(*) FROM log_operation" + whereSql;
        return jdbcManager.queryLong(countSql, params);
    }
}
