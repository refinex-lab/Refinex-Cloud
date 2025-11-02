package cn.refinex.ai.controller.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模型配置查询请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "模型配置查询请求")
public class ModelConfigQueryRequestDTO {

    @Schema(description = "供应商", example = "QWEN")
    private String provider;

    @Schema(description = "模型类型", example = "CHAT")
    private String modelType;

    @Schema(description = "状态:1正常,0停用", example = "1")
    private Integer status;

    @Schema(description = "关键词（搜索模型名称和编码）", example = "通义千问")
    private String keyword;
}

