package cn.refinex.platform.domain.entity.email;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 邮件模板实体
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
public class EmailTemplate {

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 模板编码（如 VERIFY_CODE）
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 邮件主题
     */
    private String templateSubject;

    /**
     * 模板内容（HTML 格式）
     */
    private String templateContent;

    /**
     * 模板类型（HTML、TEXT）
     */
    private String templateType;

    /**
     * 变量列表（JSON 格式）
     */
    private String variables;

    /**
     * 模板分类（验证码、通知、营销）
     */
    private String templateCategory;

    /**
     * 是否系统模板（0否、1是）
     */
    private Integer isSystem;

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
     * 逻辑删除标记（0未删除、1已删除）
     */
    private Integer deleted;

    /**
     * 乐观锁版本号
     */
    private Integer version;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 状态（0正常、1停用）
     */
    private Integer status;
}

