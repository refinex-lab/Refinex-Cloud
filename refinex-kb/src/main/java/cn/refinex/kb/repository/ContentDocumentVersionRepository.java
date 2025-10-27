package cn.refinex.kb.repository;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.kb.entity.ContentDocumentVersion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文档版本数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ContentDocumentVersionRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 插入文档版本
     *
     * @param version 版本实体
     * @return 版本ID
     */
    public Long insert(ContentDocumentVersion version) {
        String sql = """
                INSERT INTO content_document_version (
                    document_id, version_number, content_body, file_id,
                    change_summary, word_count, created_by, create_time
                ) VALUES (
                    :documentId, :versionNumber, :contentBody, :fileId,
                    :changeSummary, :wordCount, :createdBy, :createTime
                )
                """;

        BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(version);
        return jdbcManager.insertAndGetKey(sql, paramSource);
    }

    /**
     * 根据ID查询版本
     *
     * @param versionId 版本ID
     * @return 版本实体
     */
    public ContentDocumentVersion selectById(Long versionId) {
        String sql = """
                SELECT *
                FROM content_document_version
                WHERE id = :versionId
                """;

        Map<String, Object> params = Map.of("versionId", versionId);

        try {
            return jdbcManager.queryObject(sql, params, true, ContentDocumentVersion.class);
        } catch (Exception e) {
            log.error("根据ID查询版本失败，versionId: {}", versionId, e);
            return null;
        }
    }

    /**
     * 根据文档ID和版本号查询版本
     *
     * @param documentId    文档ID
     * @param versionNumber 版本号
     * @return 版本实体
     */
    public ContentDocumentVersion selectByDocumentIdAndVersionNumber(Long documentId, Integer versionNumber) {
        String sql = """
                SELECT *
                FROM content_document_version
                WHERE document_id = :documentId
                  AND version_number = :versionNumber
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("documentId", documentId);
        params.put("versionNumber", versionNumber);

        try {
            return jdbcManager.queryObject(sql, params, true, ContentDocumentVersion.class);
        } catch (Exception e) {
            log.error("根据文档ID和版本号查询版本失败，documentId: {}, versionNumber: {}", documentId, versionNumber, e);
            return null;
        }
    }

    /**
     * 查询文档的所有版本列表（按版本号倒序）
     *
     * @param documentId 文档ID
     * @return 版本列表
     */
    public List<ContentDocumentVersion> selectByDocumentId(Long documentId) {
        String sql = """
                SELECT *
                FROM content_document_version
                WHERE document_id = :documentId
                ORDER BY version_number DESC
                """;

        Map<String, Object> params = Map.of("documentId", documentId);

        try {
            return jdbcManager.queryList(sql, params, true, ContentDocumentVersion.class);
        } catch (Exception e) {
            log.error("查询文档版本列表失败，documentId: {}", documentId, e);
            return List.of();
        }
    }

    /**
     * 分页查询文档版本列表
     *
     * @param documentId  文档ID
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    public PageResult<ContentDocumentVersion> selectPageByDocumentId(Long documentId, PageRequest pageRequest) {
        String sql = """
                SELECT *
                FROM content_document_version
                WHERE document_id = :documentId
                ORDER BY version_number DESC
                """;

        Map<String, Object> params = Map.of("documentId", documentId);

        try {
            return jdbcManager.queryPage(sql, params, pageRequest, ContentDocumentVersion.class);
        } catch (Exception e) {
            log.error("分页查询文档版本列表失败，documentId: {}", documentId, e);
            return PageResult.empty(pageRequest.getPageNum(), pageRequest.getPageSize());
        }
    }

    /**
     * 获取文档的最新版本
     *
     * @param documentId 文档ID
     * @return 最新版本实体
     */
    public ContentDocumentVersion selectLatestByDocumentId(Long documentId) {
        String sql = """
                SELECT *
                FROM content_document_version
                WHERE document_id = :documentId
                ORDER BY version_number DESC
                LIMIT 1
                """;

        Map<String, Object> params = Map.of("documentId", documentId);

        try {
            return jdbcManager.queryObject(sql, params, true, ContentDocumentVersion.class);
        } catch (Exception e) {
            log.error("获取文档最新版本失败，documentId: {}", documentId, e);
            return null;
        }
    }

    /**
     * 获取文档的最大版本号
     *
     * @param documentId 文档ID
     * @return 最大版本号，如果没有版本则返回0
     */
    public Integer selectMaxVersionNumber(Long documentId) {
        String sql = """
                SELECT COALESCE(MAX(version_number), 0)
                FROM content_document_version
                WHERE document_id = :documentId
                """;

        Map<String, Object> params = Map.of("documentId", documentId);

        try {
            return jdbcManager.queryInt(sql, params);
        } catch (Exception e) {
            log.error("获取文档最大版本号失败，documentId: {}", documentId, e);
            return 0;
        }
    }

    /**
     * 统计文档的版本数量
     *
     * @param documentId 文档ID
     * @return 版本数量
     */
    public long countByDocumentId(Long documentId) {
        String sql = """
                SELECT COUNT(*)
                FROM content_document_version
                WHERE document_id = :documentId
                """;

        Map<String, Object> params = Map.of("documentId", documentId);

        try {
            return jdbcManager.queryLong(sql, params);
        } catch (Exception e) {
            log.error("统计文档版本数量失败，documentId: {}", documentId, e);
            return 0L;
        }
    }

    /**
     * 检查版本是否存在
     *
     * @param documentId    文档ID
     * @param versionNumber 版本号
     * @return 是否存在
     */
    public boolean existsByDocumentIdAndVersionNumber(Long documentId, Integer versionNumber) {
        String sql = """
                SELECT COUNT(*)
                FROM content_document_version
                WHERE document_id = :documentId
                  AND version_number = :versionNumber
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("documentId", documentId);
        params.put("versionNumber", versionNumber);

        try {
            return jdbcManager.queryInt(sql, params) > 0;
        } catch (Exception e) {
            log.error("检查版本是否存在失败，documentId: {}, versionNumber: {}", documentId, versionNumber, e);
            return false;
        }
    }

    /**
     * 删除文档的所有版本（用于级联删除）
     * 注意：版本表通常不做逻辑删除，直接物理删除
     *
     * @param documentId 文档ID
     * @return 影响行数
     */
    public int deleteByDocumentId(Long documentId) {
        String sql = """
                DELETE FROM content_document_version
                WHERE document_id = :documentId
                """;

        Map<String, Object> params = Map.of("documentId", documentId);

        try {
            return jdbcManager.update(sql, params);
        } catch (Exception e) {
            log.error("删除文档版本失败，documentId: {}", documentId, e);
            return 0;
        }
    }

    /**
     * 删除指定版本
     *
     * @param versionId 版本ID
     * @return 影响行数
     */
    public int deleteById(Long versionId) {
        String sql = """
                DELETE FROM content_document_version
                WHERE id = :versionId
                """;

        Map<String, Object> params = Map.of("versionId", versionId);

        try {
            return jdbcManager.update(sql, params);
        } catch (Exception e) {
            log.error("删除版本失败，versionId: {}", versionId, e);
            return 0;
        }
    }

    /**
     * 清理旧版本，只保留最近N个版本
     *
     * @param documentId 文档ID
     * @param keepCount  保留的版本数量
     * @return 删除的版本数量
     */
    public int cleanOldVersions(Long documentId, int keepCount) {
        String sql = """
                DELETE FROM content_document_version
                WHERE document_id = :documentId
                  AND version_number NOT IN (
                      SELECT version_number
                      FROM (
                          SELECT version_number
                          FROM content_document_version
                          WHERE document_id = :documentId
                          ORDER BY version_number DESC
                          LIMIT :keepCount
                      ) AS kept_versions
                  )
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("documentId", documentId);
        params.put("keepCount", keepCount);

        try {
            return jdbcManager.update(sql, params);
        } catch (Exception e) {
            log.error("清理旧版本失败，documentId: {}, keepCount: {}", documentId, keepCount, e);
            return 0;
        }
    }

    /**
     * 获取两个版本之间的所有版本列表（用于版本对比）
     *
     * @param documentId        文档ID
     * @param fromVersionNumber 起始版本号
     * @param toVersionNumber   结束版本号
     * @return 版本列表
     */
    public List<ContentDocumentVersion> selectVersionsInRange(Long documentId, Integer fromVersionNumber, Integer toVersionNumber) {
        String sql = """
                SELECT *
                FROM content_document_version
                WHERE document_id = :documentId
                  AND version_number >= :fromVersionNumber
                  AND version_number <= :toVersionNumber
                ORDER BY version_number ASC
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("documentId", documentId);
        params.put("fromVersionNumber", Math.min(fromVersionNumber, toVersionNumber));
        params.put("toVersionNumber", Math.max(fromVersionNumber, toVersionNumber));

        try {
            return jdbcManager.queryList(sql, params, true, ContentDocumentVersion.class);
        } catch (Exception e) {
            log.error("查询版本范围失败，documentId: {}, from: {}, to: {}", documentId, fromVersionNumber, toVersionNumber, e);
            return List.of();
        }
    }
}

