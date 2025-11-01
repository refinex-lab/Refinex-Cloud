package cn.refinex.ai.service.impl;

import cn.refinex.ai.properties.AiProperties;
import cn.refinex.ai.core.factory.AiModelFactory;
import cn.refinex.ai.core.manager.VectorStoreManager;
import cn.refinex.ai.service.VectorStoreService;
import cn.refinex.common.exception.SystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 向量存储服务实现
 * <p>
 * 提供向量存储的业务操作实现
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "refinex.ai.vector-store", name = "enabled", havingValue = "true")
public class VectorStoreServiceImpl implements VectorStoreService {

    private final VectorStoreManager vectorStoreManager;
    private final AiModelFactory aiModelFactory;
    private final AiProperties aiProperties;

    /**
     * 添加文档到向量存储
     *
     * @param modelCode 嵌入模型编码
     * @param documents 文档列表
     */
    @Override
    public void addDocuments(String modelCode, List<Document> documents) {
        try {
            EmbeddingModel embeddingModel = aiModelFactory.getEmbeddingModelClient(modelCode).embeddingModel();

            // 提取元数据字段类型
            Map<String, Class<?>> metadataFields = extractMetadataFields(documents);

            VectorStore vectorStore = vectorStoreManager.getOrCreateVectorStore(modelCode, embeddingModel, metadataFields);

            // 使用 VectorStore 的 add 方法，它会自动处理批处理和嵌入计算
            vectorStore.add(documents);

            log.info("成功添加 {} 个文档到向量存储，模型: {}", documents.size(), modelCode);
        } catch (Exception e) {
            log.error("添加文档到向量存储失败: {}", e.getMessage(), e);
            throw new SystemException("添加文档到向量存储失败", e);
        }
    }

    /**
     * 相似性搜索
     *
     * @param modelCode           嵌入模型编码
     * @param query               查询文本
     * @param topK                返回结果数量
     * @param similarityThreshold 相似度阈值(0-1)，null表示不限制
     * @param filterExpression    过滤表达式，null表示不过滤
     * @return 相似文档列表
     */
    @Override
    public List<Document> similaritySearch(String modelCode, String query, int topK, Double similarityThreshold, String filterExpression) {
        try {
            EmbeddingModel embeddingModel = aiModelFactory.getEmbeddingModelClient(modelCode).embeddingModel();
            VectorStore vectorStore = vectorStoreManager.getOrCreateVectorStore(modelCode, embeddingModel);

            // 构建搜索请求
            SearchRequest.Builder builder = SearchRequest.builder()
                    .query(query)
                    .topK(topK);

            // 设置相似度阈值
            if (similarityThreshold != null) {
                builder.similarityThreshold(similarityThreshold);
            } else {
                // 默认接受所有结果
                builder.similarityThresholdAll();
            }

            // 设置过滤表达式
            if (filterExpression != null && !filterExpression.isBlank()) {
                builder.filterExpression(filterExpression);
            }

            SearchRequest request = builder.build();

            // 执行相似性搜索
            List<Document> results = vectorStore.similaritySearch(request);

            log.info("相似性搜索完成，模型: {}，查询: {}，返回 {} 个结果", modelCode, query, results.size());
            return results;
        } catch (Exception e) {
            log.error("相似性搜索失败: {}", e.getMessage(), e);
            throw new SystemException("相似性搜索失败", e);
        }
    }

    /**
     * 删除单个文档
     *
     * @param modelCode  嵌入模型编码
     * @param documentId 文档ID
     */
    @Override
    public void deleteDocument(String modelCode, String documentId) {
        try {
            EmbeddingModel embeddingModel = aiModelFactory.getEmbeddingModelClient(modelCode).embeddingModel();
            VectorStore vectorStore = vectorStoreManager.getOrCreateVectorStore(modelCode, embeddingModel);

            // 使用 VectorStore 的 delete 方法删除单个文档
            vectorStore.delete(List.of(documentId));

            log.info("成功删除文档，模型: {}，文档ID: {}", modelCode, documentId);
        } catch (Exception e) {
            log.error("删除文档失败: {}", e.getMessage(), e);
            throw new SystemException("删除文档失败", e);
        }
    }

