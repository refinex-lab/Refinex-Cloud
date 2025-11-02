package cn.refinex.kb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 内容目录实体类
 * <p>
 * 对应数据库表：content_directory
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "内容目录实体")
public class ContentDirectory {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "所属空间ID", example = "100")
    private Long spaceId;

    @Schema(description = "父目录ID，根目录为0", example = "0")
    private Long parentId;

    @Schema(description = "目录名称", example = "Java基础")
    private String directoryName;

    @Schema(description = "完整路径", example = "/Java技术/Java基础")
    private String directoryPath;

    @Schema(description = "目录深度，从1开始", example = "2")
    private Integer depthLevel;

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

    @Schema(description = "备注说明", example = "Java基础知识目录")
    private String remark;

    @Schema(description = "排序字段", example = "0")
    private Integer sort;

    @Schema(description = "状态：1正常,0停用", example = "1")
    private Integer status;

    @Schema(description = "扩展数据（JSON格式）")
    private String extraData;
}

