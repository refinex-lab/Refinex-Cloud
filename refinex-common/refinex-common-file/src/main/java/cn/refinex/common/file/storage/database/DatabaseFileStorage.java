package cn.refinex.common.file.storage.database;

import cn.refinex.common.exception.SystemException;
import cn.refinex.common.file.core.FileStorage;
import cn.refinex.common.file.domain.entity.FileContent;
import cn.refinex.common.file.enums.StorageType;
import cn.refinex.common.file.constants.FileErrorMessageConstants;
import cn.refinex.common.file.repository.FileContentRepository;
import cn.refinex.common.utils.algorithm.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 数据库文件存储实现
 * <p>
 * 将文件内容存储到 file_content 表的 LONGBLOB 字段中。
 * 适用于小文件（< 1MB）或审计要求高的场景。
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseFileStorage implements FileStorage {

    private final FileContentRepository fileContentRepository;
    private final SnowflakeIdGenerator idGenerator;

    /**
     * 上传文件到数据库
     *
     * @param inputStream 文件输入流
     * @param fileName    文件名
     * @param contentType 文件 MIME 类型
     * @param metadata    文件元数据（包含 fileId）
     * @return 文件 ID（字符串形式）
     */
    @Override
    public String upload(InputStream inputStream, String fileName, String contentType, Map<String, String> metadata) {
        try {
            // 1. 读取文件内容到字节数组
            byte[] contentData = IOUtils.toByteArray(inputStream);

            // 2. 从元数据中获取 fileId
            Long fileId = Long.parseLong(metadata.get("fileId"));

            // 3. 创建 FileContent 实体
            FileContent fileContent = new FileContent();
            fileContent.setId(idGenerator.nextId());
            fileContent.setFileId(fileId);
            fileContent.setContentData(contentData);
            fileContent.setCreateTime(LocalDateTime.now());

            // 4. 插入到数据库
            int result = fileContentRepository.insert(fileContent);
            if (result <= 0) {
                log.error("数据库插入文件内容失败，fileId={}", fileId);
                throw new SystemException(FileErrorMessageConstants.FILE_UPLOAD_FAILED);
            }

            log.info("文件已上传到数据库，fileId={}, size={} bytes", fileId, contentData.length);
            return String.valueOf(fileId);

        } catch (Exception e) {
            log.error("文件上传到数据库失败，fileName={}", fileName, e);
            throw new SystemException(FileErrorMessageConstants.FILE_UPLOAD_FAILED, e);
        }
    }

    /**
     * 从数据库下载文件
     *
     * @param storageKey 存储路径（文件 ID）
     * @return 文件输入流
     */
    @Override
    public InputStream download(String storageKey) {
        try {
            // 1. 解析 fileId
            Long fileId = Long.parseLong(storageKey);

            // 2. 查询文件内容
            FileContent fileContent = fileContentRepository.findByFileId(fileId);
            if (fileContent == null) {
                log.warn("文件内容不存在，fileId={}", fileId);
                throw new SystemException(FileErrorMessageConstants.FILE_NOT_FOUND);
            }

            // 3. 返回字节数组输入流
            log.info("文件已从数据库下载，fileId={}, size={} bytes", fileId, fileContent.getContentData().length);
            return new ByteArrayInputStream(fileContent.getContentData());

        } catch (NumberFormatException e) {
            log.error("无效的存储路径，storageKey={}", storageKey, e);
            throw new SystemException(FileErrorMessageConstants.FILE_DOWNLOAD_FAILED);
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            log.error("文件从数据库下载失败，storageKey={}", storageKey, e);
            throw new SystemException(FileErrorMessageConstants.FILE_DOWNLOAD_FAILED, e);
        }
    }

    /**
     * 从数据库删除文件
     *
     * @param storageKey 存储路径（文件 ID）
     */
    @Override
    public void delete(String storageKey) {
        try {
            // 1. 解析 fileId
            Long fileId = Long.parseLong(storageKey);

            // 2. 删除文件内容
            int result = fileContentRepository.deleteByFileId(fileId);
            if (result <= 0) {
                log.warn("文件内容不存在或已删除，fileId={}", fileId);
            } else {
                log.info("文件已从数据库删除，fileId={}", fileId);
            }

        } catch (NumberFormatException e) {
            log.error("无效的存储路径，storageKey={}", storageKey, e);
            throw new SystemException(FileErrorMessageConstants.FILE_DELETE_FAILED, e);
        } catch (Exception e) {
            log.error("文件从数据库删除失败，storageKey={}", storageKey, e);
            throw new SystemException(FileErrorMessageConstants.FILE_DELETE_FAILED, e);
        }
    }

    /**
     * 数据库存储不支持预签名 URL
     *
     * @param storageKey 存储路径
     * @param expiration 过期时间
     * @return 不支持，抛出异常
     */
    @Override
    public String generatePresignedUrl(String storageKey, Duration expiration) {
        throw new UnsupportedOperationException("数据库存储不支持预签名 URL");
    }

    /**
     * 数据库存储不支持分片上传
     *
     * @param fileName    文件名
     * @param contentType 文件 MIME 类型
     * @return 不支持，抛出异常
     */
    @Override
    public String initiateMultipartUpload(String fileName, String contentType) {
        throw new UnsupportedOperationException("数据库存储不支持分片上传");
    }

    /**
     * 数据库存储不支持分片上传
     *
     * @param uploadId    上传任务 ID
     * @param partNumber  分片序号
     * @param inputStream 分片输入流
     * @return 不支持，抛出异常
     */
    @Override
    public String uploadPart(String uploadId, int partNumber, InputStream inputStream) {
        throw new UnsupportedOperationException("数据库存储不支持分片上传");
    }

    /**
     * 数据库存储不支持分片上传
     *
     * @param uploadId 上传任务 ID
     * @param parts    分片 ETag 列表
     * @return 不支持，抛出异常
     */
    @Override
    public String completeMultipartUpload(String uploadId, List<PartETag> parts) {
        throw new UnsupportedOperationException("数据库存储不支持分片上传");
    }

    /**
     * 获取存储类型
     *
     * @return 数据库存储
     */
    @Override
    public StorageType getStorageType() {
        return StorageType.DATABASE;
    }
}

