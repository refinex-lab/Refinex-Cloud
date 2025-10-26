package cn.refinex.kb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论实体类
 * <p>
 * 对应数据库表：content_comment
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评论实体")
public class ContentComment {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "文档ID", example = "2000")
    private Long documentId;

    @Schema(description = "评论者ID", example = "1001")
    private Long userId;

    @Schema(description = "父评论ID，一级评论为0", example = "0")
    private Long parentId;

    @Schema(description = "根评论ID，一级评论为0", example = "0")
    private Long rootId;

    @Schema(description = "被回复者ID", example = "1002")
    private Long replyToUserId;

    @Schema(description = "评论内容", example = "写得很好，学到了很多！")
    private String commentContent;

    @Schema(description = "评论图片，JSON数组格式")
    private String commentImages;

    @Schema(description = "评论IP", example = "192.168.1.100")
    private String ipAddress;

    @Schema(description = "点赞数", example = "25")
    private Long likeCount;

    @Schema(description = "评论状态：0待审核,1已发布,2已删除", example = "1")
    private Integer commentStatus;

    @Schema(description = "审核时间", example = "2025-10-05 14:35:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;

    @Schema(description = "审核人ID", example = "1")
    private Long auditBy;

    @Schema(description = "创建人ID", example = "1001")
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
}

