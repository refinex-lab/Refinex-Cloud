package cn.refinex.common.file.service;

import cn.refinex.common.file.config.properties.FileProperties;
import cn.refinex.common.file.core.FileStorage;
import cn.refinex.common.file.core.FileStorageFactory;
import cn.refinex.common.file.domain.entity.FileInfo;
import cn.refinex.common.file.exception.FileErrorCode;
import cn.refinex.common.file.exception.FileException;
import cn.refinex.common.file.repository.FileInfoRepository;
import cn.refinex.common.utils.algorithm.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * 缩略图生成服务
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ThumbnailService {

    private final FileProperties fileProperties;
    private final FileStorageFactory storageFactory;
    private final FileInfoRepository fileInfoRepository;
    private final SnowflakeIdGenerator idGenerator;

    /**
     * 生成缩略图
     *
     * @param originalInputStream 原始图片输入流
     * @param originalFormat      原始图片格式（如 jpg、png）
     * @return 缩略图输入流
     */
    public InputStream generateThumbnail(InputStream originalInputStream, String originalFormat) {
        try {
            // 1. 获取配置
            int width = fileProperties.getImage().getThumbnailWidth();
            int height = fileProperties.getImage().getThumbnailHeight();
            String format = fileProperties.getImage().getThumbnailFormat();

            // 2. 生成缩略图
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(originalInputStream)
                    .size(width, height)
                    .outputFormat(format)
                    .toOutputStream(outputStream);

            byte[] thumbnailBytes = outputStream.toByteArray();
            log.info("缩略图已生成，size={}x{}, format={}, bytes={}", width, height, format, thumbnailBytes.length);

            return new ByteArrayInputStream(thumbnailBytes);

        } catch (Exception e) {
            log.error("缩略图生成失败", e);
            throw new FileException(FileErrorCode.THUMBNAIL_GENERATE_FAILED, e);
        }
    }

    /**
     * 异步生成缩略图并保存
     *
     * @param fileInfo 原始文件信息
     */
    @Async
    public void generateThumbnailAsync(FileInfo fileInfo) {
        try {
            log.info("开始异步生成缩略图，fileGuid={}", fileInfo.getFileGuid());

            // 1. 获取存储实现
            FileStorage storage = storageFactory.getStorage(fileInfo.getStorageStrategy());

            // 2. 下载原始图片
            InputStream originalInputStream = storage.download(fileInfo.getStoragePath());

            // 3. 生成缩略图
            InputStream thumbnailInputStream = generateThumbnail(originalInputStream, fileInfo.getFileExtension());

            // 4. 构建缩略图存储路径
            String thumbnailKey = buildThumbnailKey(fileInfo);
            String thumbnailStoragePath = fileInfo.getStorageConfigId() + ":" + fileInfo.getBucketName() + ":" + thumbnailKey;

            // 5. 上传缩略图
            storage.upload(thumbnailInputStream, thumbnailStoragePath, "image/" + fileProperties.getImage().getThumbnailFormat(), new HashMap<>());

            // 6. 创建缩略图文件元数据
            FileInfo thumbnailFileInfo = new FileInfo();
            thumbnailFileInfo.setId(idGenerator.nextId());
            thumbnailFileInfo.setFileGuid(fileInfo.getFileGuid() + "_thumb");
            thumbnailFileInfo.setFileName("thumb_" + fileInfo.getFileName());
            thumbnailFileInfo.setOriginalName("thumb_" + fileInfo.getOriginalName());
            thumbnailFileInfo.setFileExtension(fileProperties.getImage().getThumbnailFormat());
            thumbnailFileInfo.setFileType("image/" + fileProperties.getImage().getThumbnailFormat());
            thumbnailFileInfo.setStorageStrategy(fileInfo.getStorageStrategy());
            thumbnailFileInfo.setStorageConfigId(fileInfo.getStorageConfigId());
            thumbnailFileInfo.setStoragePath(thumbnailStoragePath);
            thumbnailFileInfo.setBucketName(fileInfo.getBucketName());
            thumbnailFileInfo.setUploaderId(fileInfo.getUploaderId());
            thumbnailFileInfo.setBizType(fileInfo.getBizType());
            thumbnailFileInfo.setBizId(fileInfo.getBizId());
            thumbnailFileInfo.setRefCount(0);
            thumbnailFileInfo.setIsPublic(fileInfo.getIsPublic());
            thumbnailFileInfo.setCreateTime(fileInfo.getCreateTime());
            thumbnailFileInfo.setUpdateTime(fileInfo.getUpdateTime());
            thumbnailFileInfo.setDeleted(0);
            thumbnailFileInfo.setVersion(0);

            // 7. 保存缩略图元数据
            fileInfoRepository.insert(thumbnailFileInfo);

            // 8. 更新原始文件的缩略图 ID
            fileInfo.setThumbnailFileId(thumbnailFileInfo.getId());
            fileInfoRepository.update(fileInfo);

            log.info("缩略图生成成功，fileGuid={}, thumbnailFileId={}", fileInfo.getFileGuid(), thumbnailFileInfo.getId());

        } catch (Exception e) {
            log.error("异步生成缩略图失败，fileGuid={}", fileInfo.getFileGuid(), e);
            // 不抛出异常，避免影响主流程
        }
    }

    /**
     * 判断是否需要生成缩略图
     *
     * @param mimeType 文件 MIME 类型
     * @return true 需要生成，false 不需要
     */
    public boolean shouldGenerateThumbnail(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        // 仅对图片类型生成缩略图（排除 SVG）
        return mimeType.startsWith("image/") && !"image/svg+xml".equals(mimeType);
    }

    /**
     * 构建缩略图存储路径
     *
     * @param fileInfo 原始文件信息
     * @return 缩略图存储路径
     */
    private String buildThumbnailKey(FileInfo fileInfo) {
        // 从原始存储路径中提取目录部分
        String originalKey = fileInfo.getStoragePath().split(":", 3)[2];
        int lastSlashIndex = originalKey.lastIndexOf('/');
        String directory = lastSlashIndex > 0 ? originalKey.substring(0, lastSlashIndex) : "";
        String fileName = "thumb_" + fileInfo.getFileName();
        return directory.isEmpty() ? fileName : directory + "/" + fileName;
    }
}

