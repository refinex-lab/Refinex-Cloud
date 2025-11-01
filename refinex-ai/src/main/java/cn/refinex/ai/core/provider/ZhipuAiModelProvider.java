package cn.refinex.ai.core.provider;

import cn.refinex.ai.entity.AiModelConfig;
import cn.refinex.ai.enums.ModelProviders;
import cn.refinex.ai.enums.ModelType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.zhipuai.*;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.ai.zhipuai.api.ZhiPuAiImageApi;
import org.springframework.ai.zhipuai.api.ZhiPuApiConstants;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Objects;

/**
 * 智谱 AI（GLM）模型供应商实现
 * <p>
 * see:
 * <ul>
 *  <li><a href="https://docs.spring.io/spring-ai/reference/api/chat/zhipuai-chat.html">ZhiPu AI Chat</a></li>
 *  <li><a href="https://docs.spring.io/spring-ai/reference/api/embeddings/zhipuai-embeddings.html">ZhiPuAI Embeddings</a></li>
 *  <li><a href="https://docs.spring.io/spring-ai/reference/api/image/zhipuai-image.html">ZhiPuAI Image Generation</a></li>
 * </ul>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ZhipuAiModelProvider implements ModelProvider {

    @SuppressWarnings("unused")
    private final RestClient.Builder restClientBuilder;
    private final RetryTemplate retryTemplate;

    /**
     * 创建聊天模型实例
     *
     * @param config 模型配置
     * @return 聊天模型实例
     */
    @Override
    public Object createChatModel(AiModelConfig config) {
        log.info("创建智谱 AI 聊天模型: modelCode={}, modelVersion={}", config.getModelCode(), config.getModelVersion());

        // 构建智谱 AI API 客户端
        ZhiPuAiApi zhiPuAiApi = new ZhiPuAiApi(
                StringUtils.isNotBlank(config.getApiEndpoint()) ? config.getApiEndpoint() : ZhiPuApiConstants.DEFAULT_BASE_URL,
                StringUtils.isNotBlank(config.getApiKey()) ? config.getApiKey() : System.getenv("ZHIPUAI_API_KEY")
        );

        // 构建智谱 AI 聊天模型选项
        ZhiPuAiChatOptions zhiPuAiChatOptions = ZhiPuAiChatOptions.builder()
                .model(StringUtils.isNotBlank(config.getModelVersion()) ? config.getModelVersion() : ZhiPuAiApi.ChatModel.GLM_4.getValue())
                .temperature(Objects.nonNull(config.getTemperature()) ? config.getTemperature() : 0.4)
                .maxTokens(Objects.nonNull(config.getMaxTokens()) ? config.getMaxTokens() : 200)
                .build();

        // 创建 ChatModel
        return new ZhiPuAiChatModel(zhiPuAiApi, zhiPuAiChatOptions);
    }

    /**
     * 创建图像模型实例
     *
     * @param config 模型配置
     * @return 图像模型实例
     */
    @Override
    public Object createImageModel(AiModelConfig config) {
        log.info("创建智谱 AI 图像模型: modelCode={}, modelVersion={}", config.getModelCode(), config.getModelVersion());

        // 构建智谱 AI 图像 API 客户端
        ZhiPuAiImageApi zhiPuAiImageApi = new ZhiPuAiImageApi(
                StringUtils.isNotBlank(config.getApiEndpoint()) ? config.getApiEndpoint() : ZhiPuApiConstants.DEFAULT_BASE_URL,
                StringUtils.isNotBlank(config.getApiKey()) ? config.getApiKey() : System.getenv("ZHIPUAI_API_KEY"),
                restClientBuilder
        );

        // 构建智谱 AI 图像模型选项
        ZhiPuAiImageOptions zhiPuAiImageOptions = ZhiPuAiImageOptions.builder()
                .model(StringUtils.isNotBlank(config.getModelVersion()) ? config.getModelVersion() : ZhiPuAiImageApi.DEFAULT_IMAGE_MODEL)
                .build();

        return new ZhiPuAiImageModel(zhiPuAiImageApi, zhiPuAiImageOptions, retryTemplate);
    }

    /**
     * 创建嵌入模型实例
     *
     * @param config 模型配置
     * @return 嵌入模型实例
     */
    @Override
    public Object createEmbeddingModel(AiModelConfig config) {
        log.info("创建智谱 AI 嵌入模型: modelCode={}, modelVersion={}", config.getModelCode(), config.getModelVersion());

        // 构建智谱 AI API 客户端
        ZhiPuAiApi zhiPuAiApi = new ZhiPuAiApi(
                StringUtils.isNotBlank(config.getApiEndpoint()) ? config.getApiEndpoint() : ZhiPuApiConstants.DEFAULT_BASE_URL,
                StringUtils.isNotBlank(config.getApiKey()) ? config.getApiKey() : System.getenv("ZHIPUAI_API_KEY")
        );

        // 构建智谱 AI 嵌入模型选项
        ZhiPuAiEmbeddingOptions zhiPuAiEmbeddingOptions = ZhiPuAiEmbeddingOptions.builder()
                .model(StringUtils.isNotBlank(config.getModelVersion()) ? config.getModelVersion() : ZhiPuAiApi.DEFAULT_EMBEDDING_MODEL)
                .build();

        // 创建 EmbeddingModel
        // MetadataMode.EMBED 表示要包含文档的元数据模式，有如下四个选项：
        // ALL: 包含所有元数据
        // EMBED: 仅包含嵌入向量（这里和默认构造保持一致只用这个选项）
        // INFERENCE: 仅包含推理结果
        // NONE: 不包含任何元数据
        return new ZhiPuAiEmbeddingModel(zhiPuAiApi, MetadataMode.EMBED, zhiPuAiEmbeddingOptions);
    }

    /**
     * 获取供应商名称
     *
     * @return 供应商名称
     */
    @Override
    public String getProviderName() {
        return ModelProviders.ZHIPUAI.getCode();
    }

    /**
     * 检查是否支持指定的模型类型
     *
     * @param modelType 模型类型
     * @return true-支持，false-不支持
     */
    @Override
    public boolean supports(ModelType modelType) {
        return modelType == ModelType.TEXT_GENERATION
                || modelType == ModelType.EMBEDDING
                || modelType == ModelType.IMAGE_GENERATION;
    }
}

