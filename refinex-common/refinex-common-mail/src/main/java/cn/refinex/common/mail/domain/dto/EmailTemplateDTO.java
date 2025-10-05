package cn.refinex.common.mail.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 邮件模板 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplateDTO {

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 模板编码（必填）
     */
    @NotBlank(message = "模板编码不能为空")
    private String templateCode;

    /**
     * 模板名称（必填）
     */
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    /**
     * 邮件主题（必填）
     */
    @NotBlank(message = "邮件主题不能为空")
    private String templateSubject;

    /**
     * 模板内容（必填）
     */
    @NotBlank(message = "模板内容不能为空")
    private String templateContent;

    /**
     * 模板类型（HTML、TEXT）
     */
    private String templateType;

    /**
     * 变量列表
     */
    private List<String> variables;

    /**
     * 模板分类（验证码、通知、营销）
     */
    private String templateCategory;

    /**
     * 是否系统模板（0否、1是）
     */
    private Integer isSystem;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 状态（0正常、1停用）
     */
    private Integer status;

    /**
     * 创建人 ID
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人 ID
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 版本号
     */
    private Integer version;
}

