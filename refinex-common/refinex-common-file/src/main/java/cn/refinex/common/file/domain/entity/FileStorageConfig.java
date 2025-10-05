package cn.refinex.common.file.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 存储配置实体
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
public class FileStorageConfig {

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 配置编码
     */
    private String configCode;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 存储类型（S3/OSS/COS/KODO/MINIO/DB）
     */
    private String storageType;

    /**
     * 服务提供商（ALIYUN/TENCENT/AWS）
     */
    private String provider;

    /**
     * 访问密钥（加密存储，存储 sys_sensitive.row_guid）
     */
    private String accessKey;

    /**
     * 访问密钥（加密存储，存储 sys_sensitive.row_guid）
     */
    private String secretKey;

    /**
     * 访问端点 URL
     */
    private String endpoint;

    /**
     * 区域
     */
    private String region;

    /**
     * 默认存储桶名称
     */
    private String bucketName;

    /**
     * 基础路径前缀
     */
    private String basePath;

    /**
     * 访问域名
     */
    private String domainUrl;

    /**
     * 是否默认配置（0否/1是）
     */
    private Integer isDefault;

    /**
     * 是否启用（0否/1是）
     */
    private Integer isEnabled;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 最大文件大小限制（字节）
     */
    private Long maxFileSize;

    /**
     * 创建人 ID
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人 ID
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记（0未删除/1已删除）
     */
    private Integer deleted;

    /**
     * 乐观锁版本号
     */
    private Integer version;

    /**
     * 备注说明
     */
    private String remark;
}

