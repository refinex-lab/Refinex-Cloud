package cn.refinex.ai.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 向量存储类型枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum VectorStoreType {

    /**
     * 内存向量存储（基于文件持久化）
     */
    SIMPLE("SIMPLE", "内存向量存储"),

    /**
     * Redis 向量存储
     */
    REDIS("REDIS", "Redis向量存储"),

    /**
     * Qdrant 向量存储
     */
    QDRANT("QDRANT", "Qdrant向量存储"),

    /**
     * Milvus 向量存储
     */
    MILVUS("MILVUS", "Milvus向量存储");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据代码获取枚举
     *
     * @param code 类型代码
     * @return 枚举
     */
    public static VectorStoreType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (VectorStoreType type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}

