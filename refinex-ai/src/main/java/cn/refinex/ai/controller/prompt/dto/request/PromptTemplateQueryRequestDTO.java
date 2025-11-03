package cn.refinex.ai.controller.prompt.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 提示词模板查询请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "提示词模板查询请求")
public class PromptTemplateQueryRequestDTO {

    @Schema(description = "模板分类", example = "写作助手")
    private String category;

    @Schema(description = "模板类型:SYSTEM,USER", example = "USER")
    private String type;

    @Schema(description = "是否公开:0否,1是", example = "1")
    private Integer isPublic;

    @Schema(description = "状态:1正常,0停用", example = "1")
    private Integer status;

    @Schema(description = "关键词（搜索模板名称和编码）", example = "写作")
    private String keyword;
}

