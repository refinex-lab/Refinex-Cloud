package cn.refinex.kb.controller.document.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文档版本响应DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文档版本响应")
public class ContentDocumentVersionResponseDTO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "文档ID", example = "1000")
    private Long documentId;

    @Schema(description = "版本号", example = "3")
    private Integer versionNumber;

    @Schema(description = "内容快照（摘要，不返回完整内容）", example = "# 标题\n\n这是文档内容...")
    private String contentSummary;

    @Schema(description = "变更说明", example = "修正了部分代码示例，增加了性能优化章节")
    private String changeSummary;

    @Schema(description = "字数统计", example = "3500")
    private Integer wordCount;

    @Schema(description = "创建人ID", example = "1")
    private Long createdBy;

    @Schema(description = "创建人昵称", example = "张三")
    private String createdByName;

    @Schema(description = "创建时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "是否为当前版本", example = "true")
    private Boolean isCurrent;
}

