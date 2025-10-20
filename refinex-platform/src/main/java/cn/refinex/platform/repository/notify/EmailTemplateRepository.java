package cn.refinex.platform.repository.notify;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.mail.domain.entity.EmailTemplate;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.domain.dto.request.EmailTemplateQueryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统邮件模板数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class EmailTemplateRepository {

    private final JdbcTemplateManager jdbcManager;

    // ================= Read =================

    /**
     * 根据ID查询邮件模板
     *
     * @param id 邮件模板ID
     * @return 邮件模板
     */
    public EmailTemplate selectById(Long id) {
        String sql = """
                SELECT *
                FROM email_template
                WHERE id = :id AND deleted = 0
                """;

        try {
            return jdbcManager.queryObject(sql, Map.of("id", id), true, EmailTemplate.class);
        } catch (EmptyResultDataAccessException e) {
            log.error("查询邮件模板失败，id: {}", id, e);
            return null;
        }
    }

    /**
     * 根据编码查询邮件模板
     *
     * @param templateCode 邮件模板编码
     * @return 邮件模板
     */
    public EmailTemplate selectByCode(String templateCode) {
        String sql = """
                SELECT *
                FROM email_template
                WHERE template_code = :code AND deleted = 0
                """;

        try {
            return jdbcManager.queryObject(sql, Map.of("code", templateCode), true, EmailTemplate.class);
        } catch (EmptyResultDataAccessException e) {
            log.error("根据编码查询模板失败，code: {}", templateCode, e);
            return null;
        }
    }

    /**
     * 检查邮件模板编码是否存在（排除指定ID）
     *
     * @param templateCode 邮件模板编码
     * @param excludeId    排除的邮件模板ID
     * @return 是否存在
     */
    public boolean existsByCode(String templateCode, Long excludeId) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(1)
                FROM email_template
                WHERE template_code = :code AND deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();
        params.put("code", templateCode);

        if (excludeId != null) {
            sql.append(" AND id <> :excludeId");
            params.put("excludeId", excludeId);
        }

        Integer c = jdbcManager.queryInt(sql.toString(), params);
        return c != null && c > 0;
    }

    /**
     * 查询所有启用的邮件模板
     *
     * @return 邮件模板列表
     */
    public List<EmailTemplate> selectEnabledList() {
        String sql = """
                SELECT *
                FROM email_template
                WHERE deleted = 0 AND status = 0
                ORDER BY id DESC
                """;

        return jdbcManager.queryList(sql, Map.of(), true, EmailTemplate.class);
    }

    /**
     * 根据分类查询启用的邮件模板
     *
     * @param category 邮件模板分类
     * @return 邮件模板列表
     */
    public List<EmailTemplate> listByCategory(String category) {
        String sql = """
                SELECT *
                FROM email_template
                WHERE template_category = :category AND deleted = 0 AND status = 0
                ORDER BY id DESC
                """;

        return jdbcManager.queryList(sql, Map.of("category", category), true, EmailTemplate.class);
    }

    /**
     * 分页查询邮件模板
     *
     * @param query      查询参数
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    public PageResult<EmailTemplate> pageQuery(EmailTemplateQueryRequest query, PageRequest pageRequest) {
        StringBuilder sql = new StringBuilder("SELECT * FROM email_template WHERE deleted = 0");
        Map<String, Object> params = new HashMap<>();

        if (query != null) {
            if (query.getTemplateCode() != null && !query.getTemplateCode().isEmpty()) {
                sql.append(" AND template_code LIKE :templateCode");
                params.put("templateCode", "%" + query.getTemplateCode() + "%");
            }
            if (query.getTemplateName() != null && !query.getTemplateName().isEmpty()) {
                sql.append(" AND template_name LIKE :templateName");
                params.put("templateName", "%" + query.getTemplateName() + "%");
            }
            if (query.getTemplateType() != null && !query.getTemplateType().isEmpty()) {
                sql.append(" AND template_type = :templateType");
                params.put("templateType", query.getTemplateType());
            }
            if (query.getTemplateCategory() != null && !query.getTemplateCategory().isEmpty()) {
                sql.append(" AND template_category = :templateCategory");
                params.put("templateCategory", query.getTemplateCategory());
            }
            if (query.getIsSystem() != null) {
                sql.append(" AND is_system = :isSystem");
                params.put("isSystem", query.getIsSystem());
            }
            if (query.getStatus() != null) {
                sql.append(" AND status = :status");
                params.put("status", query.getStatus());
            }
        }

        sql.append(" ORDER BY id DESC");

        return jdbcManager.queryPage(sql.toString(), params, pageRequest, EmailTemplate.class);
    }

    // ================= Write (tx-managed) =================

    /**
     * 插入邮件模板
     *
     * @param tx 事务管理器
     * @param t  邮件模板实体
     * @return 影响行数
     */
    public int insert(JdbcTemplateManager tx, EmailTemplate t) {
        String sql = """
                INSERT INTO email_template (
                    id, template_code, template_name, template_subject, template_content, template_type,
                    variables, template_category, is_system, create_by, create_time, update_by, update_time,
                    deleted, version, remark, status
                ) VALUES (
                    :id, :templateCode, :templateName, :templateSubject, :templateContent, :templateType,
                    :variables, :templateCategory, :isSystem, :createBy, :createTime, :updateBy, :updateTime,
                    :deleted, :version, :remark, :status
                )
                """;

        Map<String, Object> params = BeanConverter.beanToMap(t, false, false);
        return tx.insert(sql, params);
    }

    /**
     * 根据ID更新邮件模板
     *
     * @param tx 事务管理器
     * @param t  邮件模板实体
     * @return 影响行数
     */
    public int updateById(JdbcTemplateManager tx, EmailTemplate t) {
        String sql = """
                UPDATE email_template SET
                    template_name     = :templateName,
                    template_subject  = :templateSubject,
                    template_content  = :templateContent,
                    template_type     = :templateType,
                    variables         = :variables,
                    template_category = :templateCategory,
                    is_system         = :isSystem,
                    update_by         = :updateBy,
                    update_time       = :updateTime,
                    version           = :version,
                    remark            = :remark,
                    status            = :status
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = BeanConverter.beanToMap(t, false, false);
        return tx.update(sql, params);
    }

    /**
     * 根据ID软删除邮件模板
     *
     * @param tx        事务管理器
     * @param id        邮件模板ID
     * @param operatorId 操作人ID
     * @return 影响行数
     */
    public int softDeleteById(JdbcTemplateManager tx, Long id, Long operatorId) {
        String sql = """
                UPDATE email_template SET
                    deleted         = 1,
                    update_by       = :updateBy,
                    update_time     = :updateTime
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("updateBy", operatorId);
        params.put("updateTime", LocalDateTime.now());

        return tx.update(sql, params);
    }

    /**
     * 根据ID更新邮件模板状态
     *
     * @param tx        事务管理器
     * @param id        邮件模板ID
     * @param status    新状态
     * @param operatorId 操作人ID
     * @return 影响行数
     */
    public int updateStatus(JdbcTemplateManager tx, Long id, Integer status, Long operatorId) {
        String sql = """
                UPDATE email_template SET
                    status         = :status,
                    update_by      = :updateBy,
                    update_time    = :updateTime
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("status", status);
        params.put("updateBy", operatorId);
        params.put("updateTime", LocalDateTime.now());

        return tx.update(sql, params);
    }
}


