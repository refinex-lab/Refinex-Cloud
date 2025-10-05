package cn.refinex.common.file.storage.cos;

import cn.refinex.common.file.core.FileStorage;
import cn.refinex.common.file.domain.entity.FileStorageConfig;
import cn.refinex.common.file.enums.StorageType;
import cn.refinex.common.file.exception.FileErrorCode;
import cn.refinex.common.file.exception.FileException;
import cn.refinex.common.file.repository.FileStorageConfigRepository;
import cn.refinex.common.file.storage.s3.MultipartUploadContext;
import cn.refinex.common.file.storage.s3.S3ClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * 腾讯云 COS 文件存储实现
 * <p>
 * 基于 S3 协议兼容层实现，endpoint 配置为 COS 的 S3 兼容端点。
 * 例如：https://cos.ap-guangzhou.myqcloud.com
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CosFileStorage implements FileStorage {

    private final S3ClientFactory s3ClientFactory;
    private final FileStorageConfigRepository configRepository;

    /**
     * 上传文件到腾讯云 COS
     *
     * @param inputStream 文件输入流
     * @param fileName    文件名
     * @param contentType 文件 MIME 类型
     * @param metadata    文件元数据（包含 storageKey、bucketName、configId、fileSize）
     * @return 存储路径（格式：configId:bucketName:key）
     */
    @Override
    public String upload(InputStream inputStream, String fileName, String contentType, Map<String, String> metadata) {
        try {
            // 1. 获取配置
            Long configId = Long.parseLong(metadata.get("configId"));
            FileStorageConfig config = configRepository.findById(configId);
            if (config == null) {
                throw new FileException(FileErrorCode.STORAGE_CONFIG_NOT_FOUND, "存储配置不存在，configId=" + configId);
            }

            // 2. 获取 S3Client（复用 S3ClientFactory）
            S3Client s3Client = s3ClientFactory.getClient(config);

            // 3. 构建对象 key
            String storageKey = metadata.get("storageKey");
            String bucketName = metadata.getOrDefault("bucketName", config.getBucketName());

            // 4. 上传文件
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storageKey)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(inputStream, Long.parseLong(metadata.get("fileSize"))));

            log.info("文件已上传到腾讯云 COS，bucket={}, key={}", bucketName, storageKey);
            
            // 5. 返回完整存储路径（格式：configId:bucketName:key）
            return configId + ":" + bucketName + ":" + storageKey;

        } catch (Exception e) {
            log.error("文件上传到腾讯云 COS 失败，fileName={}", fileName, e);
            throw new FileException(FileErrorCode.FILE_UPLOAD_FAILED, e);
        }
    }

    /**
     * 从腾讯云 COS 下载文件
     *
     * @param storageKey 存储路径（格式：configId:bucketName:key）
     * @return 文件输入流
     */
    @Override
    public InputStream download(String storageKey) {
        try {
            // 1. 解析 storageKey
            String[] parts = storageKey.split(":", 3);
            Long configId = Long.parseLong(parts[0]);
            String bucketName = parts[1];
            String key = parts[2];

            // 2. 获取配置
            FileStorageConfig config = configRepository.findById(configId);
            if (config == null) {
                throw new FileException(FileErrorCode.STORAGE_CONFIG_NOT_FOUND, "存储配置不存在，configId=" + configId);
            }

            // 3. 获取 S3Client
            S3Client s3Client = s3ClientFactory.getClient(config);

            // 4. 下载文件
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            InputStream inputStream = s3Client.getObject(getRequest);
            log.info("文件已从腾讯云 COS 下载，bucket={}, key={}", bucketName, key);
            return inputStream;

        } catch (Exception e) {
            log.error("文件从腾讯云 COS 下载失败，storageKey={}", storageKey, e);
            throw new FileException(FileErrorCode.FILE_DOWNLOAD_FAILED, e);
        }
    }

    /**
     * 从腾讯云 COS 删除文件
     *
     * @param storageKey 存储路径（格式：configId:bucketName:key）
     */
    @Override
    public void delete(String storageKey) {
        try {
            // 1. 解析 storageKey
            String[] parts = storageKey.split(":", 3);
            Long configId = Long.parseLong(parts[0]);
            String bucketName = parts[1];
            String key = parts[2];

            // 2. 获取配置
            FileStorageConfig config = configRepository.findById(configId);
            if (config == null) {
                throw new FileException(FileErrorCode.STORAGE_CONFIG_NOT_FOUND, "存储配置不存在，configId=" + configId);
            }

            // 3. 获取 S3Client
            S3Client s3Client = s3ClientFactory.getClient(config);

            // 4. 删除文件
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("文件已从腾讯云 COS 删除，bucket={}, key={}", bucketName, key);

        } catch (Exception e) {
            log.error("文件从腾讯云 COS 删除失败，storageKey={}", storageKey, e);
            throw new FileException(FileErrorCode.FILE_DELETE_FAILED, e);
        }
    }

    /**
     * 生成预签名 URL
     *
     * @param storageKey 存储路径（格式：configId:bucketName:key）
     * @param expiration 过期时间
     * @return 预签名 URL
     */
    @Override
    public String generatePresignedUrl(String storageKey, Duration expiration) {
        try {
            // 1. 解析 storageKey
            String[] parts = storageKey.split(":", 3);
            Long configId = Long.parseLong(parts[0]);
            String bucketName = parts[1];
            String key = parts[2];

            // 2. 获取配置
            FileStorageConfig config = configRepository.findById(configId);
            if (config == null) {
                throw new FileException(FileErrorCode.STORAGE_CONFIG_NOT_FOUND, "存储配置不存在，configId=" + configId);
            }

            // 3. 创建 S3Presigner
            S3Presigner presigner = S3Presigner.create();

            // 4. 生成预签名 URL
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(expiration)
                    .getObjectRequest(getRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            String url = presignedRequest.url().toString();

            log.info("腾讯云 COS 预签名 URL 已生成，bucket={}, key={}, expiration={}", bucketName, key, expiration);
            return url;

        } catch (Exception e) {
            log.error("腾讯云 COS 预签名 URL 生成失败，storageKey={}", storageKey, e);
            throw new FileException(FileErrorCode.STORAGE_OPERATION_FAILED, "预签名 URL 生成失败", e);
        }
    }

    /**
     * 初始化分片上传
     * <p>
     * fileName 格式：configId:bucketName:key
     * </p>
     *
     * @param fileName    文件名（格式：configId:bucketName:key）
     * @param contentType 文件 MIME 类型
     * @return 上传任务 ID（Base64 编码的上下文）
     */
    @Override
    public String initiateMultipartUpload(String fileName, String contentType) {
        try {
            // 1. 解析 fileName（格式：configId:bucketName:key）
            String[] parts = fileName.split(":", 3);
            Long configId = Long.parseLong(parts[0]);
            String bucketName = parts[1];
            String key = parts[2];

            // 2. 获取配置
            FileStorageConfig config = configRepository.findById(configId);
            if (config == null) {
                throw new FileException(FileErrorCode.STORAGE_CONFIG_NOT_FOUND, "存储配置不存在，configId=" + configId);
            }

            // 3. 获取 S3Client（复用 S3ClientFactory）
            S3Client s3Client = s3ClientFactory.getClient(config);

            // 4. 初始化分片上传
            CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            CreateMultipartUploadResponse response = s3Client.createMultipartUpload(createRequest);
            String s3UploadId = response.uploadId();

            // 5. 创建上下文并编码
            MultipartUploadContext context = new MultipartUploadContext(configId, bucketName, key, s3UploadId);
            String encodedContext = context.encode();

            log.info("腾讯云 COS 分片上传已初始化，bucket={}, key={}, uploadId={}", bucketName, key, s3UploadId);
            return encodedContext;

        } catch (Exception e) {
            log.error("腾讯云 COS 分片上传初始化失败，fileName={}", fileName, e);
            throw new FileException(FileErrorCode.FILE_UPLOAD_FAILED, "分片上传初始化失败", e);
        }
    }

    /**
     * 上传分片
     *
     * @param uploadId    上传任务 ID（Base64 编码的上下文）
     * @param partNumber  分片序号（从 1 开始）
     * @param inputStream 分片输入流
     * @return 分片 ETag
     */
    @Override
    public String uploadPart(String uploadId, int partNumber, InputStream inputStream) {
        try {
            // 1. 解码上下文
            MultipartUploadContext context = MultipartUploadContext.decode(uploadId);

            // 2. 获取配置
            FileStorageConfig config = configRepository.findById(context.getConfigId());
            if (config == null) {
                throw new FileException(FileErrorCode.STORAGE_CONFIG_NOT_FOUND, "存储配置不存在，configId=" + context.getConfigId());
            }

            // 3. 获取 S3Client
            S3Client s3Client = s3ClientFactory.getClient(config);

            // 4. 上传分片
            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(context.getBucketName())
                    .key(context.getKey())
                    .uploadId(context.getS3UploadId())
                    .partNumber(partNumber)
                    .build();

            UploadPartResponse response = s3Client.uploadPart(uploadPartRequest, RequestBody.fromInputStream(inputStream, inputStream.available()));
            String eTag = response.eTag();

            log.info("腾讯云 COS 分片已上传，bucket={}, key={}, partNumber={}, eTag={}", context.getBucketName(), context.getKey(), partNumber, eTag);
            return eTag;

        } catch (Exception e) {
            log.error("腾讯云 COS 分片上传失败，uploadId={}, partNumber={}", uploadId, partNumber, e);
            throw new FileException(FileErrorCode.FILE_UPLOAD_FAILED, "分片上传失败", e);
        }
    }

    /**
     * 完成分片上传
     *
     * @param uploadId 上传任务 ID（Base64 编码的上下文）
     * @param parts    分片 ETag 列表
     * @return 存储路径（格式：configId:bucketName:key）
     */
    @Override
    public String completeMultipartUpload(String uploadId, List<PartETag> parts) {
        try {
            // 1. 解码上下文
            MultipartUploadContext context = MultipartUploadContext.decode(uploadId);

            // 2. 获取配置
            FileStorageConfig config = configRepository.findById(context.getConfigId());
            if (config == null) {
                throw new FileException(FileErrorCode.STORAGE_CONFIG_NOT_FOUND, "存储配置不存在，configId=" + context.getConfigId());
            }

            // 3. 获取 S3Client
            S3Client s3Client = s3ClientFactory.getClient(config);

            // 4. 构建 CompletedPart 列表
            List<CompletedPart> completedParts = parts.stream()
                    .map(part -> CompletedPart.builder()
                            .partNumber(part.partNumber())
                            .eTag(part.eTag())
                            .build())
                    .toList();

            // 5. 完成分片上传
            CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                    .bucket(context.getBucketName())
                    .key(context.getKey())
                    .uploadId(context.getS3UploadId())
                    .multipartUpload(CompletedMultipartUpload.builder()
                            .parts(completedParts)
                            .build())
                    .build();

            s3Client.completeMultipartUpload(completeRequest);

            log.info("腾讯云 COS 分片上传已完成，bucket={}, key={}, parts={}", context.getBucketName(), context.getKey(), parts.size());

            // 6. 返回完整存储路径（格式：configId:bucketName:key）
            return context.getConfigId() + ":" + context.getBucketName() + ":" + context.getKey();

        } catch (Exception e) {
            log.error("腾讯云 COS 分片上传完成失败，uploadId={}", uploadId, e);
            throw new FileException(FileErrorCode.FILE_UPLOAD_FAILED, "分片上传完成失败", e);
        }
    }

    /**
     * 获取存储类型
     *
     * @return 腾讯云 COS 存储
     */
    @Override
    public StorageType getStorageType() {
        return StorageType.COS;
    }
}

