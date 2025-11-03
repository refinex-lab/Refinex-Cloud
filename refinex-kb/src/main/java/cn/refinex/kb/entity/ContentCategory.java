package cn.refinex.kb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 内容分类实体类
 * <p>
 * 对应数据库表：content_category
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "内容分类实体")
public class ContentCategory {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "分类编码", example = "TECH_JAVA")
    private String categoryCode;

    @Schema(description = "分类名称", example = "Java技术")
    private String categoryName;

    @Schema(description = "父分类ID，一级分类为0", example = "0")
    private Long parentId;

    @Schema(description = "分类图标", example = "icon-java")
    private String categoryIcon;

    @Schema(description = "分类描述", example = "Java编程语言相关的技术文章")
    private String categoryDesc;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1")
    private Long updateBy;

    @Schema(description = "更新时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除标记：0未删除,1已删除", example = "0")
    private Integer deleted;

    @Schema(description = "乐观锁版本号", example = "0")
    private Integer version;

    @Schema(description = "备注说明", example = "技术分类")
    private String remark;

    @Schema(description = "排序字段", example = "0")
    private Integer sort;

    @Schema(description = "状态：1正常,0停用", example = "1")
    private Integer status;

    @Schema(description = "扩展数据（JSON格式）")
    private String extraData;
}

