package cn.refinex.platform.domain.entity.file;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文件元数据实体类
 * <p>
 * 对应数据库表：file_info
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件元数据实体")
public class FileInfo {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "文件全局唯一标识，UUID格式", example = "550e8400-e29b-41d4-a716-446655440000")
    private String fileGuid;

    @Schema(description = "文件名", example = "document.pdf")
    private String fileName;

    @Schema(description = "原始文件名", example = "用户上传的文档.pdf")
    private String originalName;

    @Schema(description = "文件扩展名", example = "pdf")
    private String fileExtension;

    @Schema(description = "文件大小，单位字节", example = "1048576")
    private Long fileSize;

    @Schema(description = "文件MIME类型", example = "application/pdf")
    private String fileType;

    @Schema(description = "存储策略：OSS,MINIO,S3,DB", example = "OSS")
    private String storageStrategy;

    @Schema(description = "存储配置ID", example = "1")
    private Long storageConfigId;

    @Schema(description = "存储路径或对象key", example = "2025/10/05/document.pdf")
    private String storagePath;

    @Schema(description = "存储桶名称", example = "refinex-files")
    private String bucketName;

    @Schema(description = "访问URL", example = "https://cdn.example.com/files/document.pdf")
    private String accessUrl;

    @Schema(description = "文件MD5哈希值", example = "5d41402abc4b2a76b9719d911017c592")
    private String fileMd5;

    @Schema(description = "缩略图文件ID", example = "2")
    private Long thumbnailFileId;

    @Schema(description = "上传者ID", example = "1")
    private Long uploaderId;

    @Schema(description = "业务类型：AVATAR,COVER,DOCUMENT,BLOG_IMAGE,VIDEO", example = "DOCUMENT")
    private String bizType;

    @Schema(description = "业务关联ID", example = "100")
    private Long bizId;

    @Schema(description = "引用次数", example = "5")
    private Integer refCount;

    @Schema(description = "是否公开访问：0否,1是", example = "0")
    private Integer isPublic;

    @Schema(description = "访问过期时间", example = "2025-12-31 23:59:59")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime accessExpires;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1")
    private Long updateBy;

    @Schema(description = "更新时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除标记：0未删除,1已删除", example = "0")
    private Integer deleted;

    @Schema(description = "乐观锁版本号", example = "0")
    private Integer version;

    @Schema(description = "备注说明", example = "用户上传的文档")
    private String remark;
}
