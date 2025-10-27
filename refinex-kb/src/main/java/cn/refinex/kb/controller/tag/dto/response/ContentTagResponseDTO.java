package cn.refinex.kb.controller.tag.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容标签响应
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "内容标签响应")
public class ContentTagResponseDTO {

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

    @Schema(description = "创建者ID", example = "1001")
    private Long creatorId;

    @Schema(description = "创建时间", example = "2025-10-27 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-10-27 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "备注说明", example = "编程语言相关")
    private String remark;

    @Schema(description = "状态：0正常,1停用", example = "0")
    private Integer status;
}

