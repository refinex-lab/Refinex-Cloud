package cn.refinex.ai.core.client;

import cn.refinex.ai.enums.ModelType;
import cn.refinex.common.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;

import java.util.List;

/**
 * 嵌入模型客户端实现
 * <p>
 * 封装 Spring AI 的 EmbeddingModel，提供统一的文本嵌入模型调用接口
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
public record EmbeddingModelClient(
        // 嵌入模型实例
        EmbeddingModel embeddingModel,
        // 供应商名称
        String providerName,
        // 模型编码
        String modelCode
) implements AiModelClient<EmbeddingRequest, EmbeddingResponse> {

    /**
     * 调用 AI 模型
     *
     * @param request 请求对象
     * @return 响应对象
     */
    @Override
    public EmbeddingResponse invoke(EmbeddingRequest request) {
        try {
            log.debug("调用嵌入模型: provider={}, model={}", providerName, modelCode);
            return embeddingModel.call(request);
        } catch (Exception e) {
            log.error("嵌入模型调用失败: provider={}, model={}", providerName, modelCode, e);
            throw new SystemException("嵌入模型调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取模型类型
     *
     * @return 模型类型枚举
     */
    @Override
    public ModelType getModelType() {
        return ModelType.EMBEDDING;
    }

    /**
     * 检查模型健康状态
     *
     * @return true-健康，false-异常
     */
    @Override
    public boolean healthCheck() {
        try {
            // 嵌入模型健康检查：发送一个测试文本
            EmbeddingRequest testRequest = new EmbeddingRequest(List.of("hello"), null);
            embeddingModel.call(testRequest);
            log.debug("嵌入模型健康检查通过: provider={}, model={}", providerName, modelCode);
            return true;
        } catch (Exception e) {
            log.warn("嵌入模型健康检查失败: provider={}, model={}", providerName, modelCode, e);
            return false;
        }
    }

    /**
     * 获取底层 EmbeddingModel 实例
     *
     * @return EmbeddingModel 实例
     */
    @Override
    public EmbeddingModel embeddingModel() {
        return embeddingModel;
    }
}

