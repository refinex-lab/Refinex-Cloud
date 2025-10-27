package cn.refinex.kb.controller.document.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 保存文档内容请求DTO
 * <p>
 * 用于 MDXEditor 保存文档内容
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "保存文档内容请求")
public class ContentDocumentSaveContentRequestDTO {

    @Schema(description = "文档标题（可选，如果提供则同时更新标题）", example = "深入理解Java多线程")
    @Size(max = 200, message = "文档标题不能超过200个字符")
    private String docTitle;

    @Schema(description = "文档内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文档内容不能为空")
    private String contentBody;

    @Schema(description = "变更说明（可选）", example = "修正了部分代码示例，增加了性能优化章节")
    @Size(max = 500, message = "变更说明不能超过500个字符")
    private String changeSummary;

    @Schema(description = "是否自动生成摘要（如果为true且docSummary为空，则自动提取内容前200字作为摘要）", example = "true")
    private Boolean autoGenerateSummary = true;
}

