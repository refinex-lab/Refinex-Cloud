package cn.refinex.platform.controller.email.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 邮件模板分页查询条件
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "邮件模板分页查询条件")
public class EmailTemplateQueryRequestDTO {

    @Schema(description = "模板编码，支持模糊")
    private String templateCode;

    @Schema(description = "模板名称，支持模糊")
    private String templateName;

    @Schema(description = "模板类型：HTML,TEXT")
    private String templateType;

    @Schema(description = "模板分类")
    private String templateCategory;

    @Schema(description = "是否系统模板：0否,1是")
    private Integer isSystem;

    @Schema(description = "状态：1正常,0停用")
    private Integer status;
}


