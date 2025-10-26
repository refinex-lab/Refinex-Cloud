package cn.refinex.kb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 内容文档实体类
 * <p>
 * 对应数据库表：content_document
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "内容文档实体")
public class ContentDocument {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "全局唯一标识，UUID格式", example = "550e8400-e29b-41d4-a716-446655440000")
    private String docGuid;

    @Schema(description = "所属空间ID", example = "100")
    private Long spaceId;

    @Schema(description = "所属目录ID", example = "50")
    private Long directoryId;

    @Schema(description = "文档标题", example = "深入理解Java多线程")
    private String docTitle;

    @Schema(description = "文档摘要", example = "本文详细介绍Java多线程的概念和实践")
    private String docSummary;

    @Schema(description = "内容类型：MARKDOWN,RICHTEXT,VIDEO,MIXED", example = "MARKDOWN")
    private String contentType;

    @Schema(description = "内容正文")
    private String contentBody;

    @Schema(description = "封面图URL", example = "https://example.com/cover.jpg")
    private String coverImage;

    @Schema(description = "关联的文件ID", example = "200")
    private Long fileId;

    @Schema(description = "访问类型：0继承空间,1自定义私有,2自定义公开", example = "0")
    private Integer accessType;

    @Schema(description = "是否付费文档：0否,1是", example = "0")
    private Integer isPaid;

    @Schema(description = "付费金额，单位为分", example = "999")
    private Long paidAmount;

    @Schema(description = "文档状态：0草稿,1已发布,2已下架", example = "1")
    private Integer docStatus;

    @Schema(description = "发布时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    @Schema(description = "定时发布时间", example = "2025-10-06 09:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime schedulePublishTime;

    @Schema(description = "字数统计", example = "3500")
    private Integer wordCount;

    @Schema(description = "预计阅读时长，单位分钟", example = "15")
    private Integer readDuration;

    @Schema(description = "浏览次数", example = "5280")
    private Long viewCount;

    @Schema(description = "点赞数", example = "125")
    private Long likeCount;

    @Schema(description = "收藏数", example = "88")
    private Long collectCount;

    @Schema(description = "评论数", example = "42")
    private Long commentCount;

    @Schema(description = "分享次数", example = "33")
    private Long shareCount;

    @Schema(description = "SEO关键词", example = "Java,多线程,并发编程")
    private String seoKeywords;

    @Schema(description = "SEO描述", example = "深入理解Java多线程的核心概念和实践技巧")
    private String seoDescription;

    @Schema(description = "当前版本号", example = "5")
    private Integer versionNumber;

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

    @Schema(description = "备注说明", example = "技术文章")
    private String remark;

    @Schema(description = "排序字段", example = "0")
    private Integer sort;

    @Schema(description = "状态：0正常,1停用", example = "0")
    private Integer status;

    @Schema(description = "扩展数据（JSON格式）")
    private String extraData;
}

