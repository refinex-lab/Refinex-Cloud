package cn.refinex.kb.controller.document.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文档响应DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文档响应")
public class ContentDocumentResponseDTO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "全局唯一标识", example = "DOC_550e8400e29b41d4a716446655440000")
    private String docGuid;

    @Schema(description = "所属空间ID", example = "100")
    private Long spaceId;

    @Schema(description = "所属目录ID", example = "50")
    private Long directoryId;

    @Schema(description = "文档标题", example = "深入理解Java多线程")
    private String docTitle;

    @Schema(description = "文档摘要", example = "本文详细介绍Java多线程的概念和实践")
    private String docSummary;

    @Schema(description = "内容类型", example = "MARKDOWN")
    private String contentType;

    @Schema(description = "内容类型名称", example = "Markdown")
    private String contentTypeName;

    @Schema(description = "封面图URL", example = "https://example.com/cover.jpg")
    private String coverImage;

    @Schema(description = "访问类型：0继承空间,1自定义私有,2自定义公开", example = "0")
    private Integer accessType;

    @Schema(description = "访问类型描述", example = "继承空间")
    private String accessTypeDesc;

    @Schema(description = "是否付费文档：0否,1是", example = "0")
    private Integer isPaid;

    @Schema(description = "付费金额，单位为分", example = "999")
    private Long paidAmount;

    @Schema(description = "文档状态：0草稿,1已发布,2已下架", example = "1")
    private Integer docStatus;

    @Schema(description = "文档状态描述", example = "已发布")
    private String docStatusDesc;

    @Schema(description = "发布时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

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

    @Schema(description = "当前版本号", example = "5")
    private Integer versionNumber;

    @Schema(description = "文档标签列表")
    private List<DocumentTagDTO> tags;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建人昵称", example = "张三")
    private String createByName;

    @Schema(description = "创建时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "排序字段", example = "0")
    private Integer sort;

    @Schema(description = "状态：1正常,0停用", example = "1")
    private Integer status;

    /**
     * 文档标签DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "文档标签")
    public static class DocumentTagDTO {

        @Schema(description = "标签ID", example = "1")
        private Long id;

        @Schema(description = "标签名称", example = "Java")
        private String tagName;

        @Schema(description = "标签颜色", example = "#1890ff")
        private String tagColor;

        @Schema(description = "标签类型：0系统标签,1用户自定义标签", example = "1")
        private Integer tagType;
    }
}

