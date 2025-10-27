package cn.refinex.kb.controller.document.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查询文档请求DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "查询文档请求")
public class ContentDocumentQueryRequestDTO {

    @Schema(description = "所属空间ID", example = "100")
    private Long spaceId;

    @Schema(description = "所属目录ID", example = "50")
    private Long directoryId;

    @Schema(description = "文档标题（模糊查询）", example = "Java")
    private String docTitle;

    @Schema(description = "内容类型：MARKDOWN,RICHTEXT,VIDEO,MIXED", example = "MARKDOWN")
    private String contentType;

    @Schema(description = "文档状态：0草稿,1已发布,2已下架", example = "1")
    private Integer docStatus;

    @Schema(description = "是否付费文档：0否,1是", example = "0")
    private Integer isPaid;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "状态：0正常,1停用", example = "0")
    private Integer status;
}

