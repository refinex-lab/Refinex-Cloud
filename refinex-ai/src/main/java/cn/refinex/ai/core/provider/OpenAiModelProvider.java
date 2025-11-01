package cn.refinex.ai.core.provider;

import cn.refinex.ai.entity.AiModelConfig;
import cn.refinex.ai.enums.ModelProviders;
import cn.refinex.ai.enums.ModelType;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.*;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * OpenAI 模型供应商实现
 * <p>
 * see:
 * <ul>
 *  <li><a href="https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html">OpenAI Chat</a></li>
 *  <li><a href="https://docs.spring.io/spring-ai/reference/api/embeddings/openai-embeddings.html">OpenAI Embeddings</a></li>
 *  <li><a href="https://docs.spring.io/spring-ai/reference/api/image/openai-image.html">OpenAI Image Generation</a></li>
 * </ul>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiModelProvider implements ModelProvider {

    @SuppressWarnings("unused")
    private final RestClient.Builder restClientBuilder;
    private final RetryTemplate retryTemplate;
    private final ObservationRegistry observationRegistry;
    private final ToolCallingManager toolCallingManager;

    /**
     * 创建聊天模型实例
     *
     * @param config 模型配置
     * @return 聊天模型实例
     */
    @Override
    public Object createChatModel(AiModelConfig config) {
        log.info("创建 OpenAI 聊天模型: modelCode={}, modelVersion={}", config.getModelCode(), config.getModelVersion());

        // 构建 OpenAI API 客户端
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(StringUtils.isNotBlank(config.getApiEndpoint()) ? config.getApiEndpoint() : "https://api.openai.com")
                .apiKey(StringUtils.isNotBlank(config.getApiKey()) ? config.getApiKey() : System.getenv("OPENAI_API_KEY"))
                .build();

        // 构建 OpenAI 聊天选项
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model(StringUtils.isNotBlank(config.getModelVersion()) ? config.getModelVersion() : OpenAiApi.ChatModel.GPT_3_5_TURBO.getValue())
                .temperature(config.getTemperature() != null ? config.getTemperature() : 0.4)
                .maxTokens(config.getMaxTokens() != null ? config.getMaxTokens() : 200)
                .build();

        // 创建 OpenAI 聊天模型实例
        return new OpenAiChatModel(openAiApi, openAiChatOptions, toolCallingManager, retryTemplate, observationRegistry);
    }

    /**
     * 创建图像模型实例
     *
     * @param config 模型配置
     * @return 图像模型实例
     */
    @Override
    public Object createImageModel(AiModelConfig config) {
        log.info("创建 OpenAI 图像模型: modelCode={}, modelVersion={}", config.getModelCode(), config.getModelVersion());

        // 构建 OpenAI API 客户端
        OpenAiImageApi openAiImageApi = OpenAiImageApi.builder()
                .baseUrl(StringUtils.isNotBlank(config.getApiEndpoint()) ? config.getApiEndpoint() : "https://api.openai.com")
                .apiKey(StringUtils.isNotBlank(config.getApiKey()) ? config.getApiKey() : System.getenv("OPENAI_API_KEY"))
                .build();

        // 构建 OpenAI 图像选项
        OpenAiImageOptions openAiImageOptions = OpenAiImageOptions.builder()
                .model(StringUtils.isNotBlank(config.getModelVersion()) ? config.getModelVersion() : OpenAiImageApi.ImageModel.DALL_E_3.getValue())
                .build();

        // 创建 OpenAI 图像模型实例
        return new OpenAiImageModel(openAiImageApi, openAiImageOptions, retryTemplate);
    }

    /**
     * 创建嵌入模型实例
     *
     * @param config 模型配置
     * @return 嵌入模型实例
     */
    @Override
    public Object createEmbeddingModel(AiModelConfig config) {
        log.info("创建 OpenAI 嵌入模型: modelCode={}, modelVersion={}", config.getModelCode(), config.getModelVersion());

        // 构建 OpenAI API 客户端
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(StringUtils.isNotBlank(config.getApiEndpoint()) ? config.getApiEndpoint() : "https://api.openai.com")
                .apiKey(StringUtils.isNotBlank(config.getApiKey()) ? config.getApiKey() : System.getenv("OPENAI_API_KEY"))
                .build();

        // 构建 OpenAI 嵌入选项
        OpenAiEmbeddingOptions openAiEmbeddingOptions = OpenAiEmbeddingOptions.builder()
                .model(StringUtils.isNotBlank(config.getModelVersion()) ? config.getModelVersion() : OpenAiApi.EmbeddingModel.TEXT_EMBEDDING_ADA_002.getValue())
                .build();

        // 创建 OpenAI 嵌入模型实例
        return new OpenAiEmbeddingModel(openAiApi, MetadataMode.EMBED, openAiEmbeddingOptions, retryTemplate);
    }

    /**
     * 获取供应商名称
     *
     * @return 供应商名称
     */
    @Override
    public String getProviderName() {
        return ModelProviders.OPENAI.getCode();
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
                || modelType == ModelType.IMAGE_GENERATION
                || modelType == ModelType.EMBEDDING;
    }
}

