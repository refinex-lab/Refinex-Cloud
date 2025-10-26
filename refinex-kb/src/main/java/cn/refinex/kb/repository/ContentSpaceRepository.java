package cn.refinex.kb.repository;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.kb.controller.space.dto.request.ContentSpaceQueryRequestDTO;
import cn.refinex.kb.entity.ContentSpace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内容空间数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ContentSpaceRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 插入内容空间
     *
     * @param space 内容空间实体
     * @return 空间ID
     */
    public Long insert(ContentSpace space) {
        String sql = """
                INSERT INTO content_space (
                    space_code, space_name, space_desc, cover_image, owner_id,
                    space_type, access_type, access_password, is_published, publish_time,
                    view_count, create_by, create_time, update_by, update_time,
                    deleted, version, remark, sort, status, extra_data
                ) VALUES (
                    :spaceCode, :spaceName, :spaceDesc, :coverImage, :ownerId,
                    :spaceType, :accessType, :accessPassword, :isPublished, :publishTime,
                    :viewCount, :createBy, :createTime, :updateBy, :updateTime,
                    :deleted, :version, :remark, :sort, :status, :extraData
                )
                """;

        BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(space);
        return jdbcManager.insertAndGetKey(sql, paramSource);
    }

    /**
     * 根据ID更新内容空间
     *
     * @param space 内容空间实体
     * @return 影响行数
     */
    public int update(ContentSpace space) {
        String sql = """
                UPDATE content_space
                SET space_name      = :spaceName,
                    space_desc      = :spaceDesc,
                    cover_image     = :coverImage,
                    space_type      = :spaceType,
                    access_type     = :accessType,
                    access_password = :accessPassword,
                    update_by       = :updateBy,
                    update_time     = :updateTime,
                    remark          = :remark,
                    sort            = :sort,
                    status          = :status,
                    extra_data      = :extraData,
                    version         = version + 1
                WHERE id = :id
                  AND version = :version
                  AND deleted = 0
                """;

        Map<String, Object> params = BeanConverter.beanToMap(space, false, false);
        return jdbcManager.update(sql, params);
    }

    /**
     * 发布/取消发布空间
     *
     * @param spaceId     空间ID
     * @param isPublished 是否发布
     * @param publishTime 发布时间
     * @param updateBy    更新人ID
     * @param version     乐观锁版本号
     * @return 影响行数
     */
    public int updatePublishStatus(Long spaceId, Integer isPublished, LocalDateTime publishTime, Long updateBy, Integer version) {
        String sql = """
                UPDATE content_space
                SET is_published    = :isPublished,
                    publish_time    = :publishTime,
                    update_by       = :updateBy,
                    update_time     = :updateTime,
                    version         = version + 1
                WHERE id = :spaceId
                  AND version = :version
                  AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("spaceId", spaceId);
        params.put("isPublished", isPublished);
        params.put("publishTime", publishTime);
        params.put("updateBy", updateBy);
        params.put("updateTime", LocalDateTime.now());
        params.put("version", version);

        return jdbcManager.update(sql, params);
    }

    /**
     * 逻辑删除空间
     *
     * @param spaceId  空间ID
     * @param updateBy 更新人ID
     * @return 影响行数
     */
    public int deleteById(Long spaceId, Long updateBy) {
        String sql = """
                UPDATE content_space
                SET deleted        = 1,
                    update_by      = :updateBy,
                    update_time    = :updateTime
                WHERE id = :spaceId
                  AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("spaceId", spaceId);
        params.put("updateBy", updateBy);
        params.put("updateTime", LocalDateTime.now());

        return jdbcManager.update(sql, params);
    }

    /**
     * 根据ID查询空间
     *
     * @param spaceId 空间ID
     * @return 空间实体
     */
    public ContentSpace selectById(Long spaceId) {
        String sql = """
                SELECT *
                FROM content_space
                WHERE id = :spaceId
                  AND deleted = 0
                """;

        Map<String, Object> params = Map.of("spaceId", spaceId);

        try {
            return jdbcManager.queryObject(sql, params, true, ContentSpace.class);
        } catch (Exception e) {
            log.error("根据ID查询空间失败，spaceId: {}", spaceId, e);
            return null;
        }
    }

    /**
     * 根据空间编码查询空间
     *
     * @param spaceCode 空间编码
     * @return 空间实体
     */
    public ContentSpace selectBySpaceCode(String spaceCode) {
        String sql = """
                SELECT *
                FROM content_space
                WHERE space_code = :spaceCode
                  AND deleted = 0
                """;

        Map<String, Object> params = Map.of("spaceCode", spaceCode);

        try {
            return jdbcManager.queryObject(sql, params, true, ContentSpace.class);
        } catch (Exception e) {
            log.error("根据空间编码查询空间失败，spaceCode: {}", spaceCode, e);
            return null;
        }
    }

    /**
     * 根据拥有者ID查询空间列表
     *
     * @param ownerId 拥有者ID
     * @return 空间列表
     */
    public List<ContentSpace> selectByOwnerId(Long ownerId) {
        String sql = """
                SELECT *
                FROM content_space
                WHERE owner_id = :ownerId
                  AND deleted = 0
                ORDER BY sort ASC, create_time DESC
                """;

        Map<String, Object> params = Map.of("ownerId", ownerId);

        try {
            return jdbcManager.queryList(sql, params, true, ContentSpace.class);
        } catch (Exception e) {
            log.error("根据拥有者ID查询空间列表失败，ownerId: {}", ownerId, e);
            return List.of();
        }
    }

    /**
     * 分页查询空间列表
     *
     * @param queryDTO    查询条件
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    public PageResult<ContentSpace> selectPage(ContentSpaceQueryRequestDTO queryDTO, PageRequest pageRequest) {
        StringBuilder sqlBuilder = new StringBuilder("""
                SELECT *
                FROM content_space
                WHERE deleted = 0
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        // 动态条件
        if (queryDTO.getSpaceCode() != null && !queryDTO.getSpaceCode().isBlank()) {
            sqlBuilder.append(" AND space_code LIKE :spaceCode");
            params.addValue("spaceCode", "%" + queryDTO.getSpaceCode() + "%");
        }
        if (queryDTO.getSpaceName() != null && !queryDTO.getSpaceName().isBlank()) {
            sqlBuilder.append(" AND space_name LIKE :spaceName");
            params.addValue("spaceName", "%" + queryDTO.getSpaceName() + "%");
        }
        if (queryDTO.getOwnerId() != null) {
            sqlBuilder.append(" AND owner_id = :ownerId");
            params.addValue("ownerId", queryDTO.getOwnerId());
        }
        if (queryDTO.getSpaceType() != null) {
            sqlBuilder.append(" AND space_type = :spaceType");
            params.addValue("spaceType", queryDTO.getSpaceType());
        }
        if (queryDTO.getAccessType() != null) {
            sqlBuilder.append(" AND access_type = :accessType");
            params.addValue("accessType", queryDTO.getAccessType());
        }
        if (queryDTO.getIsPublished() != null) {
            sqlBuilder.append(" AND is_published = :isPublished");
            params.addValue("isPublished", queryDTO.getIsPublished());
        }
        if (queryDTO.getStatus() != null) {
            sqlBuilder.append(" AND status = :status");
            params.addValue("status", queryDTO.getStatus());
        }

        sqlBuilder.append(" ORDER BY sort ASC, create_time DESC");

        try {
            return jdbcManager.queryPage(sqlBuilder.toString(), params.getValues(), pageRequest, ContentSpace.class);
        } catch (Exception e) {
            log.error("分页查询空间列表失败", e);
            return PageResult.empty(pageRequest.getPageNum(), pageRequest.getPageSize());
        }
    }

    /**
     * 检查空间编码是否存在
     *
     * @param spaceCode 空间编码
     * @param excludeId 排除的ID（更新时使用）
     * @return 是否存在
     */
    public boolean existsBySpaceCode(String spaceCode, Long excludeId) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*)
                FROM content_space
                WHERE space_code = :spaceCode
                  AND deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();
        params.put("spaceCode", spaceCode);

        if (excludeId != null) {
            sql.append(" AND id != :excludeId");
            params.put("excludeId", excludeId);
        }

        try {
            return jdbcManager.queryInt(sql.toString(), params) > 0;
        } catch (Exception e) {
            log.error("检查空间编码是否存在失败", e);
            return false;
        }
    }

    /**
     * 增加浏览次数
     *
     * @param spaceId 空间ID
     */
    public void incrementViewCount(Long spaceId) {
        String sql = """
                UPDATE content_space
                SET view_count = view_count + 1
                WHERE id = :spaceId
                  AND deleted = 0
                """;

        Map<String, Object> params = Map.of("spaceId", spaceId);
        jdbcManager.update(sql, params);
    }

    /**
     * 统计空间下的文档数量
     *
     * @param spaceId 空间ID
     * @return 文档数量
     */
    public long countDocumentsBySpaceId(Long spaceId) {
        String sql = """
                SELECT COUNT(*)
                FROM content_document
                WHERE space_id = :spaceId
                  AND deleted = 0
                """;

        Map<String, Object> params = Map.of("spaceId", spaceId);

        try {
            return jdbcManager.queryLong(sql, params);
        } catch (Exception e) {
            log.error("统计空间文档数量失败，spaceId: {}", spaceId, e);
            return 0L;
        }
    }

    /**
     * 统计空间下的目录数量
     *
     * @param spaceId 空间ID
     * @return 目录数量
     */
    public long countDirectoriesBySpaceId(Long spaceId) {
        String sql = """
                SELECT COUNT(*)
                FROM content_directory
                WHERE space_id = :spaceId
                  AND deleted = 0
                """;

        Map<String, Object> params = Map.of("spaceId", spaceId);

        try {
            return jdbcManager.queryLong(sql, params);
        } catch (Exception e) {
            log.error("统计空间目录数量失败，spaceId: {}", spaceId, e);
            return 0L;
        }
    }
}

