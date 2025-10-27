package cn.refinex.kb.repository;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.kb.entity.ContentDocumentTag;
import cn.refinex.kb.entity.ContentTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文档标签关联数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ContentDocumentTagRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 插入文档标签关联
     *
     * @param documentTag 文档标签关联实体
     * @return 关联ID
     */
    public Long insert(ContentDocumentTag documentTag) {
        String sql = """
                INSERT INTO content_document_tag (
                    document_id, tag_id, create_time
                ) VALUES (
                    :documentId, :tagId, :createTime
                )
                """;

        BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(documentTag);
        return jdbcManager.insertAndGetKey(sql, paramSource);
    }

    /**
     * 批量插入文档标签关联
     *
     * @param documentId 文档ID
     * @param tagIds     标签ID列表
     * @return 插入的记录数
     */
    public int batchInsert(Long documentId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return 0;
        }

        String sql = """
                INSERT INTO content_document_tag (
                    document_id, tag_id, create_time
                ) VALUES (
                    :documentId, :tagId, :createTime
                )
                """;

        LocalDateTime now = LocalDateTime.now();
        @SuppressWarnings("unchecked")
        Map<String, Object>[] batchParams = tagIds.stream()
                .map(tagId -> {
                    Map<String, Object> params = new HashMap<>();
                    params.put("documentId", documentId);
                    params.put("tagId", tagId);
                    params.put("createTime", now);
                    return params;
                })
                .toArray(size -> (Map<String, Object>[]) new Map[size]);

        try {
            int[] results = jdbcManager.batchUpdate(sql, batchParams);
            return results.length;
        } catch (Exception e) {
            log.error("批量插入文档标签关联失败，documentId: {}, tagIds: {}", documentId, tagIds, e);
            return 0;
        }
    }

    /**
     * 删除文档标签关联
     *
     * @param documentId 文档ID
     * @param tagId      标签ID
     * @return 影响行数
     */
    public int delete(Long documentId, Long tagId) {
        String sql = """
                DELETE FROM content_document_tag
                WHERE document_id = :documentId
                  AND tag_id = :tagId
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("documentId", documentId);
        params.put("tagId", tagId);

        try {
            return jdbcManager.update(sql, params);
        } catch (Exception e) {
            log.error("删除文档标签关联失败，documentId: {}, tagId: {}", documentId, tagId, e);
            return 0;
        }
    }

    /**
     * 删除文档的所有标签关联
     *
     * @param documentId 文档ID
     * @return 影响行数
     */
    public int deleteByDocumentId(Long documentId) {
        String sql = """
                DELETE FROM content_document_tag
                WHERE document_id = :documentId
                """;

        Map<String, Object> params = Map.of("documentId", documentId);

        try {
            return jdbcManager.update(sql, params);
        } catch (Exception e) {
            log.error("删除文档所有标签关联失败，documentId: {}", documentId, e);
            return 0;
        }
    }

    /**
     * 删除标签的所有文档关联
     *
     * @param tagId 标签ID
     * @return 影响行数
     */
    public int deleteByTagId(Long tagId) {
        String sql = """
                DELETE FROM content_document_tag
                WHERE tag_id = :tagId
                """;

        Map<String, Object> params = Map.of("tagId", tagId);

        try {
            return jdbcManager.update(sql, params);
        } catch (Exception e) {
            log.error("删除标签所有文档关联失败，tagId: {}", tagId, e);
            return 0;
        }
    }

    /**
     * 查询文档的所有标签关联
     *
     * @param documentId 文档ID
     * @return 标签关联列表
     */
    public List<ContentDocumentTag> selectByDocumentId(Long documentId) {
        String sql = """
                SELECT *
                FROM content_document_tag
                WHERE document_id = :documentId
                ORDER BY create_time DESC
                """;

        Map<String, Object> params = Map.of("documentId", documentId);

        try {
            return jdbcManager.queryList(sql, params, true, ContentDocumentTag.class);
        } catch (Exception e) {
            log.error("查询文档标签关联失败，documentId: {}", documentId, e);
            return List.of();
        }
    }

    /**
     * 查询标签的所有文档关联
     *
     * @param tagId 标签ID
     * @return 文档关联列表
     */
    public List<ContentDocumentTag> selectByTagId(Long tagId) {
        String sql = """
                SELECT *
                FROM content_document_tag
                WHERE tag_id = :tagId
                ORDER BY create_time DESC
                """;

        Map<String, Object> params = Map.of("tagId", tagId);

        try {
            return jdbcManager.queryList(sql, params, true, ContentDocumentTag.class);
        } catch (Exception e) {
            log.error("查询标签文档关联失败，tagId: {}", tagId, e);
            return List.of();
        }
    }

    /**
     * 查询文档的标签ID列表
     *
     * @param documentId 文档ID
     * @return 标签ID列表
     */
    public List<Long> selectTagIdsByDocumentId(Long documentId) {
        String sql = """
                SELECT tag_id
                FROM content_document_tag
                WHERE document_id = :documentId
                ORDER BY create_time DESC
                """;

        Map<String, Object> params = Map.of("documentId", documentId);

        try {
            return jdbcManager.queryList(sql, params, Long.class);
        } catch (Exception e) {
            log.error("查询文档标签ID列表失败，documentId: {}", documentId, e);
            return List.of();
        }
    }

    /**
     * 查询文档的标签详情列表（关联查询标签表）
     *
     * @param documentId 文档ID
     * @return 标签详情列表
     */
    public List<ContentTag> selectTagsByDocumentId(Long documentId) {
        String sql = """
                SELECT t.*
                FROM content_tag t
                INNER JOIN content_document_tag dt ON t.id = dt.tag_id
                WHERE dt.document_id = :documentId
                  AND t.deleted = 0
                ORDER BY dt.create_time DESC
                """;

        Map<String, Object> params = Map.of("documentId", documentId);

        try {
            return jdbcManager.queryList(sql, params, true, ContentTag.class);
        } catch (Exception e) {
            log.error("查询文档标签详情列表失败，documentId: {}", documentId, e);
            return List.of();
        }
    }

    /**
     * 查询标签下的文档ID列表
     *
     * @param tagId 标签ID
     * @return 文档ID列表
     */
    public List<Long> selectDocumentIdsByTagId(Long tagId) {
        String sql = """
                SELECT document_id
                FROM content_document_tag
                WHERE tag_id = :tagId
                ORDER BY create_time DESC
                """;

        Map<String, Object> params = Map.of("tagId", tagId);

        try {
            return jdbcManager.queryList(sql, params, Long.class);
        } catch (Exception e) {
            log.error("查询标签文档ID列表失败，tagId: {}", tagId, e);
            return List.of();
        }
    }

    /**
     * 检查文档标签关联是否存在
     *
     * @param documentId 文档ID
     * @param tagId      标签ID
     * @return 是否存在
     */
    public boolean exists(Long documentId, Long tagId) {
        String sql = """
                SELECT COUNT(*)
                FROM content_document_tag
                WHERE document_id = :documentId
                  AND tag_id = :tagId
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("documentId", documentId);
        params.put("tagId", tagId);

        try {
            return jdbcManager.queryInt(sql, params) > 0;
        } catch (Exception e) {
            log.error("检查文档标签关联是否存在失败，documentId: {}, tagId: {}", documentId, tagId, e);
            return false;
        }
    }

    /**
     * 统计文档的标签数量
     *
     * @param documentId 文档ID
     * @return 标签数量
     */
    public long countByDocumentId(Long documentId) {
        String sql = """
                SELECT COUNT(*)
                FROM content_document_tag
                WHERE document_id = :documentId
                """;

        Map<String, Object> params = Map.of("documentId", documentId);

        try {
            return jdbcManager.queryLong(sql, params);
        } catch (Exception e) {
            log.error("统计文档标签数量失败，documentId: {}", documentId, e);
            return 0L;
        }
    }

    /**
     * 统计标签下的文档数量
     *
     * @param tagId 标签ID
     * @return 文档数量
     */
    public long countByTagId(Long tagId) {
        String sql = """
                SELECT COUNT(*)
                FROM content_document_tag
                WHERE tag_id = :tagId
                """;

        Map<String, Object> params = Map.of("tagId", tagId);

        try {
            return jdbcManager.queryLong(sql, params);
        } catch (Exception e) {
            log.error("统计标签文档数量失败，tagId: {}", tagId, e);
            return 0L;
        }
    }

    /**
     * 批量查询文档的标签（优化性能）
     *
     * @param documentIds 文档ID列表
     * @return Map<文档ID, 标签列表>
     */
    public Map<Long, List<ContentTag>> selectTagsMapByDocumentIds(List<Long> documentIds) {
        if (documentIds == null || documentIds.isEmpty()) {
            return Map.of();
        }

        String sql = """
                SELECT dt.document_id, t.*
                FROM content_tag t
                INNER JOIN content_document_tag dt ON t.id = dt.tag_id
                WHERE dt.document_id IN (:documentIds)
                  AND t.deleted = 0
                ORDER BY dt.document_id, dt.create_time DESC
                """;

        Map<String, Object> params = Map.of("documentIds", documentIds);

        try {
            List<Map<String, Object>> resultList = jdbcManager.queryList(sql, params, false);

            Map<Long, List<ContentTag>> resultMap = new HashMap<>();
            for (Map<String, Object> row : resultList) {
                Long documentId = (Long) row.get("document_id");

                ContentTag tag = ContentTag.builder()
                        .id((Long) row.get("id"))
                        .tagName((String) row.get("tag_name"))
                        .tagColor((String) row.get("tag_color"))
                        .tagType((Integer) row.get("tag_type"))
                        .usageCount((Long) row.get("usage_count"))
                        .creatorId((Long) row.get("creator_id"))
                        .build();

                resultMap.computeIfAbsent(documentId, k -> new java.util.ArrayList<>()).add(tag);
            }

            return resultMap;
        } catch (Exception e) {
            log.error("批量查询文档标签失败，documentIds: {}", documentIds, e);
            return Map.of();
        }
    }

    /**
     * 替换文档的所有标签（先删除后插入）
     *
     * @param documentId 文档ID
     * @param tagIds     新的标签ID列表
     * @return 是否成功
     */
    public boolean replaceDocumentTags(Long documentId, List<Long> tagIds) {
        try {
            // 先删除旧的关联
            deleteByDocumentId(documentId);

            // 如果有新标签，则插入
            if (tagIds != null && !tagIds.isEmpty()) {
                batchInsert(documentId, tagIds);
            }

            return true;
        } catch (Exception e) {
            log.error("替换文档标签失败，documentId: {}, tagIds: {}", documentId, tagIds, e);
            return false;
        }
    }
}

