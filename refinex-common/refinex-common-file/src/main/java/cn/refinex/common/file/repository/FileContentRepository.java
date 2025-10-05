package cn.refinex.common.file.repository;

import cn.refinex.common.file.domain.entity.FileContent;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件内容 Repository
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class FileContentRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 插入文件内容
     *
     * @param fileContent 文件内容
     * @return 影响行数
     */
    public int insert(FileContent fileContent) {
        String sql = """
            INSERT INTO file_content (id, file_id, content_data, create_time)
            VALUES (:id, :fileId, :contentData, :createTime)
            """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", fileContent.getId());
        params.put("fileId", fileContent.getFileId());
        params.put("contentData", fileContent.getContentData());
        params.put("createTime", fileContent.getCreateTime());

        return jdbcManager.insert(sql, params);
    }

    /**
     * 根据文件 ID 查询文件内容
     *
     * @param fileId 文件 ID
     * @return 文件内容
     */
    public FileContent findByFileId(Long fileId) {
        String sql = """
            SELECT * FROM file_content
            WHERE file_id = :fileId
            """;

        Map<String, Object> params = Map.of("fileId", fileId);
        return jdbcManager.queryObject(sql, params, FileContent.class);
    }

    /**
     * 根据文件 ID 删除文件内容
     *
     * @param fileId 文件 ID
     * @return 影响行数
     */
    public int deleteByFileId(Long fileId) {
        String sql = """
            DELETE FROM file_content
            WHERE file_id = :fileId
            """;

        Map<String, Object> params = Map.of("fileId", fileId);
        return jdbcManager.delete(sql, params);
    }
}

