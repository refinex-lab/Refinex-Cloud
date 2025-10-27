package cn.refinex.kb.repository;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.kb.entity.ContentDocument;
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
 * 内容文档数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ContentDocumentRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 插入内容文档
     *
     * @param document 文档实体
     * @return 文档ID
     */
    public Long insert(ContentDocument document) {
        String sql = """
                INSERT INTO content_document (
                    doc_guid, space_id, directory_id, doc_title, doc_summary,
                    content_type, content_body, cover_image, file_id, access_type,
                    is_paid, paid_amount, doc_status, publish_time, schedule_publish_time,
                    word_count, read_duration, view_count, like_count, collect_count,
                    comment_count, share_count, seo_keywords, seo_description, version_number,
                    create_by, create_time, update_by, update_time,
                    deleted, version, remark, sort, status, extra_data
                ) VALUES (
                    :docGuid, :spaceId, :directoryId, :docTitle, :docSummary,
                    :contentType, :contentBody, :coverImage, :fileId, :accessType,
                    :isPaid, :paidAmount, :docStatus, :publishTime, :schedulePublishTime,
                    :wordCount, :readDuration, :viewCount, :likeCount, :collectCount,
                    :commentCount, :shareCount, :seoKeywords, :seoDescription, :versionNumber,
                    :createBy, :createTime, :updateBy, :updateTime,
                    :deleted, :version, :remark, :sort, :status, :extraData
                )
                """;

        BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(document);
        return jdbcManager.insertAndGetKey(sql, paramSource);
    }

    /**
     * 根据ID更新文档
     *
     * @param document 文档实体
     * @return 影响行数
     */
    public int update(ContentDocument document) {
        String sql = """
                UPDATE content_document
                SET directory_id       = :directoryId,
                    doc_title          = :docTitle,
                    doc_summary        = :docSummary,
                    content_type       = :contentType,
                    content_body       = :contentBody,
                    cover_image        = :coverImage,
                    file_id            = :fileId,
                    access_type        = :accessType,
                    is_paid            = :isPaid,
                    paid_amount        = :paidAmount,
                    word_count         = :wordCount,
                    read_duration      = :readDuration,
                    seo_keywords       = :seoKeywords,
                    seo_description    = :seoDescription,
                    update_by          = :updateBy,
                    update_time        = :updateTime,
                    remark             = :remark,
                    sort               = :sort,
                    status             = :status,
                    extra_data         = :extraData,
                    version            = version + 1
                WHERE id = :id
                  AND version = :version
                  AND deleted = 0
                """;

        Map<String, Object> params = BeanConverter.beanToMap(document, false, false);
        return jdbcManager.update(sql, params);
    }

    /**
     * 更新文档内容和版本号（保存文档内容）
     *
     * @param documentId    文档ID
     * @param contentBody   文档内容
     * @param wordCount     字数统计
     * @param readDuration  预计阅读时长
     * @param versionNumber 新版本号
     * @param updateBy      更新人ID
     * @param version       乐观锁版本号
     * @return 影响行数
     */
    public int updateContentAndVersion(Long documentId, String contentBody, Integer wordCount,
                                       Integer readDuration, Integer versionNumber,
                                       Long updateBy, Integer version) {
        String sql = """
                UPDATE content_document
                SET content_body      = :contentBody,
                    word_count        = :wordCount,
                    read_duration     = :readDuration,
                    version_number    = :versionNumber,
                    update_by         = :updateBy,
                    update_time       = :updateTime,
                    version           = version + 1
                WHERE id = :documentId
                  AND version = :version
                  AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("documentId", documentId);
        params.put("contentBody", contentBody);
        params.put("wordCount", wordCount);
        params.put("readDuration", readDuration);
        params.put("versionNumber", versionNumber);
        params.put("updateBy", updateBy);
        params.put("updateTime", LocalDateTime.now());
        params.put("version", version);

        return jdbcManager.update(sql, params);
    }

    /**
     * 发布/下架文档
     *
     * @param documentId  文档ID
     * @param docStatus   文档状态
     * @param publishTime 发布时间
     * @param updateBy    更新人ID
     * @param version     乐观锁版本号
     * @return 影响行数
     */
    public int updatePublishStatus(Long documentId, Integer docStatus, LocalDateTime publishTime,
                                   Long updateBy, Integer version) {
        String sql = """
                UPDATE content_document
                SET doc_status        = :docStatus,
                    publish_time      = :publishTime,
                    update_by         = :updateBy,
                    update_time       = :updateTime,
                    version           = version + 1
                WHERE id = :documentId
                  AND version = :version
                  AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("documentId", documentId);
        params.put("docStatus", docStatus);
        params.put("publishTime", publishTime);
        params.put("updateBy", updateBy);
        params.put("updateTime", LocalDateTime.now());
        params.put("version", version);

        return jdbcManager.update(sql, params);
    }

    /**
     * 移动文档到指定目录
     *
     * @param documentId  文档ID
     * @param directoryId 目标目录ID
     * @param updateBy    更新人ID
     * @return 影响行数
     */
    public int moveToDirectory(Long documentId, Long directoryId, Long updateBy) {
        String sql = """
                UPDATE content_document
                SET directory_id      = :directoryId,
                    update_by         = :updateBy,
                    update_time       = :updateTime
                WHERE id = :documentId
                  AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("documentId", documentId);
        params.put("directoryId", directoryId);
        params.put("updateBy", updateBy);
        params.put("updateTime", LocalDateTime.now());

        return jdbcManager.update(sql, params);
    }

    /**
     * 逻辑删除文档
     *
     * @param documentId 文档ID
     * @param updateBy   更新人ID
     * @return 影响行数
     */
    public int deleteById(Long documentId, Long updateBy) {
        String sql = """
                UPDATE content_document
                SET deleted           = 1,
                    update_by         = :updateBy,
                    update_time       = :updateTime
                WHERE id = :documentId
                  AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("documentId", documentId);
        params.put("updateBy", updateBy);
        params.put("updateTime", LocalDateTime.now());

        return jdbcManager.update(sql, params);
    }

    /**
     * 根据ID查询文档
     *
     * @param documentId 文档ID
     * @return 文档实体
     */
    public ContentDocument selectById(Long documentId) {
        String sql = """
                SELECT *
                FROM content_document
                WHERE id = :documentId
                  AND deleted = 0
                """;

        Map<String, Object> params = Map.of("documentId", documentId);

        try {
            return jdbcManager.queryObject(sql, params, true, ContentDocument.class);
        } catch (Exception e) {
            log.error("根据ID查询文档失败，documentId: {}", documentId, e);
            return null;
        }
    }

    /**
     * 根据文档GUID查询文档
     *
     * @param docGuid 文档GUID
     * @return 文档实体
     */
    public ContentDocument selectByDocGuid(String docGuid) {
        String sql = """
                SELECT *
                FROM content_document
                WHERE doc_guid = :docGuid
                  AND deleted = 0
                """;

        Map<String, Object> params = Map.of("docGuid", docGuid);

        try {
            return jdbcManager.queryObject(sql, params, true, ContentDocument.class);
        } catch (Exception e) {
            log.error("根据文档GUID查询文档失败，docGuid: {}", docGuid, e);
            return null;
        }
    }

    /**
     * 根据空间ID查询文档列表
     *
     * @param spaceId 空间ID
     * @return 文档列表
     */
    public List<ContentDocument> selectBySpaceId(Long spaceId) {
        String sql = """
                SELECT *
                FROM content_document
                WHERE space_id = :spaceId
                  AND deleted = 0
                ORDER BY sort ASC, create_time DESC
                """;

        Map<String, Object> params = Map.of("spaceId", spaceId);

        try {
            return jdbcManager.queryList(sql, params, true, ContentDocument.class);
        } catch (Exception e) {
            log.error("根据空间ID查询文档列表失败，spaceId: {}", spaceId, e);
            return List.of();
        }
    }

    /**
     * 根据目录ID查询文档列表
     *
     * @param directoryId 目录ID
     * @return 文档列表
     */
    public List<ContentDocument> selectByDirectoryId(Long directoryId) {
        String sql = """
                SELECT *
                FROM content_document
                WHERE directory_id = :directoryId
                  AND deleted = 0
                ORDER BY sort ASC, create_time DESC
                """;

        Map<String, Object> params = Map.of("directoryId", directoryId);

        try {
            return jdbcManager.queryList(sql, params, true, ContentDocument.class);
        } catch (Exception e) {
            log.error("根据目录ID查询文档列表失败，directoryId: {}", directoryId, e);
            return List.of();
        }
    }

    /**
     * 根据空间ID和目录ID查询文档列表（支持null目录ID，查询根目录文档）
     *
     * @param spaceId     空间ID
     * @param directoryId 目录ID，null表示查询根目录文档
     * @return 文档列表
     */
    public List<ContentDocument> selectBySpaceIdAndDirectoryId(Long spaceId, Long directoryId) {
        StringBuilder sql = new StringBuilder("""
                SELECT *
                FROM content_document
                WHERE space_id = :spaceId
                  AND deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();
        params.put("spaceId", spaceId);

        if (directoryId == null) {
            sql.append(" AND directory_id IS NULL");
        } else {
            sql.append(" AND directory_id = :directoryId");
            params.put("directoryId", directoryId);
        }

        sql.append(" ORDER BY sort ASC, create_time DESC");

        try {
            return jdbcManager.queryList(sql.toString(), params, true, ContentDocument.class);
        } catch (Exception e) {
            log.error("根据空间ID和目录ID查询文档列表失败，spaceId: {}, directoryId: {}", spaceId, directoryId, e);
            return List.of();
        }
    }

    /**
     * 分页查询文档列表
     *
     * @param queryParams 查询条件
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    public PageResult<ContentDocument> selectPage(Map<String, Object> queryParams, PageRequest pageRequest) {
        StringBuilder sqlBuilder = new StringBuilder("""
                SELECT *
                FROM content_document
                WHERE deleted = 0
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        // 动态条件
        if (queryParams.containsKey("spaceId")) {
            sqlBuilder.append(" AND space_id = :spaceId");
            params.addValue("spaceId", queryParams.get("spaceId"));
        }
        if (queryParams.containsKey("directoryId")) {
            sqlBuilder.append(" AND directory_id = :directoryId");
            params.addValue("directoryId", queryParams.get("directoryId"));
        }
        if (queryParams.containsKey("docTitle")) {
            sqlBuilder.append(" AND doc_title LIKE :docTitle");
            params.addValue("docTitle", "%" + queryParams.get("docTitle") + "%");
        }
        if (queryParams.containsKey("contentType")) {
            sqlBuilder.append(" AND content_type = :contentType");
            params.addValue("contentType", queryParams.get("contentType"));
        }
        if (queryParams.containsKey("docStatus")) {
            sqlBuilder.append(" AND doc_status = :docStatus");
            params.addValue("docStatus", queryParams.get("docStatus"));
        }
        if (queryParams.containsKey("isPaid")) {
            sqlBuilder.append(" AND is_paid = :isPaid");
            params.addValue("isPaid", queryParams.get("isPaid"));
        }
        if (queryParams.containsKey("status")) {
            sqlBuilder.append(" AND status = :status");
            params.addValue("status", queryParams.get("status"));
        }
        if (queryParams.containsKey("createBy")) {
            sqlBuilder.append(" AND create_by = :createBy");
            params.addValue("createBy", queryParams.get("createBy"));
        }

        sqlBuilder.append(" ORDER BY sort ASC, create_time DESC");

        try {
            return jdbcManager.queryPage(sqlBuilder.toString(), params.getValues(), pageRequest, ContentDocument.class);
        } catch (Exception e) {
            log.error("分页查询文档列表失败", e);
            return PageResult.empty(pageRequest.getPageNum(), pageRequest.getPageSize());
        }
    }

    /**
     * 检查文档标题是否存在（同一空间下）
     *
     * @param spaceId   空间ID
     * @param docTitle  文档标题
     * @param excludeId 排除的文档ID（更新时使用）
     * @return 是否存在
     */
    public boolean existsByTitleInSpace(Long spaceId, String docTitle, Long excludeId) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*)
                FROM content_document
                WHERE space_id = :spaceId
                  AND doc_title = :docTitle
                  AND deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();
        params.put("spaceId", spaceId);
        params.put("docTitle", docTitle);

        if (excludeId != null) {
            sql.append(" AND id != :excludeId");
            params.put("excludeId", excludeId);
        }

        try {
            return jdbcManager.queryInt(sql.toString(), params) > 0;
        } catch (Exception e) {
            log.error("检查文档标题是否存在失败", e);
            return false;
        }
    }

    /**
     * 增加浏览次数
     *
     * @param documentId 文档ID
     */
    public void incrementViewCount(Long documentId) {
        String sql = """
                UPDATE content_document
                SET view_count = view_count + 1
                WHERE id = :documentId
                  AND deleted = 0
                """;

        Map<String, Object> params = Map.of("documentId", documentId);
        jdbcManager.update(sql, params);
    }

    /**
     * 增加点赞数
     *
     * @param documentId 文档ID
     */
    public void incrementLikeCount(Long documentId) {
        String sql = """
                UPDATE content_document
                SET like_count = like_count + 1
                WHERE id = :documentId
                  AND deleted = 0
                """;

        Map<String, Object> params = Map.of("documentId", documentId);
        jdbcManager.update(sql, params);
    }

    /**
     * 减少点赞数
     *
     * @param documentId 文档ID
     */
    public void decrementLikeCount(Long documentId) {
        String sql = """
                UPDATE content_document
                SET like_count = GREATEST(0, like_count - 1)
                WHERE id = :documentId
                  AND deleted = 0
                """;

        Map<String, Object> params = Map.of("documentId", documentId);
        jdbcManager.update(sql, params);
    }

    /**
     * 增加收藏数
     *
     * @param documentId 文档ID
     */
    public void incrementCollectCount(Long documentId) {
        String sql = """
                UPDATE content_document
                SET collect_count = collect_count + 1
                WHERE id = :documentId
                  AND deleted = 0
                """;

        Map<String, Object> params = Map.of("documentId", documentId);
        jdbcManager.update(sql, params);
    }

    /**
     * 减少收藏数
     *
     * @param documentId 文档ID
     */
    public void decrementCollectCount(Long documentId) {
        String sql = """
                UPDATE content_document
                SET collect_count = GREATEST(0, collect_count - 1)
                WHERE id = :documentId
                  AND deleted = 0
                """;

        Map<String, Object> params = Map.of("documentId", documentId);
        jdbcManager.update(sql, params);
    }

    /**
     * 统计空间下的文档数量
     *
     * @param spaceId 空间ID
     * @return 文档数量
     */
    public long countBySpaceId(Long spaceId) {
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
     * 统计目录下的文档数量
     *
     * @param directoryId 目录ID
     * @return 文档数量
     */
    public long countByDirectoryId(Long directoryId) {
        String sql = """
                SELECT COUNT(*)
                FROM content_document
                WHERE directory_id = :directoryId
                  AND deleted = 0
                """;

        Map<String, Object> params = Map.of("directoryId", directoryId);

        try {
            return jdbcManager.queryLong(sql, params);
        } catch (Exception e) {
            log.error("统计目录文档数量失败，directoryId: {}", directoryId, e);
            return 0L;
        }
    }

    /**
     * 统计指定状态的文档数量
     *
     * @param spaceId   空间ID
     * @param docStatus 文档状态
     * @return 文档数量
     */
    public long countBySpaceIdAndStatus(Long spaceId, Integer docStatus) {
        String sql = """
                SELECT COUNT(*)
                FROM content_document
                WHERE space_id = :spaceId
                  AND doc_status = :docStatus
                  AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("spaceId", spaceId);
        params.put("docStatus", docStatus);

        try {
            return jdbcManager.queryLong(sql, params);
        } catch (Exception e) {
            log.error("统计指定状态文档数量失败，spaceId: {}, docStatus: {}", spaceId, docStatus, e);
            return 0L;
        }
    }

    /**
     * 批量删除目录下的所有文档（用于级联删除）
     *
     * @param directoryId 目录ID
     * @param updateBy    更新人ID
     * @return 影响行数
     */
    public int deleteByDirectoryId(Long directoryId, Long updateBy) {
        String sql = """
                UPDATE content_document
                SET deleted           = 1,
                    update_by         = :updateBy,
                    update_time       = :updateTime
                WHERE directory_id = :directoryId
                  AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("directoryId", directoryId);
        params.put("updateBy", updateBy);
        params.put("updateTime", LocalDateTime.now());

        return jdbcManager.update(sql, params);
    }

    /**
     * 批量删除空间下的所有文档（用于级联删除）
     *
     * @param spaceId  空间ID
     * @param updateBy 更新人ID
     * @return 影响行数
     */
    public int deleteBySpaceId(Long spaceId, Long updateBy) {
        String sql = """
                UPDATE content_document
                SET deleted           = 1,
                    update_by         = :updateBy,
                    update_time       = :updateTime
                WHERE space_id = :spaceId
                  AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("spaceId", spaceId);
        params.put("updateBy", updateBy);
        params.put("updateTime", LocalDateTime.now());

        return jdbcManager.update(sql, params);
    }
}

