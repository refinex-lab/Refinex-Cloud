package cn.refinex.kb.repository;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.kb.entity.ContentDirectory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内容目录数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ContentDirectoryRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 插入目录
     *
     * @param directory 目录实体
     * @return 目录ID
     */
    public Long insert(ContentDirectory directory) {
        String sql = """
                INSERT INTO content_directory (
                    space_id, parent_id, directory_name, directory_path, depth_level,
                    create_by, create_time, update_by, update_time,
                    deleted, version, remark, sort, status, extra_data
                ) VALUES (
                    :spaceId, :parentId, :directoryName, :directoryPath, :depthLevel,
                    :createBy, :createTime, :updateBy, :updateTime,
                    :deleted, :version, :remark, :sort, :status, :extraData
                )
                """;

        BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(directory);
        return jdbcManager.insertAndGetKey(sql, paramSource);
    }

    /**
     * 根据ID更新目录
     *
     * @param directory 目录实体
     * @return 影响行数
     */
    public int update(ContentDirectory directory) {
        String sql = """
                UPDATE content_directory
                SET directory_name = :directoryName,
                    directory_path = :directoryPath,
                    depth_level    = :depthLevel,
                    update_by      = :updateBy,
                    update_time    = :updateTime,
                    remark         = :remark,
                    sort           = :sort,
                    status         = :status,
                    extra_data     = :extraData,
                    version        = version + 1
                WHERE id = :id
                  AND version = :version
                  AND deleted = 0
                """;

        Map<String, Object> params = BeanConverter.beanToMap(directory, false, false);
        return jdbcManager.update(sql, params);
    }

    /**
     * 更新目录排序
     *
     * @param directoryId 目录ID
     * @param sort        排序值
     * @param updateBy    更新人ID
     * @return 影响行数
     */
    public int updateSort(Long directoryId, Integer sort, Long updateBy) {
        String sql = """
                UPDATE content_directory
                SET sort        = :sort,
                    update_by   = :updateBy,
                    update_time = :updateTime
                WHERE id = :directoryId
                  AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("directoryId", directoryId);
        params.put("sort", sort);
        params.put("updateBy", updateBy);
        params.put("updateTime", LocalDateTime.now());

        return jdbcManager.update(sql, params);
    }

    /**
     * 更新目录的父目录和层级
     *
     * @param directoryId 目录ID
     * @param parentId    新的父目录ID
     * @param depthLevel  新的层级
     * @param updateBy    更新人ID
     * @return 影响行数
     */
    public int updateParentAndDepth(Long directoryId, Long parentId, Integer depthLevel, Long updateBy) {
        String sql = """
                UPDATE content_directory
                SET parent_id   = :parentId,
                    depth_level = :depthLevel,
                    update_by   = :updateBy,
                    update_time = :updateTime
                WHERE id = :directoryId
                  AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("directoryId", directoryId);
        params.put("parentId", parentId);
        params.put("depthLevel", depthLevel);
        params.put("updateBy", updateBy);
        params.put("updateTime", LocalDateTime.now());

        return jdbcManager.update(sql, params);
    }

    /**
     * 更新目录路径
     *
     * @param directoryId   目录ID
     * @param directoryPath 新的路径
     * @param updateBy      更新人ID
     * @return 影响行数
     */
    public int updatePath(Long directoryId, String directoryPath, Long updateBy) {
        String sql = """
                UPDATE content_directory
                SET directory_path = :directoryPath,
                    update_by      = :updateBy,
                    update_time    = :updateTime
                WHERE id = :directoryId
                  AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("directoryId", directoryId);
        params.put("directoryPath", directoryPath);
        params.put("updateBy", updateBy);
        params.put("updateTime", LocalDateTime.now());

        return jdbcManager.update(sql, params);
    }

    /**
     * 逻辑删除目录
     *
     * @param directoryId 目录ID
     * @param updateBy    更新人ID
     * @return 影响行数
     */
    public int deleteById(Long directoryId, Long updateBy) {
        String sql = """
                UPDATE content_directory
                SET deleted     = 1,
                    update_by   = :updateBy,
                    update_time = :updateTime
                WHERE id = :directoryId
                  AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("directoryId", directoryId);
        params.put("updateBy", updateBy);
        params.put("updateTime", LocalDateTime.now());

        return jdbcManager.update(sql, params);
    }

    /**
     * 根据ID查询目录
     *
     * @param directoryId 目录ID
     * @return 目录实体
     */
    public ContentDirectory selectById(Long directoryId) {
        String sql = """
                SELECT *
                FROM content_directory
                WHERE id = :directoryId
                  AND deleted = 0
                """;

        Map<String, Object> params = Map.of("directoryId", directoryId);

        try {
            return jdbcManager.queryObject(sql, params, true, ContentDirectory.class);
        } catch (Exception e) {
            log.error("根据ID查询目录失败，directoryId: {}", directoryId, e);
            return null;
        }
    }

    /**
     * 根据空间ID查询所有目录（按层级和排序）
     *
     * @param spaceId 空间ID
     * @return 目录列表
     */
    public List<ContentDirectory> selectBySpaceId(Long spaceId) {
        String sql = """
                SELECT *
                FROM content_directory
                WHERE space_id = :spaceId
                  AND deleted = 0
                ORDER BY parent_id ASC, sort ASC, create_time ASC
                """;

        Map<String, Object> params = Map.of("spaceId", spaceId);

        try {
            return jdbcManager.queryList(sql, params, true, ContentDirectory.class);
        } catch (Exception e) {
            log.error("根据空间ID查询目录列表失败，spaceId: {}", spaceId, e);
            return List.of();
        }
    }

    /**
     * 根据父目录ID查询子目录列表
     *
     * @param spaceId  空间ID
     * @param parentId 父目录ID
     * @return 子目录列表
     */
    public List<ContentDirectory> selectByParentId(Long spaceId, Long parentId) {
        String sql = """
                SELECT *
                FROM content_directory
                WHERE space_id = :spaceId
                  AND parent_id = :parentId
                  AND deleted = 0
                ORDER BY sort ASC, create_time ASC
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("spaceId", spaceId);
        params.put("parentId", parentId);

        try {
            return jdbcManager.queryList(sql, params, true, ContentDirectory.class);
        } catch (Exception e) {
            log.error("根据父目录ID查询子目录列表失败，spaceId: {}, parentId: {}", spaceId, parentId, e);
            return List.of();
        }
    }

    /**
     * 查询目录的所有子孙目录ID（递归）
     *
     * @param directoryId 目录ID
     * @return 子孙目录ID列表
     */
    public List<Long> selectDescendantIds(Long directoryId) {
        String sql = """
                WITH RECURSIVE directory_tree AS (
                    -- 初始查询：选择指定目录的直接子目录
                    SELECT id, parent_id
                    FROM content_directory
                    WHERE parent_id = :directoryId
                      AND deleted = 0
                
                    UNION ALL
                
                    -- 递归查询：选择子目录的子目录
                    SELECT cd.id, cd.parent_id
                    FROM content_directory cd
                    INNER JOIN directory_tree dt ON cd.parent_id = dt.id
                    WHERE cd.deleted = 0
                )
                SELECT id FROM directory_tree
                """;

        Map<String, Object> params = Map.of("directoryId", directoryId);

        try {
            return jdbcManager.queryList(sql, params, Long.class);
        } catch (Exception e) {
            log.error("查询目录的所有子孙目录失败，directoryId: {}", directoryId, e);
            return List.of();
        }
    }

    /**
     * 检查目录名称是否存在（同一空间同一父目录下）
     *
     * @param spaceId       空间ID
     * @param parentId      父目录ID
     * @param directoryName 目录名称
     * @param excludeId     排除的ID（更新时使用）
     * @return 是否存在
     */
    public boolean existsByName(Long spaceId, Long parentId, String directoryName, Long excludeId) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*)
                FROM content_directory
                WHERE space_id = :spaceId
                  AND parent_id = :parentId
                  AND directory_name = :directoryName
                  AND deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();
        params.put("spaceId", spaceId);
        params.put("parentId", parentId);
        params.put("directoryName", directoryName);

        if (excludeId != null) {
            sql.append(" AND id != :excludeId");
            params.put("excludeId", excludeId);
        }

        try {
            return jdbcManager.queryInt(sql.toString(), params) > 0;
        } catch (Exception e) {
            log.error("检查目录名称是否存在失败", e);
            return false;
        }
    }

    /**
     * 统计父目录下的子目录数量
     *
     * @param spaceId  空间ID
     * @param parentId 父目录ID
     * @return 子目录数量
     */
    public long countByParentId(Long spaceId, Long parentId) {
        String sql = """
                SELECT COUNT(*)
                FROM content_directory
                WHERE space_id = :spaceId
                  AND parent_id = :parentId
                  AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("spaceId", spaceId);
        params.put("parentId", parentId);

        try {
            return jdbcManager.queryLong(sql, params);
        } catch (Exception e) {
            log.error("统计子目录数量失败，spaceId: {}, parentId: {}", spaceId, parentId, e);
            return 0L;
        }
    }

    /**
     * 获取父目录下的最大排序值
     *
     * @param spaceId  空间ID
     * @param parentId 父目录ID
     * @return 最大排序值
     */
    public Integer getMaxSort(Long spaceId, Long parentId) {
        String sql = """
                SELECT COALESCE(MAX(sort), 0)
                FROM content_directory
                WHERE space_id = :spaceId
                  AND parent_id = :parentId
                  AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("spaceId", spaceId);
        params.put("parentId", parentId);

        try {
            return jdbcManager.queryInt(sql, params);
        } catch (Exception e) {
            log.error("获取最大排序值失败，spaceId: {}, parentId: {}", spaceId, parentId, e);
            return 0;
        }
    }
}

