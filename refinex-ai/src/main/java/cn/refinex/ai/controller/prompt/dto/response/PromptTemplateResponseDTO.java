package cn.refinex.ai.controller.prompt.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提示词模板响应
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "提示词模板响应")
public class PromptTemplateResponseDTO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "模板编码", example = "WRITING_ASSISTANT")
    private String templateCode;

    @Schema(description = "版本号", example = "1")
    private Integer versionNumber;

    @Schema(description = "父模板ID", example = "10")
    private Long parentTemplateId;

    @Schema(description = "模板名称", example = "写作助手")
    private String templateName;

    @Schema(description = "模板内容")
    private String templateContent;

    @Schema(description = "模板类型", example = "USER")
    private String templateType;

    @Schema(description = "模板分类", example = "写作助手")
    private String templateCategory;

    @Schema(description = "适用模型数组")
    private String applicableModels;

    @Schema(description = "是否系统模板:0否,1是", example = "0")
    private Integer isSystem;

    @Schema(description = "是否公开:0否,1是", example = "1")
    private Integer isPublic;

    @Schema(description = "创建者ID", example = "1")
    private Long creatorId;

    @Schema(description = "使用次数", example = "1500")
    private Long usageCount;

    @Schema(description = "点赞数", example = "120")
    private Long likeCount;

    @Schema(description = "平均token消耗", example = "500")
    private Integer avgTokenUsage;

    @Schema(description = "平均成本(分)", example = "10")
    private Long avgCost;

    @Schema(description = "成功率", example = "95.50")
    private BigDecimal successRate;

    @Schema(description = "平均满意度评分", example = "4.50")
    private BigDecimal avgSatisfaction;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1")
    private Long updateBy;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "备注说明")
    private String remark;

    @Schema(description = "排序字段", example = "0")
    private Integer sort;

    @Schema(description = "状态:1正常,0停用", example = "1")
    private Integer status;

    @Schema(description = "扩展数据")
    private String extraData;

    @Schema(description = "版本号", example = "0")
    private Integer version;
}

