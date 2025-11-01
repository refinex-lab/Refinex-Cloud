package cn.refinex.ai.repository;

import cn.refinex.ai.entity.AiPromptTemplate;
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
 * AI 提示词模板数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class AiPromptTemplateRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 根据 ID 查询模板
     *
     * @param id 模板 ID
     * @return 提示词模板
     */
    public AiPromptTemplate findById(Long id) {
        String sql = """
                SELECT * FROM ai_prompt_template
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = Map.of("id", id);
        return jdbcManager.queryObject(sql, params, AiPromptTemplate.class);
    }

    /**
     * 根据模板编码查询模板
     *
     * @param templateCode 模板编码
     * @return 提示词模板
     */
    public AiPromptTemplate findByCode(String templateCode) {
        String sql = """
                SELECT * FROM ai_prompt_template
                WHERE template_code = :templateCode AND deleted = 0
                LIMIT 1
                """;

        Map<String, Object> params = Map.of("templateCode", templateCode);
        return jdbcManager.queryObject(sql, params, AiPromptTemplate.class);
    }

    /**
     * 插入提示词模板
     *
     * @param template 提示词模板
     * @return 影响行数
     */
    public long insert(AiPromptTemplate template) {
        String sql = """
                INSERT INTO ai_prompt_template (
                    template_code, version_number, parent_template_id, template_name, template_content,
                    template_type, template_category, applicable_models, is_system, is_public, creator_id,
                    usage_count, like_count, avg_token_usage, avg_cost, success_rate, avg_satisfaction,
                    create_by, create_time, update_by, update_time, deleted, version, remark, sort, status, extra_data
                ) VALUES (
                    :templateCode, :versionNumber, :parentTemplateId, :templateName, :templateContent,
                    :templateType, :templateCategory, :applicableModels, :isSystem, :isPublic, :creatorId,
                    :usageCount, :likeCount, :avgTokenUsage, :avgCost, :successRate, :avgSatisfaction,
                    :createBy, :createTime, :updateBy, :updateTime, :deleted, :version, :remark, :sort, :status, :extraData
                )
                """;

        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(template);
        return jdbcManager.insertAndGetKey(sql, params);
    }

    /**
     * 更新提示词模板
     *
     * @param template 提示词模板
     * @return 影响行数
     */
    public int update(AiPromptTemplate template) {
        String sql = """
                UPDATE ai_prompt_template SET
                    template_name     = :templateName,
                    template_content  = :templateContent,
                    template_type     = :templateType,
                    template_category = :templateCategory,
                    applicable_models = :applicableModels,
                    is_public         = :isPublic,
                    update_by         = :updateBy,
                    update_time       = :updateTime,
                    remark            = :remark,
                    sort              = :sort,
                    status            = :status,
                    extra_data        = :extraData,
                    version           = version + 1
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = BeanConverter.beanToMap(template, false, false);
        return jdbcManager.update(sql, params);
    }

    /**
     * 软删除提示词模板
     *
     * @param id       模板 ID
     * @param updateBy 更新人 ID
     * @return 影响行数
     */
    public int deleteById(Long id, Long updateBy) {
        String sql = """
                UPDATE ai_prompt_template SET
                    deleted     = 1,
                    update_by   = :updateBy,
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
     * 分页查询提示词模板
     *
     * @param category    模板分类（可选）
     * @param type        模板类型（可选）
     * @param isPublic    是否公开（可选）
     * @param status      状态（可选）
     * @param keyword     关键词（可选，搜索模板名称和编码）
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    public PageResult<AiPromptTemplate> pageQuery(String category, String type, Integer isPublic, Integer status, String keyword, PageRequest pageRequest) {
        StringBuilder sql = new StringBuilder("""
                SELECT *
                FROM ai_prompt_template
                WHERE deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();

        if (category != null && !category.isEmpty()) {
            sql.append(" AND template_category = :category");
            params.put("category", category);
        }

        if (type != null && !type.isEmpty()) {
            sql.append(" AND template_type = :type");
            params.put("type", type);
        }

        if (isPublic != null) {
            sql.append(" AND is_public = :isPublic");
            params.put("isPublic", isPublic);
        }

        if (status != null) {
            sql.append(" AND status = :status");
            params.put("status", status);
        }

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (template_name LIKE :keyword OR template_code LIKE :keyword)");
            params.put("keyword", "%" + keyword + "%");
        }

        // 默认排序：排序字段升序，ID降序
        if (pageRequest.getOrderBy() == null || pageRequest.getOrderBy().isEmpty()) {
            sql.append(" ORDER BY sort ASC, id DESC");
        }

        return jdbcManager.queryPage(sql.toString(), params, pageRequest, AiPromptTemplate.class);
    }

    /**
     * 查询所有提示词模板（用于下拉选择）
     *
     * @return 模板列表
     */
    public List<AiPromptTemplate> findAll() {
        String sql = """
                SELECT * FROM ai_prompt_template
                WHERE deleted = 0
                ORDER BY sort ASC, id DESC
                """;

        return jdbcManager.queryList(sql, Map.of(), AiPromptTemplate.class);
    }

    /**
     * 检查模板编码是否存在
     *
     * @param templateCode 模板编码
     * @param excludeId    排除的 ID（用于更新时检查）
     * @return 是否存在
     */
    public boolean checkTemplateCodeExists(String templateCode, Long excludeId) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) FROM ai_prompt_template
                WHERE template_code = :templateCode AND deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();
        params.put("templateCode", templateCode);

        if (excludeId != null) {
            sql.append(" AND id != :excludeId");
            params.put("excludeId", excludeId);
        }

        Long count = jdbcManager.queryObject(sql.toString(), params, Long.class);
        return count != null && count > 0;
    }

    /**
     * 增加使用次数
     *
     * @param id 模板 ID
     * @return 影响行数
     */
    public int incrementUsageCount(Long id) {
        String sql = """
                UPDATE ai_prompt_template
                SET usage_count = usage_count + 1
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = Map.of("id", id);
        return jdbcManager.update(sql, params);
    }

    /**
     * 更新模板状态
     *
     * @param id       模板 ID
     * @param status   状态
     * @param updateBy 更新人 ID
     * @return 影响行数
     */
    public int updateStatus(Long id, Integer status, Long updateBy) {
        String sql = """
                UPDATE ai_prompt_template SET
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

