package cn.refinex.platform.controller.file.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件信息 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Feign API - 文件信息传输对象")
public class FileInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "文件 GUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String fileGuid;

    @Schema(description = "文件名", example = "example.jpg")
    private String fileName;

    @Schema(description = "原始文件名", example = "example.jpg")
    private String originalName;

    @Schema(description = "文件扩展名", example = "jpg")
    private String fileExtension;

    @Schema(description = "文件大小（字节）", example = "102400")
    private Long fileSize;

    @Schema(description = "文件类型（MIME Type）", example = "image/jpeg")
    private String fileType;

    @Schema(description = "存储策略", example = "oss")
    private String storageStrategy;

    @Schema(description = "访问 URL", example = "https://example.com/example.jpg")
    private String accessUrl;

    @Schema(description = "缩略图 URL", example = "https://example.com/example_thumbnail.jpg")
    private String thumbnailUrl;

    @Schema(description = "文件 MD5", example = "1234567890abcdef1234567890abcdef")
    private String fileMd5;

    @Schema(description = "上传者 ID", example = "1")
    private Long uploaderId;

    @Schema(description = "业务类型", example = "AVATAR")
    private String bizType;

    @Schema(description = "业务 ID", example = "1")
    private Long bizId;

    @Schema(description = "是否公开", example = "1")
    private Integer isPublic;

    @Schema(description = "创建时间", example = "2025-10-04T12:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-10-04T12:00:00")
    private LocalDateTime updateTime;
}

