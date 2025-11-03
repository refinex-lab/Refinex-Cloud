package cn.refinex.kb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 内容标签实体类
 * <p>
 * 对应数据库表：content_tag
 * 支持多用户独立标签体系，每个用户可以创建自己的标签，互不冲突
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "内容标签实体")
public class ContentTag {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "标签名称", example = "Java")
    private String tagName;

    @Schema(description = "标签颜色，十六进制值", example = "#1890ff")
    private String tagColor;

    @Schema(description = "标签类型：0系统标签,1用户自定义标签", example = "1")
    private Integer tagType;

    @Schema(description = "使用次数", example = "10")
    private Long usageCount;

    @Schema(description = "创建者ID，系统标签为0", example = "1001")
    private Long creatorId;

    @Schema(description = "创建人ID", example = "1001")
    private Long createBy;

    @Schema(description = "创建时间", example = "2025-10-27 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1001")
    private Long updateBy;

    @Schema(description = "更新时间", example = "2025-10-27 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除标记：0未删除,1已删除", example = "0")
    private Integer deleted;

    @Schema(description = "乐观锁版本号", example = "0")
    private Integer version;

    @Schema(description = "备注说明", example = "编程语言相关")
    private String remark;

    @Schema(description = "状态：1正常,0停用", example = "1")
    private Integer status;
}

