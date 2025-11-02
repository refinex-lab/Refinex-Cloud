package cn.refinex.platform.controller.email.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 邮件模板 响应对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "邮件模板响应")
public class EmailTemplateResponseDTO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "邮件主题")
    private String templateSubject;

    @Schema(description = "模板内容（HTML）")
    private String templateContent;

    @Schema(description = "模板类型")
    private String templateType;

    @Schema(description = "变量列表（JSON）")
    private String variables;

    @Schema(description = "模板分类")
    private String templateCategory;

    @Schema(description = "是否系统模板：0否,1是")
    private Integer isSystem;

    @Schema(description = "状态：1正常,0停用")
    private Integer status;

    @Schema(description = "备注说明")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}


