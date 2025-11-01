package cn.refinex.ai.core.provider;

import cn.refinex.ai.entity.AiModelConfig;
import cn.refinex.ai.enums.ModelProviders;
import cn.refinex.ai.enums.ModelType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Objects;

/**
 * DeepSeek 模型供应商实现
 * <p>
 * see:
 * <ul>
 *  <li><a href="https://docs.spring.io/spring-ai/reference/api/chat/deepseek-chat.html">DeepSeek Chat</a></li>
 * </ul>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeepSeekModelProvider implements ModelProvider {

    @SuppressWarnings("unused")
    private final RestClient.Builder restClientBuilder;

    /**
     * 创建聊天模型实例
     *
     * @param config 模型配置
     * @return 聊天模型实例
     */
    @Override
    public Object createChatModel(AiModelConfig config) {
        log.info("创建 DeepSeek 聊天模型: modelCode={}, modelVersion={}", config.getModelCode(), config.getModelVersion());

        // 构建 DeepSeek API 客户端
        DeepSeekApi deepSeekApi = DeepSeekApi.builder()
                .baseUrl(StringUtils.isNotBlank(config.getApiEndpoint()) ? config.getApiEndpoint() : "https://api.deepseek.com")
                .apiKey(StringUtils.isNotBlank(config.getApiKey()) ? config.getApiKey() : System.getenv("DEEPSEEK_API_KEY"))
                .build();

        // 构建 DeepSeek 聊天模型选项
        DeepSeekChatOptions deepSeekChatOptions = DeepSeekChatOptions.builder()
                .model(StringUtils.isNotBlank(config.getModelVersion()) ? config.getModelVersion() : DeepSeekApi.ChatModel.DEEPSEEK_CHAT.getValue())
                .temperature(Objects.nonNull(config.getTemperature()) ? config.getTemperature() : 0.4)
                .maxTokens(Objects.nonNull(config.getMaxTokens()) ? config.getMaxTokens() : 200)
                .build();

        // 构建 DeepSeek 聊天模型
        return DeepSeekChatModel.builder()
                .deepSeekApi(deepSeekApi)
                .defaultOptions(deepSeekChatOptions)
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
        log.warn("DeepSeek 暂不支持图像生成模型");
        throw new UnsupportedOperationException("DeepSeek 暂不支持图像生成模型");
    }

    /**
     * 创建嵌入模型实例
     *
     * @param config 模型配置
     * @return 嵌入模型实例
     */
    @Override
    public Object createEmbeddingModel(AiModelConfig config) {
        log.warn("DeepSeek 暂不支持嵌入模型");
        throw new UnsupportedOperationException("DeepSeek 暂不支持嵌入模型");
    }

    /**
     * 获取供应商名称
     *
     * @return 供应商名称
     */
    @Override
    public String getProviderName() {
        return ModelProviders.DEEPSEEK.getCode();
    }

    /**
     * 检查是否支持指定的模型类型
     *
     * @param modelType 模型类型
     * @return true-支持，false-不支持
     */
    @Override
    public boolean supports(ModelType modelType) {
        return modelType == ModelType.TEXT_GENERATION || modelType == ModelType.CODE_GENERATION;
    }
}

