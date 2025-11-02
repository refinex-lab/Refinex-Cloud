package cn.refinex.ai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 向量数据库配置实体类
 * <p>
 * 对应数据库表：ai_vector_database_config
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "向量数据库配置实体")
public class AiVectorDatabaseConfig {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "配置编码", example = "MILVUS_DEFAULT")
    private String configCode;

    @Schema(description = "配置名称", example = "默认Milvus配置")
    private String configName;

    @Schema(description = "向量库类型:MILVUS,PINECONE,WEAVIATE,CHROMA,ELASTICSEARCH", example = "MILVUS")
    private String dbType;

    @Schema(description = "连接地址", example = "http://localhost:19530")
    private String connectionUrl;

    @Schema(description = "API密钥,加密存储")
    private String apiKey;

    @Schema(description = "集合名称前缀", example = "refinex_")
    private String collectionPrefix;

    @Schema(description = "默认向量维度", example = "1536")
    private Integer defaultDimension;

    @Schema(description = "批量写入大小", example = "100")
    private Integer batchSize;

    @Schema(description = "是否默认配置:0否,1是", example = "1")
    private Integer isDefault;

    @Schema(description = "是否启用:0否,1是", example = "1")
    private Integer isEnabled;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1")
    private Long updateBy;

    @Schema(description = "更新时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除标记:0未删除,1已删除", example = "0")
    private Integer deleted;

    @Schema(description = "乐观锁版本号", example = "0")
    private Integer version;

    @Schema(description = "备注说明")
    private String remark;

    @Schema(description = "状态:1正常,0停用", example = "1")
    private Integer status;
}

