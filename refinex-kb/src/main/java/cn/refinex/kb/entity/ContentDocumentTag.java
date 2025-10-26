package cn.refinex.kb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文档标签关联实体类
 * <p>
 * 对应数据库表：content_document_tag
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文档标签关联实体")
public class ContentDocumentTag {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "文档ID", example = "1000")
    private Long documentId;

    @Schema(description = "标签ID", example = "50")
    private Long tagId;

    @Schema(description = "创建时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

