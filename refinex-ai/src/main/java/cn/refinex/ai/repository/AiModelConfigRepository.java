package cn.refinex.ai.repository;

import cn.refinex.ai.entity.AiModelConfig;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.utils.object.BeanConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 模型配置数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class AiModelConfigRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 根据模型编码查询配置
     *
     * @param modelCode 模型编码
     * @return 模型配置
     */
    public AiModelConfig findByModelCode(String modelCode) {
        String sql = """
                SELECT * FROM ai_model_config
                WHERE model_code = :modelCode AND is_enabled = 1 AND deleted = 0
                LIMIT 1
                """;

        Map<String, Object> params = Map.of("modelCode", modelCode);
        return jdbcManager.queryObject(sql, params, AiModelConfig.class);
    }

    /**
     * 根据 ID 查询配置
     *
     * @param id 配置 ID
     * @return 模型配置
     */
    public AiModelConfig findById(Long id) {
        String sql = """
                SELECT * FROM ai_model_config
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = Map.of("id", id);
        return jdbcManager.queryObject(sql, params, AiModelConfig.class);
    }

    /**
     * 根据供应商和模型类型查询配置列表
     *
     * @param provider  供应商
     * @param modelType 模型类型
     * @return 配置列表
     */
    public List<AiModelConfig> findByProviderAndType(String provider, String modelType) {
        String sql = """
                SELECT * FROM ai_model_config
                WHERE provider = :provider AND model_type = :modelType 
                    AND is_enabled = 1 AND deleted = 0
                ORDER BY priority DESC, id ASC
                """;

        Map<String, Object> params = Map.of(
                "provider", provider,
                "modelType", modelType
        );
        return jdbcManager.queryList(sql, params, AiModelConfig.class);
    }

    /**
     * 查询所有启用的模型配置
     *
     * @return 配置列表
     */
    public List<AiModelConfig> findAllEnabled() {
        String sql = """
                SELECT * FROM ai_model_config
                WHERE is_enabled = 1 AND deleted = 0
                ORDER BY priority DESC, id ASC
                """;

        return jdbcManager.queryList(sql, Map.of(), AiModelConfig.class);
    }

    /**
     * 查询健康的模型配置
     *
     * @param modelType 模型类型
     * @return 配置列表
     */
    public List<AiModelConfig> findHealthyByType(String modelType) {
        String sql = """
                SELECT * FROM ai_model_config
                WHERE model_type = :modelType
                    AND is_enabled = 1
                    AND health_status = 1
                    AND deleted = 0
                ORDER BY priority DESC, id ASC
                """;

        Map<String, Object> params = Map.of("modelType", modelType);
        return jdbcManager.queryList(sql, params, AiModelConfig.class);
    }

    /**
     * 更新模型健康状态
     *
     * @param id           配置 ID
     * @param healthStatus 健康状态（0-异常，1-正常）
     * @return 更新行数
     */
    public int updateHealthStatus(Long id, Integer healthStatus) {
        String sql = """
                UPDATE ai_model_config
                SET health_status          = :healthStatus,
                    last_health_check_time = NOW()
                WHERE id = :id
                """;

        Map<String, Object> params = Map.of(
                "id", id,
                "healthStatus", healthStatus
        );
        return jdbcManager.update(sql, params);
    }

    /**
     * 根据模型编码查询降级模型配置
     *
     * @param modelCode 主模型编码
     * @return 降级模型配置
     */
    public AiModelConfig findFallbackModel(String modelCode) {
        String sql = """
                SELECT fb.* FROM ai_model_config main
                INNER JOIN ai_model_config fb ON main.fallback_model_code = fb.model_code
                WHERE main.model_code = :modelCode
                    AND fb.is_enabled = 1
                    AND fb.health_status = 1
                    AND fb.deleted = 0
                LIMIT 1
                """;

        Map<String, Object> params = Map.of("modelCode", modelCode);
        return jdbcManager.queryObject(sql, params, AiModelConfig.class);
    }

    /**
     * 插入模型配置
     *
     * @param config 模型配置
     * @return 影响行数
     */
    public long insert(AiModelConfig config) {
        String sql = """
                INSERT INTO ai_model_config (
                    model_code, model_version, model_name, provider, model_type,
                    api_endpoint, api_key, api_version, model_capabilities,
                    context_window, max_tokens, temperature, pricing_input, pricing_output,
                    rpm_limit, tpm_limit, timeout_seconds, retry_times, circuit_breaker_threshold,
                    fallback_model_code, health_check_url, health_status, is_enabled, priority,
                    create_by, create_time, update_by, update_time, deleted, version, remark, sort, status, extra_data
                ) VALUES (
                    :modelCode, :modelVersion, :modelName, :provider, :modelType,
                    :apiEndpoint, :apiKey, :apiVersion, :modelCapabilities,
                    :contextWindow, :maxTokens, :temperature, :pricingInput, :pricingOutput,
                    :rpmLimit, :tpmLimit, :timeoutSeconds, :retryTimes, :circuitBreakerThreshold,
                    :fallbackModelCode, :healthCheckUrl, :healthStatus, :isEnabled, :priority,
                    :createBy, :createTime, :updateBy, :updateTime, :deleted, :version, :remark, :sort, :status, :extraData
                )
                """;

        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(config);
        return jdbcManager.insertAndGetKey(sql, params);
    }

    /**
     * 更新模型配置
     *
     * @param config 模型配置
     * @return 影响行数
     */
    public int update(AiModelConfig config) {
        String sql = """
                UPDATE ai_model_config SET
                    model_version              = :modelVersion,
                    model_name                 = :modelName,
                    provider                   = :provider,
                    model_type                 = :modelType,
                    api_endpoint               = :apiEndpoint,
                    api_key                    = :apiKey,
                    api_version                = :apiVersion,
                    model_capabilities         = :modelCapabilities,
                    context_window             = :contextWindow,
                    max_tokens                 = :maxTokens,
                    temperature                = :temperature,
                    pricing_input              = :pricingInput,
                    pricing_output             = :pricingOutput,
                    rpm_limit                  = :rpmLimit,
                    tpm_limit                  = :tpmLimit,
                    timeout_seconds            = :timeoutSeconds,
                    retry_times                = :retryTimes,
                    circuit_breaker_threshold  = :circuitBreakerThreshold,
                    fallback_model_code        = :fallbackModelCode,
                    health_check_url           = :healthCheckUrl,
                    priority                   = :priority,
                    update_by                  = :updateBy,
                    update_time                = :updateTime,
                    remark                     = :remark,
                    sort                       = :sort,
                    status                     = :status,
                    extra_data                 = :extraData,
                    version                    = version + 1
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = BeanConverter.beanToMap(config, false, false);
        return jdbcManager.update(sql, params);
    }

    /**
     * 软删除模型配置
     *
     * @param id       配置 ID
     * @param updateBy 更新人 ID
     * @return 影响行数
     */
    public int deleteById(Long id, Long updateBy) {
        String sql = """
                UPDATE ai_model_config SET
                    deleted = 1,
                    update_by = :updateBy,
                    update_time = :updateTime
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = Map.of(
                "id", id,
                "updateBy", updateBy,
                "updateTime", LocalDateTime.now()
        );
        return jdbcManager.update(sql, params);
    }

    /**
     * 分页查询模型配置
     *
     * @param provider    供应商（可选）
     * @param modelType   模型类型（可选）
     * @param status      状态（可选）
     * @param keyword     关键词（可选，搜索模型名称和编码）
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    public PageResult<AiModelConfig> pageQuery(String provider, String modelType, Integer status, String keyword, PageRequest pageRequest) {
        StringBuilder sql = new StringBuilder("""
                SELECT *
                FROM ai_model_config
                WHERE deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();

        if (provider != null && !provider.isEmpty()) {
            sql.append(" AND provider = :provider");
            params.put("provider", provider);
        }

        if (modelType != null && !modelType.isEmpty()) {
            sql.append(" AND model_type = :modelType");
            params.put("modelType", modelType);
        }

        if (status != null) {
            sql.append(" AND status = :status");
            params.put("status", status);
        }

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (model_name LIKE :keyword OR model_code LIKE :keyword)");
            params.put("keyword", "%" + keyword + "%");
        }

        // 默认排序：优先级降序，ID降序
        if (pageRequest.getOrderBy() == null || pageRequest.getOrderBy().isEmpty()) {
            sql.append(" ORDER BY priority DESC, id DESC");
        }

        return jdbcManager.queryPage(sql.toString(), params, pageRequest, AiModelConfig.class);
    }

    /**
     * 查询所有模型配置（用于下拉选择）
     *
     * @return 配置列表
     */
    public List<AiModelConfig> findAll() {
        String sql = """
                SELECT * FROM ai_model_config
                WHERE deleted = 0
                ORDER BY priority DESC, sort ASC, id DESC
                """;

        return jdbcManager.queryList(sql, Map.of(), AiModelConfig.class);
    }

    /**
     * 检查模型编码是否存在
     *
     * @param modelCode 模型编码
     * @param excludeId 排除的 ID（用于更新时检查）
     * @return 是否存在
     */
    public boolean checkModelCodeExists(String modelCode, Long excludeId) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) FROM ai_model_config
                WHERE model_code = :modelCode AND deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();
        params.put("modelCode", modelCode);

        if (excludeId != null) {
            sql.append(" AND id != :excludeId");
            params.put("excludeId", excludeId);
        }

        Long count = jdbcManager.queryLong(sql.toString(), params);
        return count != null && count > 0;
    }

    /**
     * 更新模型状态
     *
     * @param id       配置 ID
     * @param status   状态
     * @param updateBy 更新人 ID
     * @return 影响行数
     */
    public int updateStatus(Long id, Integer status, Long updateBy) {
        String sql = """
                UPDATE ai_model_config SET
                    status      = :status,
                    update_by   = :updateBy,
                    update_time = :updateTime
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = Map.of(
                "id", id,
                "status", status,
                "updateBy", updateBy,
                "updateTime", LocalDateTime.now()
        );
        return jdbcManager.update(sql, params);
    }
}

