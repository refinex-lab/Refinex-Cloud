package cn.refinex.ai.core.provider;

import cn.refinex.ai.entity.AiModelConfig;
import cn.refinex.ai.enums.ModelProviders;
import cn.refinex.ai.enums.ModelType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;

/**
 * Anthropic 模型供应商实现
 * <p>
 * see:
 * <ul>
 *  <li><a href="https://docs.spring.io/spring-ai/reference/api/chat/anthropic-chat.html">Anthropic Chat</a></li>
 * </ul>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
public class AnthropicModelProvider implements ModelProvider {

    /**
     * 创建聊天模型实例
     *
     * @param config 模型配置
     * @return 聊天模型实例
     */
    @Override
    public Object createChatModel(AiModelConfig config) {
        log.info("创建 Anthropic 聊天模型实例，配置：{}", config);

        // 构建 AnthropicApi API 客户端
        AnthropicApi anthropicApi = AnthropicApi.builder()
                .baseUrl(StringUtils.isNotBlank(config.getApiEndpoint()) ? config.getApiEndpoint() : AnthropicApi.DEFAULT_BASE_URL)
                .apiKey(StringUtils.isNotBlank(config.getApiKey()) ? config.getApiKey() : System.getenv("ANTHROPIC_API_KEY"))
                .build();

        // 构建 AnthropicChatOptions 聊天选项
        AnthropicChatOptions anthropicChatOptions = AnthropicChatOptions.builder()
                .model(StringUtils.isNotBlank(config.getModelVersion()) ? config.getModelVersion() : AnthropicApi.ChatModel.CLAUDE_3_7_SONNET.getValue())
                .temperature(config.getTemperature() != null ? config.getTemperature() : AnthropicChatModel.DEFAULT_TEMPERATURE)
                .maxTokens(config.getMaxTokens() != null ? config.getMaxTokens() : AnthropicChatModel.DEFAULT_MAX_TOKENS)
                .build();

        // 构建 AnthropicChatModel 聊天模型
        return AnthropicChatModel.builder()
                .anthropicApi(anthropicApi)
                .defaultOptions(anthropicChatOptions)
                .build();
    }

    /**
     * 创建图像模型实例
     *
     * @param config 模型配置
     * @return 图像模型实例
     */
    @Override
    public Object createImageModel(AiModelConfig config) {
        log.warn("Anthropic 暂不支持图像生成模型");
        throw new UnsupportedOperationException("Anthropic 暂不支持图像生成模型");
    }

    /**
     * 创建嵌入模型实例
     *
     * @param config 模型配置
     * @return 嵌入模型实例
     */
    @Override
    public Object createEmbeddingModel(AiModelConfig config) {
        log.warn("Anthropic 暂不支持嵌入模型");
        throw new UnsupportedOperationException("Anthropic 暂不支持嵌入模型");
    }

    /**
     * 获取供应商名称
     *
     * @return 供应商名称
     */
    @Override
    public String getProviderName() {
        return ModelProviders.ANTHROPIC.getCode();
    }

    /**
     * 检查是否支持指定的模型类型
     *
     * @param modelType 模型类型
     * @return true-支持，false-不支持
     */
    @Override
    public boolean supports(ModelType modelType) {
        return modelType == ModelType.TEXT_GENERATION;
    }
}
