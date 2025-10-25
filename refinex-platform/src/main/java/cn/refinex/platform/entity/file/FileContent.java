package cn.refinex.platform.entity.file;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文件内容实体类
 * <p>
 * 对应数据库表：file_content
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件内容实体")
public class FileContent {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "文件ID", example = "1")
    private Long fileId;

    @Schema(description = "文件二进制内容")
    private byte[] contentData;

    @Schema(description = "创建时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
