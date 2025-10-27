package cn.refinex.kb.repository;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.kb.entity.ContentTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内容标签数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ContentTagRepository {

    private final JdbcTemplateManager jdbcManager;

    // ================= Read =================

    /**
     * 根据 ID 查询标签
     *
     * @param id 标签ID
     * @return 标签
     */
    public ContentTag selectById(Long id) {
        String sql = """
                SELECT *
                FROM content_tag
                WHERE id = :id AND deleted = 0
                """;

        try {
            return jdbcManager.queryObject(sql, Map.of("id", id), true, ContentTag.class);
        } catch (EmptyResultDataAccessException e) {
            log.error("查询标签失败，id: {}", id, e);
            return null;
        }
    }

    /**
     * 根据创建者ID和标签名查询标签
     *
     * @param creatorId 创建者ID
     * @param tagName   标签名称
     * @return 标签
     */
    public ContentTag selectByCreatorAndName(Long creatorId, String tagName) {
        String sql = """
                SELECT *
                FROM content_tag
                WHERE creator_id = :creatorId AND tag_name = :tagName AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("creatorId", creatorId);
        params.put("tagName", tagName);

        try {
            return jdbcManager.queryObject(sql, params, true, ContentTag.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 根据创建者ID查询所有标签
     *
     * @param creatorId 创建者ID（系统标签传0）
     * @return 标签列表
     */
    public List<ContentTag> listByCreator(Long creatorId) {
        String sql = """
                SELECT *
                FROM content_tag
                WHERE creator_id = :creatorId AND deleted = 0 AND status = 0
                ORDER BY usage_count DESC, create_time DESC
                """;

        try {
            return jdbcManager.queryList(sql, Map.of("creatorId", creatorId), true, ContentTag.class);
        } catch (Exception e) {
            log.error("根据创建者查询标签失败，creatorId: {}", creatorId, e);
            return List.of();
        }
    }

    /**
     * 查询系统标签（所有人共享）
     *
     * @return 系统标签列表
     */
    public List<ContentTag> listSystemTags() {
        String sql = """
                SELECT *
                FROM content_tag
                WHERE tag_type = 0 AND deleted = 0 AND status = 0
                ORDER BY usage_count DESC, create_time DESC
                """;

        try {
            return jdbcManager.queryList(sql, Map.of(), true, ContentTag.class);
        } catch (Exception e) {
            log.error("查询系统标签失败", e);
            return List.of();
        }
    }

    /**
     * 分页查询标签（管理端 - 所有用户标签）
     *
     * @param tagName     标签名称（可选）
     * @param tagType     标签类型（可选）
     * @param creatorId   创建者ID（可选）
     * @param pageRequest 分页参数
     * @return 标签分页结果
     */
    public PageResult<ContentTag> pageQueryAll(String tagName, Integer tagType, Long creatorId, PageRequest pageRequest) {
        StringBuilder sql = new StringBuilder("""
                SELECT *
                FROM content_tag
                WHERE deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();

        if (tagName != null && !tagName.isEmpty()) {
            sql.append(" AND tag_name LIKE :tagName");
            params.put("tagName", "%" + tagName + "%");
        }
        if (tagType != null) {
            sql.append(" AND tag_type = :tagType");
            params.put("tagType", tagType);
        }
        if (creatorId != null) {
            sql.append(" AND creator_id = :creatorId");
            params.put("creatorId", creatorId);
        }

        sql.append(" ORDER BY usage_count DESC, create_time DESC");
        return jdbcManager.queryPage(sql.toString(), params, pageRequest, ContentTag.class);
    }

    /**
     * 分页查询标签（用户端 - 仅当前用户标签）
     *
     * @param creatorId   创建者ID
     * @param tagName     标签名称（可选）
     * @param pageRequest 分页参数
     * @return 标签分页结果
     */
    public PageResult<ContentTag> pageQueryByCreator(Long creatorId, String tagName, PageRequest pageRequest) {
        StringBuilder sql = new StringBuilder("""
                SELECT *
                FROM content_tag
                WHERE creator_id = :creatorId AND deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();
        params.put("creatorId", creatorId);

        if (tagName != null && !tagName.isEmpty()) {
            sql.append(" AND tag_name LIKE :tagName");
            params.put("tagName", "%" + tagName + "%");
        }

        sql.append(" ORDER BY usage_count DESC, create_time DESC");
        return jdbcManager.queryPage(sql.toString(), params, pageRequest, ContentTag.class);
    }

    /**
     * 检查标签名是否存在（同一创建者下，排除指定ID）
     *
     * @param creatorId 创建者ID
     * @param tagName   标签名称
     * @param excludeId 排除的标签ID
     * @return 是否存在
     */
    public boolean existsName(Long creatorId, String tagName, Long excludeId) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(1) FROM content_tag 
                WHERE creator_id = :creatorId AND tag_name = :tagName AND deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();
        params.put("creatorId", creatorId);
        params.put("tagName", tagName);

        if (excludeId != null) {
            sql.append(" AND id <> :excludeId");
            params.put("excludeId", excludeId);
        }

        Integer c = jdbcManager.queryInt(sql.toString(), params);
        return c != null && c > 0;
    }

    /**
     * 增加标签使用次数
     *
     * @param tagId 标签ID
     * @return 影响行数
     */
    public int incrementUsageCount(Long tagId) {
        String sql = """
                UPDATE content_tag SET
                    usage_count = usage_count + 1,
                    update_time = :updateTime
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", tagId);
        params.put("updateTime", LocalDateTime.now());

        return jdbcManager.update(sql, params);
    }

    /**
     * 减少标签使用次数
     *
     * @param tagId 标签ID
     * @return 影响行数
     */
    public int decrementUsageCount(Long tagId) {
        String sql = """
                UPDATE content_tag SET
                    usage_count = GREATEST(usage_count - 1, 0),
                    update_time = :updateTime
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", tagId);
        params.put("updateTime", LocalDateTime.now());

        return jdbcManager.update(sql, params);
    }

    // ================= Write (tx-managed) =================

    /**
     * 插入标签
     *
     * @param tx  事务管理器
     * @param tag 标签
     * @return 标签ID
     */
    public long insert(JdbcTemplateManager tx, ContentTag tag) {
        String sql = """
                INSERT INTO content_tag (
                    tag_name, tag_color, tag_type, usage_count, creator_id,
                    create_by, create_time, update_by, update_time, remark, status
                ) VALUES (
                    :tagName, :tagColor, :tagType, :usageCount, :creatorId,
                    :createBy, :createTime, :updateBy, :updateTime, :remark, :status
                )
                """;

        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(tag);
        return tx.insertAndGetKey(sql, params);
    }

    /**
     * 根据 ID 更新标签
     *
     * @param tx  事务管理器
     * @param tag 标签
     * @return 影响行数
     */
    public int updateById(JdbcTemplateManager tx, ContentTag tag) {
        String sql = """
                UPDATE content_tag SET
                    tag_name    = :tagName,
                    tag_color   = :tagColor,
                    tag_type    = :tagType,
                    update_by   = :updateBy,
                    update_time = :updateTime,
                    version     = :version,
                    remark      = :remark,
                    status      = :status
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = BeanConverter.beanToMap(tag, false, false);
        return tx.update(sql, params);
    }

    /**
     * 根据 ID 软删除标签
     *
     * @param tx         事务管理器
     * @param id         标签 ID
     * @param operatorId 操作人 ID
     * @return 影响行数
     */
    public int softDeleteById(JdbcTemplateManager tx, Long id, Long operatorId) {
        String sql = """
                UPDATE content_tag SET
                    deleted = 1,
                    update_by = :updateBy,
                    update_time = :updateTime
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("updateBy", operatorId);
        params.put("updateTime", LocalDateTime.now());

        return tx.update(sql, params);
    }

    /**
     * 根据 ID 批量软删除标签
     *
     * @param tx         事务管理器
     * @param ids        标签 ID 列表
     * @param operatorId 操作人 ID
     * @return 影响行数
     */
    public int batchSoftDelete(JdbcTemplateManager tx, List<Long> ids, Long operatorId) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        String sql = """
                UPDATE content_tag SET
                    deleted = 1,
                    update_by = :updateBy,
                    update_time = :updateTime
                WHERE id IN (:ids) AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        params.put("updateBy", operatorId);
        params.put("updateTime", LocalDateTime.now());

        return tx.update(sql, params);
    }
}

