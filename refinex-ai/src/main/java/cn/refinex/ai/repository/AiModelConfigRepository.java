package cn.refinex.ai.repository;

import cn.refinex.ai.entity.AiModelConfig;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

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
            SET health_status = :healthStatus,
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
}

