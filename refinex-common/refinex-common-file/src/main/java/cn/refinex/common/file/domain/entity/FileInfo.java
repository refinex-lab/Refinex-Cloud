package cn.refinex.common.file.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文件元数据实体
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FileInfo {

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 文件全局唯一标识（UUID）
     */
    private String fileGuid;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件扩展名
     */
    private String fileExtension;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件 MIME 类型
     */
    private String fileType;

    /**
     * 存储策略（S3/OSS/COS/KODO/MINIO/DB）
     */
    private String storageStrategy;

    /**
     * 存储配置 ID
     */
    private Long storageConfigId;

    /**
     * 存储路径或对象 key
     */
    private String storagePath;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 访问 URL
     */
    private String accessUrl;

    /**
     * 文件 MD5 哈希值
     */
    private String fileMd5;

    /**
     * 缩略图文件 ID
     */
    private Long thumbnailFileId;

    /**
     * 上传者 ID
     */
    private Long uploaderId;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务关联 ID
     */
    private Long bizId;

    /**
     * 引用次数
     */
    private Integer refCount;

    /**
     * 是否公开访问（0否/1是）
     */
    private Integer isPublic;

    /**
     * 访问过期时间
     */
    private LocalDateTime accessExpires;

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

