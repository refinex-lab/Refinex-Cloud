package cn.refinex.ai.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 向量数据库类型枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum VectorDatabaseType {

    /**
     * Milvus - 开源向量数据库
     */
    MILVUS("MILVUS", "Milvus", 2379),

    /**
     * Pinecone - 云原生向量数据库
     */
    PINECONE("PINECONE", "Pinecone", 443),

    /**
     * Weaviate - 开源向量搜索引擎
     */
    WEAVIATE("WEAVIATE", "Weaviate", 8080),

    /**
     * Chroma - 轻量级向量数据库
     */
    CHROMA("CHROMA", "Chroma", 8000),

    /**
     * Elasticsearch - 支持向量搜索的搜索引擎
     */
    ELASTICSEARCH("ELASTICSEARCH", "Elasticsearch", 9200),

    /**
     * Qdrant - Rust实现的向量数据库
     */
    QDRANT("QDRANT", "Qdrant", 6333),

    /**
     * PgVector - PostgreSQL向量扩展
     */
    PGVECTOR("PGVECTOR", "PgVector", 5432),

    /**
     * Redis - 支持向量搜索的Redis
     */
    REDIS("REDIS", "Redis", 6379),

    /**
     * 本地存储（文件系统）
     */
    LOCAL("LOCAL", "本地存储", 0);

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 默认端口
     */
    private final Integer defaultPort;

    /**
     * 根据代码获取枚举
     *
     * @param code 类型代码
     * @return 枚举
     */
    public static VectorDatabaseType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (VectorDatabaseType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为云服务
     *
     * @return 是否为云服务
     */
    public boolean isCloudService() {
        return this == PINECONE;
    }

    /**
     * 判断是否需要本地部署
     *
     * @return 是否需要本地部署
     */
    public boolean requiresLocalDeployment() {
        return !isCloudService() && this != LOCAL;
    }
}

