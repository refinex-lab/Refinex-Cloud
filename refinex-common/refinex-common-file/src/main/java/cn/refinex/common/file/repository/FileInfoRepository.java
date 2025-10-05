package cn.refinex.common.file.repository;

import cn.refinex.common.file.domain.entity.FileInfo;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.utils.object.BeanConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 文件元数据 Repository
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class FileInfoRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 插入文件元数据
     *
     * @param fileInfo 文件元数据
     * @return 影响行数
     */
    public int insert(FileInfo fileInfo) {
        String sql = """
            INSERT INTO file_info (
                id, file_guid, file_name, original_name, file_extension, file_size, file_type,
                storage_strategy, storage_config_id, storage_path, bucket_name, access_url,
                file_md5, thumbnail_file_id, uploader_id, biz_type, biz_id, ref_count,
                is_public, access_expires, create_by, create_time, update_by, update_time,
                deleted, version, remark
            ) VALUES (
                :id, :fileGuid, :fileName, :originalName, :fileExtension, :fileSize, :fileType,
                :storageStrategy, :storageConfigId, :storagePath, :bucketName, :accessUrl,
                :fileMd5, :thumbnailFileId, :uploaderId, :bizType, :bizId, :refCount,
                :isPublic, :accessExpires, :createBy, :createTime, :updateBy, :updateTime,
                :deleted, :version, :remark
            )
            """;

        Map<String, Object> params = BeanConverter.beanToMap(fileInfo, false, false);
        return jdbcManager.insert(sql, params);
    }

    /**
     * 更新文件信息
     *
     * @param fileInfo 文件信息
     * @return 影响行数
     */
    public int update(FileInfo fileInfo) {
        String sql = """
            UPDATE file_info
            SET file_name = :fileName, original_name = :originalName, file_extension = :fileExtension,
                file_size = :fileSize, file_type = :fileType, storage_strategy = :storageStrategy,
                storage_config_id = :storageConfigId, storage_path = :storagePath, bucket_name = :bucketName,
                access_url = :accessUrl, file_md5 = :fileMd5, thumbnail_file_id = :thumbnailFileId,
                uploader_id = :uploaderId, biz_type = :bizType, biz_id = :bizId, ref_count = :refCount,
                is_public = :isPublic, access_expires = :accessExpires, update_time = :updateTime,
                version = version + 1, remark = :remark
            WHERE id = :id AND deleted = 0
            """;

        Map<String, Object> params = BeanConverter.beanToMap(fileInfo, false, false);
        return jdbcManager.update(sql, params);
    }

    /**
     * 根据 file_guid 查询文件元数据
     *
     * @param fileGuid 文件 GUID
     * @return 文件元数据
     */
    public FileInfo findByFileGuid(String fileGuid) {
        String sql = """
            SELECT * FROM file_info
            WHERE file_guid = :fileGuid AND deleted = 0
            """;

        Map<String, Object> params = Map.of("fileGuid", fileGuid);
        return jdbcManager.queryObject(sql, params, FileInfo.class);
    }

    /**
     * 根据 MD5 查询文件元数据
     *
     * @param fileMd5 文件 MD5
     * @return 文件元数据列表
     */
    public List<FileInfo> findByFileMd5(String fileMd5) {
        String sql = """
            SELECT * FROM file_info
            WHERE file_md5 = :fileMd5 AND deleted = 0
            ORDER BY create_time DESC
            """;

        Map<String, Object> params = Map.of("fileMd5", fileMd5);
        return jdbcManager.queryList(sql, params, FileInfo.class);
    }

    /**
     * 更新引用计数
     *
     * @param fileId 文件 ID
     * @param delta  增量（正数增加，负数减少）
     * @return 影响行数
     */
    public int updateRefCount(Long fileId, int delta) {
        String sql = """
            UPDATE file_info
            SET ref_count = ref_count + :delta, update_time = NOW()
            WHERE id = :fileId AND deleted = 0
            """;

        Map<String, Object> params = Map.of("fileId", fileId, "delta", delta);
        return jdbcManager.update(sql, params);
    }

    /**
     * 逻辑删除文件
     *
     * @param fileId 文件 ID
     * @return 影响行数
     */
    public int logicalDelete(Long fileId) {
        String sql = """
            UPDATE file_info
            SET deleted = 1, update_time = NOW()
            WHERE id = :fileId AND deleted = 0
            """;

        Map<String, Object> params = Map.of("fileId", fileId);
        return jdbcManager.update(sql, params);
    }
}

