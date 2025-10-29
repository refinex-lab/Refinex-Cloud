package cn.refinex.kb.controller.directory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 统一树节点响应DTO（目录+文档）
 * 用于前端树形展示，支持目录和文档两种节点类型
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "统一树节点（目录+文档）")
public class ContentTreeNodeResponseDTO {

    @Schema(description = "节点类型：directory=目录, document=文档", example = "directory")
    private String nodeType;

    @Schema(description = "节点唯一标识key（dir_1 或 doc_DOC_xxx）", example = "dir_1")
    private String key;

    @Schema(description = "节点显示标题", example = "Java基础")
    private String title;

    // ==================== 目录字段（nodeType=directory时使用） ====================

    @Schema(description = "目录ID", example = "1")
    private Long directoryId;

    @Schema(description = "目录名称", example = "Java基础")
    private String directoryName;

    @Schema(description = "目录路径", example = "/Java技术/Java基础")
    private String directoryPath;

    // ==================== 文档字段（nodeType=document时使用） ====================

    @Schema(description = "文档ID", example = "100")
    private Long documentId;

    @Schema(description = "文档全局唯一标识", example = "DOC_550e8400e29b41d4a716446655440000")
    private String docGuid;

    @Schema(description = "文档标题", example = "深入理解Java多线程")
    private String docTitle;

    @Schema(description = "文档状态：0=草稿,1=已发布,2=已下架", example = "1")
    private Integer docStatus;

    @Schema(description = "文档状态描述", example = "已发布")
    private String docStatusDesc;

    @Schema(description = "访问类型：0=继承空间,1=自定义私有,2=自定义公开", example = "0")
    private Integer accessType;

    @Schema(description = "文档摘要", example = "这是一篇关于Java多线程的技术文章...")
    private String docSummary;

    @Schema(description = "封面图片URL", example = "https://example.com/cover.jpg")
    private String coverImage;

    @Schema(description = "字数统计", example = "3500")
    private Integer wordCount;

    @Schema(description = "预计阅读时长(分钟)", example = "15")
    private Integer readDuration;

    @Schema(description = "浏览次数", example = "1280")
    private Long viewCount;

    @Schema(description = "点赞次数", example = "45")
    private Long likeCount;

    @Schema(description = "收藏次数", example = "32")
    private Long collectCount;

    @Schema(description = "评论次数", example = "18")
    private Long commentCount;

    @Schema(description = "创建人ID", example = "100")
    private Long createBy;

    @Schema(description = "创建人昵称", example = "张三")
    private String createByName;

    @Schema(description = "创建时间", example = "2025-01-15 10:30:00")
    private String createTime;

    @Schema(description = "更新时间", example = "2025-01-20 15:45:00")
    private String updateTime;

    // ==================== 共用字段 ====================

    @Schema(description = "父节点ID（0表示根节点）", example = "0")
    private Long parentId;

    @Schema(description = "节点深度", example = "2")
    private Integer depthLevel;

    @Schema(description = "排序字段", example = "0")
    private Integer sort;

    @Schema(description = "是否为叶子节点", example = "false")
    private Boolean isLeaf;

    @Schema(description = "子节点列表")
    private List<ContentTreeNodeResponseDTO> children;
}

