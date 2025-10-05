package cn.refinex.platform.service;

import cn.hutool.core.util.RandomUtil;
import cn.refinex.api.platform.domain.dto.file.*;
import cn.refinex.common.file.core.FileStorage;
import cn.refinex.common.file.core.FileStorageFactory;
import cn.refinex.common.file.domain.entity.FileInfo;
import cn.refinex.common.file.domain.entity.FileStorageConfig;
import cn.refinex.common.file.enums.StorageType;
import cn.refinex.common.file.exception.FileErrorCode;
import cn.refinex.common.file.exception.FileException;
import cn.refinex.common.file.repository.FileInfoRepository;
import cn.refinex.common.file.repository.FileStorageConfigRepository;
import cn.refinex.common.file.service.FileDeduplicationService;
import cn.refinex.common.file.service.ThumbnailService;
import cn.refinex.common.utils.algorithm.SnowflakeIdGenerator;
import cn.refinex.common.utils.file.FileUtils;
import cn.refinex.common.utils.object.BeanConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 文件服务
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileStorageFactory storageFactory;
    private final FileInfoRepository fileInfoRepository;
    private final FileStorageConfigRepository storageConfigRepository;
    private final FileDeduplicationService deduplicationService;
    private final ThumbnailService thumbnailService;
    private final SnowflakeIdGenerator idGenerator;

    /**
     * 生成文件上传 URL
     *
     * @param request 上传请求
     * @param userId  用户 ID
     * @return 上传 URL 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public FileUploadUrlResult generateUploadUrl(FileUploadUrlRequest request, Long userId) {
        try {
            // 1. 检查是否可以秒传
            if (request.getFileMd5() != null && !request.getFileMd5().isEmpty()) {
                FileInfo existingFile = deduplicationService.checkFileExists(request.getFileMd5());
                if (existingFile != null) {
                    // 秒传：复制文件元数据
                    FileInfo newFile = deduplicationService.copyFileMetadata(
                            existingFile, userId, request.getBizType(), request.getBizId());

                    log.info("文件秒传成功，fileGuid={}, md5={}", newFile.getFileGuid(), request.getFileMd5());

                    return FileUploadUrlResult.builder()
                            .fileGuid(newFile.getFileGuid())
                            .isInstantUpload(true)
                            .fileInfo(BeanConverter.toBean(newFile, FileInfoDTO.class))
                            .build();
                }
            }

            // 2. 获取存储配置, 默认使用 S3
            StorageType storageType = request.getStorageType() != null
                    ? StorageType.of(request.getStorageType())
                    : StorageType.S3;
            FileStorageConfig config = storageConfigRepository.findDefaultConfig(storageType.getCode());
            if (config == null) {
                throw new FileException(FileErrorCode.STORAGE_CONFIG_NOT_FOUND);
            }

            // 3. 检测文件 MIME 类型、提取文件扩展名
            String mimeType = FileUtils.getMimeTypeByFileName(request.getFileName());
            String fileExtension = FileUtils.getExtension(request.getFileName());

            // 4. 创建文件元数据（预分配）
            FileInfo fileInfo = new FileInfo();
            fileInfo.setId(idGenerator.nextId());
            fileInfo.setFileGuid(RandomUtil.randomString(36));
            fileInfo.setFileName(generateFileName(request.getFileName()));
            fileInfo.setOriginalName(request.getFileName());
            fileInfo.setFileExtension(fileExtension);
            fileInfo.setFileSize(request.getFileSize());
            fileInfo.setFileType(mimeType);
            fileInfo.setStorageStrategy(storageType.getCode());
            fileInfo.setStorageConfigId(config.getId());
            fileInfo.setFileMd5(request.getFileMd5());
            fileInfo.setUploaderId(userId);
            fileInfo.setBizType(request.getBizType());
            fileInfo.setBizId(request.getBizId());
            fileInfo.setRefCount(0);
            fileInfo.setIsPublic(request.getIsPublic());
            fileInfo.setCreateTime(LocalDateTime.now());
            fileInfo.setUpdateTime(LocalDateTime.now());
            fileInfo.setDeleted(0);
            fileInfo.setVersion(0);

            // 5. 构建存储路径
            String storageKey = buildStorageKey(fileInfo);
            String storagePath = config.getId() + ":" + config.getBucketName() + ":" + storageKey;
            fileInfo.setStoragePath(storagePath);
            fileInfo.setBucketName(config.getBucketName());
            fileInfo.setAccessUrl(buildAccessUrl(config, storageKey));

            // 6. 保存文件元数据（状态：待上传）
            fileInfoRepository.insert(fileInfo);

            // 7. 生成预签名上传 URL
            FileStorage storage = storageFactory.getStorage(storageType);
            String uploadUrl = storage.generatePresignedUrl(storagePath, Duration.ofHours(1));

            log.info("生成上传 URL 成功，fileGuid={}, uploadUrl={}", fileInfo.getFileGuid(), uploadUrl);

            return FileUploadUrlResult.builder()
                    .fileGuid(fileInfo.getFileGuid())
                    .uploadUrl(uploadUrl)
                    .expiresIn(3600L)
                    .isInstantUpload(false)
                    .build();

        } catch (Exception e) {
            log.error("生成上传 URL 失败", e);
            throw new FileException(FileErrorCode.FILE_UPLOAD_FAILED, e);
        }
    }

    /**
     * 确认文件上传完成
     *
     * @param request 确认请求
     * @return 文件信息
     */
    @Transactional(rollbackFor = Exception.class)
    public FileInfoDTO confirmUpload(FileConfirmUploadRequest request) {
        try {
            // 1. 查询文件元数据
            FileInfo fileInfo = fileInfoRepository.findByFileGuid(request.getFileGuid());
            if (fileInfo == null) {
                throw new FileException(FileErrorCode.FILE_NOT_FOUND);
            }

            // 2. 更新文件大小和 MD5（如果提供）
            if (request.getActualFileSize() != null) {
                fileInfo.setFileSize(request.getActualFileSize());
            }
            if (request.getActualFileMd5() != null) {
                fileInfo.setFileMd5(request.getActualFileMd5());
            }
            fileInfo.setUpdateTime(LocalDateTime.now());
            fileInfoRepository.update(fileInfo);

            // 3. 生成缩略图（如果需要）
            if (thumbnailService.shouldGenerateThumbnail(fileInfo.getFileType())) {
                generateThumbnailAsync(fileInfo);
            }

            log.info("文件上传确认成功，fileGuid={}", request.getFileGuid());

            return BeanConverter.toBean(fileInfo, FileInfoDTO.class);

        } catch (Exception e) {
            log.error("确认上传失败，fileGuid={}", request.getFileGuid(), e);
            throw new FileException(FileErrorCode.FILE_UPLOAD_FAILED, e);
        }
    }

    /**
     * 生成文件下载 URL
     *
     * @param fileGuid  文件 GUID
     * @param expiresIn URL 有效期（秒）
     * @return 下载 URL
     */
    public String generateDownloadUrl(String fileGuid, Long expiresIn) {
        try {
            // 1. 查询文件元数据
            FileInfo fileInfo = fileInfoRepository.findByFileGuid(fileGuid);
            if (fileInfo == null) {
                throw new FileException(FileErrorCode.FILE_NOT_FOUND);
            }

            // 2. 获取存储实现
            FileStorage storage = storageFactory.getStorage(fileInfo.getStorageStrategy());

            // 3. 生成预签名 URL
            Duration duration = Duration.ofSeconds(expiresIn != null ? expiresIn : 3600);
            String downloadUrl = storage.generatePresignedUrl(fileInfo.getStoragePath(), duration);

            log.info("生成下载 URL 成功，fileGuid={}", fileGuid);
            return downloadUrl;

        } catch (Exception e) {
            log.error("生成下载 URL 失败，fileGuid={}", fileGuid, e);
            throw new FileException(FileErrorCode.FILE_DOWNLOAD_FAILED, e);
        }
    }

    /**
     * 获取文件信息
     *
     * @param fileGuid 文件 GUID
     * @return 文件信息
     */
    public FileInfoDTO getFileInfo(String fileGuid) {
        FileInfo fileInfo = fileInfoRepository.findByFileGuid(fileGuid);
        if (fileInfo == null) {
            throw new FileException(FileErrorCode.FILE_NOT_FOUND);
        }
        return BeanConverter.toBean(fileInfo, FileInfoDTO.class);
    }

    /**
     * 删除文件
     *
     * @param fileGuid 文件 GUID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(String fileGuid) {
        try {
            // 1. 查询文件元数据
            FileInfo fileInfo = fileInfoRepository.findByFileGuid(fileGuid);
            if (fileInfo == null) {
                throw new FileException(FileErrorCode.FILE_NOT_FOUND);
            }

            // 2. 检查引用计数
            if (fileInfo.getRefCount() > 0) {
                throw new FileException(FileErrorCode.FILE_REF_COUNT_NOT_ZERO);
            }

            // 3. 逻辑删除文件元数据
            fileInfoRepository.logicalDelete(fileInfo.getId());

            // 4. 物理删除文件（可选，也可以通过定时任务异步删除）
            FileStorage storage = storageFactory.getStorage(fileInfo.getStorageStrategy());
            storage.delete(fileInfo.getStoragePath());

            log.info("文件已删除，fileGuid={}", fileGuid);

        } catch (Exception e) {
            log.error("文件删除失败，fileGuid={}", fileGuid, e);
            throw new FileException(FileErrorCode.FILE_DELETE_FAILED, e);
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 生成文件名
     */
    private String generateFileName(String originalFilename) {
        String extension = FilenameUtils.getExtension(originalFilename);
        return UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }

    /**
     * 构建存储路径
     * <p>
     * 格式：{bizType}/{year}/{month}/{day}/{fileName}
     */
    private String buildStorageKey(FileInfo fileInfo) {
        LocalDateTime now = LocalDateTime.now();
        return String.format("%s/%04d/%02d/%02d/%s",
                fileInfo.getBizType(),
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth(),
                fileInfo.getFileName());
    }

    /**
     * 构建访问 URL
     *
     * @param config      存储配置
     * @param storageKey  存储路径
     * @return            访问 URL
     */
    private String buildAccessUrl(FileStorageConfig config, String storageKey) {
        if (config.getDomainUrl() != null && !config.getDomainUrl().isEmpty()) {
            return config.getDomainUrl() + "/" + storageKey;
        }
        return config.getEndpoint() + "/" + config.getBucketName() + "/" + storageKey;
    }

    /**
     * 异步生成缩略图
     *
     * @param fileInfo 文件元数据
     */
    private void generateThumbnailAsync(FileInfo fileInfo) {
        try {
            // 调用 ThumbnailService 的异步方法
            thumbnailService.generateThumbnailAsync(fileInfo);
            log.info("已触发异步缩略图生成，fileGuid={}", fileInfo.getFileGuid());
        } catch (Exception e) {
            log.error("触发异步缩略图生成失败，fileGuid={}", fileInfo.getFileGuid(), e);
            // 不抛出异常，避免影响主流程
        }
    }
}

