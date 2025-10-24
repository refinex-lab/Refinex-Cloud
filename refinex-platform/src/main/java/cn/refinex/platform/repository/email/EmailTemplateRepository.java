package cn.refinex.platform.repository.email;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.platform.domain.entity.email.EmailTemplate;
import cn.refinex.common.utils.object.BeanConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 邮件模板 Repository
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class EmailTemplateRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 插入邮件模板
     *
     * @param template 邮件模板
     * @return 影响行数
     */
    public int insert(EmailTemplate template) {
        String sql = """
            INSERT INTO email_template (
                id, template_code, template_name, template_subject, template_content,
                template_type, variables, template_category, is_system,
                create_by, create_time, update_by, update_time, deleted, version, remark, status
            ) VALUES (
                :id, :templateCode, :templateName, :templateSubject, :templateContent,
                :templateType, :variables, :templateCategory, :isSystem,
                :createBy, :createTime, :updateBy, :updateTime, :deleted, :version, :remark, :status
            )
            """;

        Map<String, Object> params = BeanConverter.beanToMap(template, false, false);
        return jdbcManager.insert(sql, params);
    }

    /**
     * 更新邮件模板
     *
     * @param template 邮件模板
     * @return 影响行数
     */
    public int update(EmailTemplate template) {
        String sql = """
            UPDATE email_template
            SET template_name = :templateName, template_subject = :templateSubject,
                template_content = :templateContent, template_type = :templateType,
                variables = :variables, template_category = :templateCategory,
                update_by = :updateBy, update_time = :updateTime,
                version = version + 1, remark = :remark, status = :status
            WHERE id = :id AND deleted = 0
            """;

        Map<String, Object> params = BeanConverter.beanToMap(template, false, false);
        return jdbcManager.update(sql, params);
    }

    /**
     * 根据模板编码查询模板
     *
     * @param templateCode 模板编码
     * @return 邮件模板
     */
    public EmailTemplate findByTemplateCode(String templateCode) {
        String sql = """
            SELECT * FROM email_template
            WHERE template_code = :templateCode AND deleted = 0
            """;

        Map<String, Object> params = Map.of("templateCode", templateCode);
        return jdbcManager.queryObject(sql, params, EmailTemplate.class);
    }

    /**
     * 根据 ID 查询模板
     *
     * @param id 模板 ID
     * @return 邮件模板
     */
    public EmailTemplate findById(Long id) {
        String sql = """
            SELECT * FROM email_template
            WHERE id = :id AND deleted = 0
            """;

        Map<String, Object> params = Map.of("id", id);
        return jdbcManager.queryObject(sql, params, EmailTemplate.class);
    }

    /**
     * 根据分类查询模板列表
     *
     * @param category 模板分类
     * @return 邮件模板列表
     */
    public List<EmailTemplate> findByCategory(String category) {
        String sql = """
            SELECT * FROM email_template
            WHERE template_category = :category AND deleted = 0 AND status = 0
            ORDER BY create_time DESC
            """;

        Map<String, Object> params = Map.of("category", category);
        return jdbcManager.queryList(sql, params, EmailTemplate.class);
    }

    /**
     * 查询所有可用模板
     *
     * @return 邮件模板列表
     */
    public List<EmailTemplate> findAllAvailable() {
        String sql = """
            SELECT * FROM email_template
            WHERE deleted = 0 AND status = 0
            ORDER BY template_category, create_time DESC
            """;

        return jdbcManager.queryList(sql, Map.of(), EmailTemplate.class);
    }

    /**
     * 逻辑删除模板
     *
     * @param id 模板 ID
     * @return 影响行数
     */
    public int deleteById(Long id) {
        String sql = """
            UPDATE email_template
            SET deleted = 1, update_time = NOW()
            WHERE id = :id AND deleted = 0
            """;

        Map<String, Object> params = Map.of("id", id);
        return jdbcManager.update(sql, params);
    }

    /**
     * 更新模板状态
     *
     * @param id     模板 ID
     * @param status 状态（0正常、1停用）
     * @return 影响行数
     */
    public int updateStatus(Long id, Integer status) {
        String sql = """
            UPDATE email_template
            SET status = :status, update_time = NOW()
            WHERE id = :id AND deleted = 0
            """;

        Map<String, Object> params = Map.of("id", id, "status", status);
        return jdbcManager.update(sql, params);
    }

    /**
     * 检查模板编码是否存在
     *
     * @param templateCode 模板编码
     * @return 是否存在
     */
    public boolean existsByTemplateCode(String templateCode) {
        String sql = """
            SELECT COUNT(*) FROM email_template
            WHERE template_code = :templateCode AND deleted = 0
            """;

        Map<String, Object> params = Map.of("templateCode", templateCode);
        Integer count = jdbcManager.queryObject(sql, params, Integer.class);
        return count != null && count > 0;
    }
}

