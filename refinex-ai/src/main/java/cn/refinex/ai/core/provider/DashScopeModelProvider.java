package cn.refinex.ai.core.provider;

import cn.refinex.ai.entity.AiModelConfig;
import cn.refinex.ai.enums.ModelProviders;
import cn.refinex.ai.enums.ModelType;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.api.DashScopeImageApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Objects;

/**
 * 阿里云通义千问（DashScope）模型供应商实现
 * <p>
 * see:
 * <ul>
 *  <li><a href="https://java2ai.com/docs/1.0.0.2/tutorials/basics/chat-model/?spm=4347728f.6bbdfafa.0.0.69ee175cNPFM9v">对话模型(Chat Model)</a></li>
 *  <li><a href="https://java2ai.com/docs/1.0.0.2/tutorials/basics/embedding/?spm=4347728f.6bbdfafa.0.0.69ee175cNPFM9v">嵌入模型 (Embedding Model)</a></li>
 * </ul>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DashScopeModelProvider implements ModelProvider {

    @SuppressWarnings("unused")
    private final RestClient.Builder restClientBuilder;
    private final ToolCallingManager toolCallingManager;

    /**
     * 创建聊天模型实例
     *
     * @param config 模型配置
     * @return 聊天模型实例
     */
    @Override
    public Object createChatModel(AiModelConfig config) {
        log.info("创建通义千问聊天模型: modelCode={}, modelVersion={}", config.getModelCode(), config.getModelVersion());

        // 构建 DashScope API 客户端
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .baseUrl(StringUtils.isNotBlank(config.getApiEndpoint()) ? config.getApiEndpoint() : "https://dashscope.aliyuncs.com")
                .apiKey(StringUtils.isNotBlank(config.getApiKey()) ? config.getApiKey() : System.getenv("DASHSCOPE_API_KEY"))
                .build();

        // 构建 DashScope 聊天选项
        DashScopeChatOptions dashScopeChatOptions = DashScopeChatOptions.builder()
                .withModel(StringUtils.isNotBlank(config.getModelVersion()) ? config.getModelVersion() : DashScopeApi.DEFAULT_CHAT_MODEL)
                .withTemperature(Objects.nonNull(config.getTemperature()) ? config.getTemperature() : 0.7)
                .withMaxToken(Objects.nonNull(config.getMaxTokens()) ? config.getMaxTokens() : 2048)
                .build();

        // 构建 DashScope 聊天模型
        return DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(dashScopeChatOptions)
                .toolCallingManager(toolCallingManager)
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
        log.info("创建通义万相图像模型: modelCode={}, modelVersion={}", config.getModelCode(), config.getModelVersion());

        // 构建 DashScope 图像 API 客户端
        DashScopeImageApi dashScopeImageApi = new DashScopeImageApi(
                StringUtils.isNotBlank(config.getApiEndpoint()) ? config.getApiEndpoint() : "https://dashscope.aliyuncs.com",
                StringUtils.isNotBlank(config.getApiKey()) ? config.getApiKey() : System.getenv("DASHSCOPE_API_KEY")
        );

        // 构建 DashScope 图像选项
        DashScopeImageOptions dashScopeImageOptions = DashScopeImageOptions.builder()
                .withModel(StringUtils.isNotBlank(config.getModelVersion()) ? config.getModelVersion() : DashScopeImageApi.DEFAULT_IMAGE_MODEL)
                .build();

        // 构建 DashScope 图像模型
        return new DashScopeImageModel(dashScopeImageApi, dashScopeImageOptions);
    }

    /**
     * 创建嵌入模型实例
     *
     * @param config 模型配置
     * @return 嵌入模型实例
     */
    @Override
    public Object createEmbeddingModel(AiModelConfig config) {
        log.info("创建通义千问嵌入模型: modelCode={}, modelVersion={}", config.getModelCode(), config.getModelVersion());

        // 构建 DashScope API 客户端
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .baseUrl(StringUtils.isNotBlank(config.getApiEndpoint()) ? config.getApiEndpoint() : "https://dashscope.aliyuncs.com")
                .apiKey(StringUtils.isNotBlank(config.getApiKey()) ? config.getApiKey() : System.getenv("DASHSCOPE_API_KEY"))
                .build();

        // 构建 DashScope 嵌入选项
        DashScopeEmbeddingOptions dashScopeEmbeddingOptions = DashScopeEmbeddingOptions.builder()
                .withModel(StringUtils.isNotBlank(config.getModelVersion()) ? config.getModelVersion() : DashScopeApi.DEFAULT_EMBEDDING_MODEL)
                .build();

        // 构建 DashScope 嵌入模型
        return new DashScopeEmbeddingModel(dashScopeApi, MetadataMode.EMBED, dashScopeEmbeddingOptions);
    }

    /**
     * 获取供应商名称
     *
     * @return 供应商名称
     */
    @Override
    public String getProviderName() {
        return ModelProviders.QWEN.getCode();
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

