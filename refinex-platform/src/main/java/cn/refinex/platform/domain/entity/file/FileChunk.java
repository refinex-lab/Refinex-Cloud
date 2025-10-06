package cn.refinex.platform.domain.entity.file;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文件分片实体类
 * <p>
 * 对应数据库表：file_chunk
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件分片实体")
public class FileChunk {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "上传任务ID", example = "upload-1234567890")
    private String uploadId;

    @Schema(description = "文件MD5", example = "5d41402abc4b2a76b9719d911017c592")
    private String fileMd5;

    @Schema(description = "文件名", example = "large-file.zip")
    private String fileName;

    @Schema(description = "文件总大小", example = "104857600")
    private Long fileSize;

    @Schema(description = "分片大小", example = "5242880")
    private Integer chunkSize;

    @Schema(description = "总分片数", example = "20")
    private Integer totalChunks;

    @Schema(description = "已上传分片号列表（JSON格式）", example = "[1,2,3,4,5]")
    private String uploadedChunks;

    @Schema(description = "合并状态：0上传中,1已合并,2合并失败", example = "0")
    private Integer mergeStatus;

    @Schema(description = "上传者ID", example = "1")
    private Long uploaderId;

    @Schema(description = "过期时间", example = "2025-10-06 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

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
}
