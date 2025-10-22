package cn.refinex.api.platform.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件上传 URL 请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Feign API - 文件上传 URL 请求")
public class FileUploadUrlRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "文件名", example = "example.jpg")
    private String fileName;

    @Schema(description = "文件大小（字节）", example = "102400")
    private Long fileSize;

    @Schema(description = "文件 MD5（用于秒传检测）", example = "1234567890abcdef1234567890abcdef")
    private String fileMd5;

    @Schema(description = "业务类型（如 AVATAR、DOCUMENT、IMAGE）", example = "IMAGE")
    private String bizType;

    @Schema(description = "业务 ID", example = "1")
    private Long bizId;

    @Schema(description = "是否公开访问（0否/1是）", example = "1")
    private Integer isPublic;

    @Schema(description = "存储类型（可选，不指定则使用默认配置）", example = "S3")
    private String storageType;
}

