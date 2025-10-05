package cn.refinex.common.file.core;

import cn.refinex.common.file.enums.StorageType;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * 文件存储统一接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface FileStorage {

    /**
     * 上传文件
     *
     * @param inputStream 文件输入流
     * @param fileName    文件名
     * @param contentType 文件 MIME 类型
     * @param metadata    文件元数据（可选）
     * @return 存储路径或对象 key
     */
    String upload(InputStream inputStream, String fileName, String contentType, Map<String, String> metadata);

    /**
     * 下载文件
     *
     * @param storageKey 存储路径或对象 key
     * @return 文件输入流
     */
    InputStream download(String storageKey);

    /**
     * 删除文件
     *
     * @param storageKey 存储路径或对象 key
     */
    void delete(String storageKey);

    /**
     * 生成预签名 URL（限时访问）
     *
     * @param storageKey 存储路径或对象 key
     * @param expiration 过期时间
     * @return 预签名 URL
     */
    String generatePresignedUrl(String storageKey, Duration expiration);

    /**
     * 初始化分片上传
     *
     * @param fileName    文件名
     * @param contentType 文件 MIME 类型
     * @return 上传任务 ID
     */
    String initiateMultipartUpload(String fileName, String contentType);

    /**
     * 上传分片
     *
     * @param uploadId    上传任务 ID
     * @param partNumber  分片序号（从 1 开始）
     * @param inputStream 分片输入流
     * @return 分片 ETag
     */
    String uploadPart(String uploadId, int partNumber, InputStream inputStream);

    /**
     * 完成分片上传
     *
     * @param uploadId 上传任务 ID
     * @param parts    分片 ETag 列表
     * @return 存储路径或对象 key
     */
    String completeMultipartUpload(String uploadId, List<PartETag> parts);

    /**
     * 获取存储类型
     *
     * @return 存储类型枚举
     */
    StorageType getStorageType();

    /**
     * 分片 ETag 信息
     *
     * @param partNumber 分片序号
     * @param eTag       分片 ETag
     */
    record PartETag(int partNumber, String eTag) {
    }
}

