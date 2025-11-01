package cn.refinex.ai.service;

import org.springframework.ai.document.Document;

import java.util.List;

/**
 * 向量存储服务接口
 * <p>
 * 提供向量存储的业务操作接口
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface VectorStoreService {

    /**
     * 添加文档到向量存储
     *
     * @param modelCode 嵌入模型编码
     * @param documents 文档列表
     */
    void addDocuments(String modelCode, List<Document> documents);

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
    List<Document> similaritySearch(String modelCode, String query, int topK, Double similarityThreshold, String filterExpression);

    /**
     * 删除文档
     *
     * @param modelCode  模型编码
     * @param documentId 文档ID
     */
    void deleteDocument(String modelCode, String documentId);

    /**
     * 批量删除文档
     *
     * @param modelCode   模型编码
     * @param documentIds 文档ID列表
     */
    void deleteDocuments(String modelCode, List<String> documentIds);

    /**
     * 根据过滤表达式删除文档
     *
     * @param modelCode        模型编码
     * @param filterExpression 过滤表达式
     */
    void deleteByFilter(String modelCode, String filterExpression);

    /**
     * 持久化向量存储（仅适用于SimpleVectorStore）
     *
     * @param modelCode 模型编码
     */
    void persistVectorStore(String modelCode);

}
