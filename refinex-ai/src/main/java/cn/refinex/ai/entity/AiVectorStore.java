package cn.refinex.ai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 向量存储索引实体类
 * <p>
 * 对应数据库表：ai_vector_store
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "向量存储索引实体")
public class AiVectorStore {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "文档ID", example = "100")
    private Long documentId;

    @Schema(description = "分块索引", example = "0")
    private Integer chunkIndex;

    @Schema(description = "分块大小(字符数)", example = "500")
    private Integer chunkSize;

    @Schema(description = "分块重叠大小", example = "50")
    private Integer chunkOverlap;

    @Schema(description = "分块内容")
    private String chunkContent;

    @Schema(description = "分块内容哈希,用于去重", example = "a1b2c3d4e5f6")
    private String chunkHash;

    @Schema(description = "分块token数", example = "150")
    private Integer chunkTokens;

    @Schema(description = "向量数据库中的向量ID", example = "vec_550e8400e29b41d4")
    private String vectorId;

    @Schema(description = "向量库集合名称", example = "kb_collection_001")
    private String collectionName;

    @Schema(description = "嵌入模型", example = "text-embedding-v1")
    private String embeddingModel;

    @Schema(description = "向量维度,如1536", example = "1536")
    private Integer vectorDimension;

    @Schema(description = "向量数据(小规模场景)或存储路径")
    private String vectorData;

    @Schema(description = "相似度阈值", example = "0.7500")
    private BigDecimal similarityThreshold;

    @Schema(description = "同步状态:0待同步,1已同步,2同步失败", example = "1")
    private Integer syncStatus;

    @Schema(description = "同步到向量库的时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime syncTime;

    @Schema(description = "同步错误信息")
    private String syncError;

    @Schema(description = "元数据")
    private String metadata;

    @Schema(description = "创建时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

