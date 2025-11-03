package cn.refinex.ai.controller.prompt.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 提示词模板更新请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "提示词模板更新请求")
public class PromptTemplateUpdateRequestDTO {

    @NotBlank(message = "模板名称不能为空")
    @Size(max = 100, message = "模板名称长度不能超过100")
    @Schema(description = "模板名称", example = "写作助手", requiredMode = Schema.RequiredMode.REQUIRED)
    private String templateName;

    @NotBlank(message = "模板内容不能为空")
    @Schema(description = "模板内容,支持变量占位符", requiredMode = Schema.RequiredMode.REQUIRED)
    private String templateContent;

    @NotBlank(message = "模板类型不能为空")
    @Size(max = 20, message = "模板类型长度不能超过20")
    @Schema(description = "模板类型:SYSTEM,USER", example = "USER", requiredMode = Schema.RequiredMode.REQUIRED)
    private String templateType;

    @Size(max = 50, message = "模板分类长度不能超过50")
    @Schema(description = "模板分类", example = "写作助手")
    private String templateCategory;

    @Schema(description = "适用模型数组（JSON格式）", example = "[\"QWEN_MAX\",\"GPT4\"]")
    private String applicableModels;

    @NotNull(message = "是否公开不能为空")
    @Min(value = 0, message = "是否公开只能为0或1")
    @Max(value = 1, message = "是否公开只能为0或1")
    @Schema(description = "是否公开:0否,1是", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer isPublic;

    @Size(max = 500, message = "备注说明长度不能超过500")
    @Schema(description = "备注说明")
    private String remark;

    @NotNull(message = "排序字段不能为空")
    @Schema(description = "排序字段", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sort;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态只能为0或1")
    @Max(value = 1, message = "状态只能为0或1")
    @Schema(description = "状态:1正常,0停用", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Schema(description = "扩展数据（JSON格式）")
    private String extraData;
}

