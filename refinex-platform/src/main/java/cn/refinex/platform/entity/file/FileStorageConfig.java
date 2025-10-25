package cn.refinex.platform.entity.file;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 存储策略配置实体类
 * <p>
 * 对应数据库表：file_storage_config
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "存储策略配置实体")
public class FileStorageConfig {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "配置编码", example = "OSS_ALIYUN")
    private String configCode;

    @Schema(description = "配置名称", example = "阿里云OSS")
    private String configName;

    @Schema(description = "存储类型：OSS,MINIO,S3,DB", example = "OSS")
    private String storageType;

    @Schema(description = "服务提供商：ALIYUN,TENCENT,AWS", example = "ALIYUN")
    private String provider;

    @Schema(description = "访问密钥，加密存储")
    private String accessKey;

    @Schema(description = "访问密钥，加密存储")
    private String secretKey;

    @Schema(description = "访问端点URL", example = "https://oss-cn-shanghai.aliyuncs.com")
    private String endpoint;

    @Schema(description = "区域", example = "cn-shanghai")
    private String region;

    @Schema(description = "默认存储桶名称", example = "refinex-files")
    private String bucketName;

    @Schema(description = "基础路径前缀", example = "files/")
    private String basePath;

    @Schema(description = "访问域名", example = "https://cdn.example.com")
    private String domainUrl;

    @Schema(description = "是否默认配置：0否,1是", example = "1")
    private Integer isDefault;

    @Schema(description = "是否启用：0否,1是", example = "1")
    private Integer isEnabled;

    @Schema(description = "优先级", example = "0")
    private Integer priority;

    @Schema(description = "最大文件大小限制，单位字节", example = "104857600")
    private Long maxFileSize;

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

    @Schema(description = "备注说明", example = "阿里云OSS存储配置")
    private String remark;
}