    /**
     * 批量删除文档
     *
     * @param modelCode   嵌入模型编码
     * @param documentIds 文档ID列表
     */
    @Override
    public void deleteDocuments(String modelCode, List<String> documentIds) {
        try {
            EmbeddingModel embeddingModel = aiModelFactory.getEmbeddingModelClient(modelCode).embeddingModel();
            VectorStore vectorStore = vectorStoreManager.getOrCreateVectorStore(modelCode, embeddingModel);

            // 使用 VectorStore 的 delete 方法批量删除文档
            vectorStore.delete(documentIds);

            log.info("成功批量删除 {} 个文档，模型: {}", documentIds.size(), modelCode);
        } catch (Exception e) {
            log.error("批量删除文档失败: {}", e.getMessage(), e);
            throw new SystemException("批量删除文档失败", e);
        }
    }

    /**
     * 根据过滤表达式删除文档
     *
     * @param modelCode        嵌入模型编码
     * @param filterExpression 过滤表达式，null表示不过滤
     */
    @Override
    public void deleteByFilter(String modelCode, String filterExpression) {
        try {
            EmbeddingModel embeddingModel = aiModelFactory.getEmbeddingModelClient(modelCode).embeddingModel();
            VectorStore vectorStore = vectorStoreManager.getOrCreateVectorStore(modelCode, embeddingModel);

            // 使用 VectorStore 的 delete 方法根据过滤表达式删除文档
            vectorStore.delete(filterExpression);

            log.info("成功根据过滤表达式删除文档，模型: {}，表达式: {}", modelCode, filterExpression);
        } catch (Exception e) {
            log.error("根据过滤表达式删除文档失败: {}", e.getMessage(), e);
            throw new SystemException("根据过滤表达式删除文档失败", e);
        }
    }

    /**
     * 持久化向量存储（仅适用于SimpleVectorStore）
     *
     * @param modelCode 模型编码
     */
    @Override
    public void persistVectorStore(String modelCode) {
        try {
            EmbeddingModel embeddingModel = aiModelFactory.getEmbeddingModelClient(modelCode).embeddingModel();
            VectorStore vectorStore = vectorStoreManager.getOrCreateVectorStore(modelCode, embeddingModel);

            // 仅对 SimpleVectorStore 执行持久化
            if (vectorStore instanceof SimpleVectorStore simpleVectorStore) {
                String storePath = aiProperties.getVectorStore().getSimple().getStorePath();
                File storeFile = new File(storePath, "vector-store.json");
                simpleVectorStore.save(storeFile);
                log.info("成功持久化向量存储，模型: {}，文件: {}", modelCode, storeFile.getAbsolutePath());
            } else {
                log.debug("向量存储类型不是 SimpleVectorStore，跳过持久化，模型: {}", modelCode);
            }
        } catch (Exception e) {
            log.error("持久化向量存储失败: {}", e.getMessage(), e);
            throw new SystemException("持久化向量存储失败", e);
        }
    }

    /**
     * 从文档列表中提取元数据字段类型
     *
     * @param documents 文档列表
     * @return 元数据字段类型映射
     */
    private Map<String, Class<?>> extractMetadataFields(List<Document> documents) {
        Map<String, Class<?>> metadataFields = new HashMap<>();

        for (Document document : documents) {
            document.getMetadata().forEach((key, value) -> {
                if (value != null && !metadataFields.containsKey(key)) {
                    metadataFields.put(key, value.getClass());
                }
            });
        }

        log.debug("提取到 {} 个元数据字段: {}", metadataFields.size(), metadataFields.keySet());
        return metadataFields;
    }
}

