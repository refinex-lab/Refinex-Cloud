package cn.refinex.common.file.config.properties;

import cn.refinex.common.file.enums.StorageType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件存储配置属性
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "refinex.file")
public class FileProperties {

    /**
     * 是否启用文件存储模块
     */
    private boolean enabled = true;

    /**
     * 默认存储类型
     */
    private StorageType defaultStorageType = StorageType.S3;

    /**
     * 上传配置
     */
    private UploadConfig upload = new UploadConfig();

    /**
     * 下载配置
     */
    private DownloadConfig download = new DownloadConfig();

    /**
     * 图片配置
     */
    private ImageConfig image = new ImageConfig();

    /**
     * 存储配置（可选，优先使用数据库配置）
     */
    private Map<String, StorageConfig> storage = new HashMap<>();

    /**
     * 上传配置
     */
    @Data
    public static class UploadConfig {
        /**
         * 最大文件大小（如 100MB）
         */
        private String maxFileSize = "100MB";

        /**
         * 允许的文件扩展名（为空表示不限制）
         */
        private List<String> allowedExtensions;

        /**
         * 分片大小（如 5MB）
         */
        private String chunkSize = "5MB";
    }

    /**
     * 下载配置
     */
    @Data
    public static class DownloadConfig {
        /**
         * 预签名 URL 过期时间
         */
        private Duration presignedUrlExpiration = Duration.ofHours(1);
    }

    /**
     * 图片配置
     */
    @Data
    public static class ImageConfig {
        /**
         * 缩略图宽度
         */
        private int thumbnailWidth = 300;

        /**
         * 缩略图高度
         */
        private int thumbnailHeight = 300;

        /**
         * 缩略图格式（webp、jpg、png）
         */
        private String thumbnailFormat = "webp";
    }

    /**
     * 存储配置
     */
    @Data
    public static class StorageConfig {
        /**
         * 访问密钥
         */
        private String accessKey;

        /**
         * 访问密钥
         */
        private String secretKey;

        /**
         * 区域
         */
        private String region;

        /**
         * 存储桶名称
         */
        private String bucketName;

        /**
         * 访问端点 URL
         */
        private String endpoint;

        /**
         * 基础路径前缀
         */
        private String basePath;

        /**
         * 访问域名
         */
        private String domainUrl;
    }
}

