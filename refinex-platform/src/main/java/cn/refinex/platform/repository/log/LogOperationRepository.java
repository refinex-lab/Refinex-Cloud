package cn.refinex.platform.repository.log;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.domain.entity.log.LogOperation;
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
                    user_id, username, operation_module, operation_type, operation_desc, request_method, request_url
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
}
