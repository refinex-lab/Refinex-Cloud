package cn.refinex.kb.controller.document.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建文档请求DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "创建文档请求")
public class ContentDocumentCreateRequestDTO {

    @Schema(description = "所属空间ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "所属空间ID不能为空")
    private Long spaceId;

    @Schema(description = "所属目录ID（可选，null表示根目录）", example = "50")
    private Long directoryId;

    @Schema(description = "文档标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "深入理解Java多线程")
    @NotBlank(message = "文档标题不能为空")
    @Size(max = 200, message = "文档标题不能超过200个字符")
    private String docTitle;

    @Schema(description = "文档摘要", example = "本文详细介绍Java多线程的概念和实践")
    @Size(max = 500, message = "文档摘要不能超过500个字符")
    private String docSummary;

    @Schema(description = "内容类型：MARKDOWN,RICHTEXT,VIDEO,MIXED", example = "MARKDOWN")
    private String contentType = "MARKDOWN";

    @Schema(description = "内容正文（可选，创建时可为空）")
    private String contentBody;

    @Schema(description = "封面图URL", example = "https://example.com/cover.jpg")
    @Size(max = 500, message = "封面图URL不能超过500个字符")
    private String coverImage;

    @Schema(description = "访问类型：0继承空间,1自定义私有,2自定义公开", example = "0")
    private Integer accessType = 0;

    @Schema(description = "是否付费文档：0否,1是", example = "0")
    private Integer isPaid = 0;

    @Schema(description = "付费金额，单位为分", example = "999")
    private Long paidAmount;

    @Schema(description = "SEO关键词", example = "Java,多线程,并发编程")
    @Size(max = 200, message = "SEO关键词不能超过200个字符")
    private String seoKeywords;

    @Schema(description = "SEO描述", example = "深入理解Java多线程的核心概念和实践技巧")
    @Size(max = 500, message = "SEO描述不能超过500个字符")
    private String seoDescription;

    @Schema(description = "排序字段", example = "0")
    private Integer sort = 0;

    @Schema(description = "备注说明", example = "技术文章")
    @Size(max = 500, message = "备注说明不能超过500个字符")
    private String remark;
}

