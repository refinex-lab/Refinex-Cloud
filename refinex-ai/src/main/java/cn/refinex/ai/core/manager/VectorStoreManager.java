package cn.refinex.ai.core.manager;

import cn.refinex.ai.core.factory.VectorStoreFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 向量存储管理器
 * <p>
 * 管理向量存储的生命周期，提供统一的向量存储访问接口
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VectorStoreManager {

    private final VectorStoreFactory vectorStoreFactory;

    /**
     * 获取或创建向量存储实例
     * <p>
     * 根据 EmbeddingModel 和元数据字段动态创建 VectorStore 实例
     * </p>
     *
     * @param modelCode      模型编码（用于日志记录）
     * @param embeddingModel 嵌入模型
     * @param metadataFields 元数据字段映射（字段名 -> 字段类型），可为 null
     * @return 向量存储实例
     */
    public VectorStore getOrCreateVectorStore(String modelCode, EmbeddingModel embeddingModel, Map<String, Class<?>> metadataFields) {
        log.debug("创建模型 {} 的向量存储实例", modelCode);

        // 如果 metadataFields 为 null，使用空 Map
        Map<String, Class<?>> fields = metadataFields != null ? metadataFields : new HashMap<>();

        return vectorStoreFactory.createVectorStore(embeddingModel, fields);
    }

    /**
     * 获取或创建向量存储实例（无元数据字段）
     *
     * @param modelCode      模型编码
     * @param embeddingModel 嵌入模型
     * @return 向量存储实例
     */
    public VectorStore getOrCreateVectorStore(String modelCode, EmbeddingModel embeddingModel) {
        return getOrCreateVectorStore(modelCode, embeddingModel, null);
    }

}

