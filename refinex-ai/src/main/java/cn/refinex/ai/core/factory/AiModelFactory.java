package cn.refinex.ai.core.factory;

import cn.hutool.core.convert.Convert;
import cn.refinex.ai.properties.AiProperties;
import cn.refinex.ai.core.client.AiModelClient;
import cn.refinex.ai.core.client.ChatModelClient;
import cn.refinex.ai.core.client.EmbeddingModelClient;
import cn.refinex.ai.core.client.ImageModelClient;
import cn.refinex.ai.core.provider.ModelProvider;
import cn.refinex.ai.entity.AiModelConfig;
import cn.refinex.ai.enums.ModelType;
import cn.refinex.ai.repository.AiModelConfigRepository;
import cn.refinex.common.exception.SystemException;
import cn.refinex.common.jdbc.service.SensitiveDataService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImageModel;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI 模型工厂（核心）
 * <p>
 * 负责创建和缓存 AI 模型实例，支持：
 * <ul>
 *   <li>从数据库动态加载模型配置</li>
 *   <li>模型实例缓存（基于 Caffeine）</li>
 *   <li>敏感数据解密（API Key）</li>
 *   <li>多供应商适配</li>
 *   <li>降级模型支持</li>
 * </ul>
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
public class AiModelFactory {

    private final AiModelConfigRepository configRepository;
    private final SensitiveDataService sensitiveDataService;
    private final AiProperties aiProperties;
    private final Map<String, ModelProvider> providerMap;

    /**
     * 模型客户端缓存（key: modelCode）
     */
    private final Cache<String, AiModelClient<?, ?>> clientCache;

    /**
     * 构造函数
     *
     * @param configRepository     模型配置仓库
     * @param sensitiveDataService 敏感数据服务
     * @param aiProperties         AI 配置属性
     * @param providers            模型供应商列表
     */
    public AiModelFactory(
            AiModelConfigRepository configRepository,
            SensitiveDataService sensitiveDataService,
            AiProperties aiProperties,
            List<ModelProvider> providers) {
        this.configRepository = configRepository;
        this.sensitiveDataService = sensitiveDataService;
        this.aiProperties = aiProperties;

        // 构建供应商映射
        this.providerMap = providers.stream()
                .collect(Collectors.toMap(ModelProvider::getProviderName, p -> p));

        // 初始化缓存
        this.clientCache = Caffeine.newBuilder()
                .maximumSize(aiProperties.getCache().getMaxSize())
                .expireAfterWrite(Duration.ofHours(aiProperties.getCache().getTtl()))
                .recordStats()
                .build();

        log.info("AI 模型工厂初始化完成，支持供应商: {}", providerMap.keySet());
    }

    /**
     * 获取聊天模型客户端
     *
     * @param modelCode 模型编码
     * @return 聊天模型客户端
     */
    public ChatModelClient getChatModelClient(String modelCode) {
        return (ChatModelClient) getOrCreateClient(modelCode, ModelType.TEXT_GENERATION);
    }

    /**
     * 获取图像模型客户端
     *
     * @param modelCode 模型编码
     * @return 图像模型客户端
     */
    public ImageModelClient getImageModelClient(String modelCode) {
        return (ImageModelClient) getOrCreateClient(modelCode, ModelType.IMAGE_GENERATION);
    }

    /**
     * 获取嵌入模型客户端
     *
     * @param modelCode 模型编码
     * @return 嵌入模型客户端
     */
    public EmbeddingModelClient getEmbeddingModelClient(String modelCode) {
        return (EmbeddingModelClient) getOrCreateClient(modelCode, ModelType.EMBEDDING);
    }

    /**
     * 获取或创建模型客户端
     *
     * @param modelCode 模型编码
     * @param modelType 模型类型
     * @return 模型客户端
     */
    private AiModelClient<?, ?> getOrCreateClient(String modelCode, ModelType modelType) {
        if (!aiProperties.getCache().isEnabled()) {
            // 缓存未启用，直接创建
            return createClient(modelCode, modelType);
        }

        // 从缓存获取或创建
        return clientCache.get(modelCode, key -> createClient(key, modelType));
    }

    /**
     * 创建模型客户端
     *
     * @param modelCode 模型编码
     * @param modelType 模型类型
     * @return 模型客户端
     */
    private AiModelClient<?, ?> createClient(String modelCode, ModelType modelType) {
        try {
            log.debug("创建 AI 模型客户端: modelCode={}, modelType={}", modelCode, modelType);

            // 1. 从数据库加载配置
            AiModelConfig config = configRepository.findByModelCode(modelCode);
            if (config == null) {
                log.error("模型配置不存在: modelCode={}", modelCode);
                throw new SystemException("模型配置不存在: " + modelCode);
            }

            // 2. 解密 API Key
            String decryptedApiKey = sensitiveDataService.queryAndDecrypt(
                    "ai_model_config",
                    Convert.toStr(config.getId()),
                    "api_key"
            );
            config.setApiKey(decryptedApiKey);

            // 3. 获取供应商适配器
            ModelProvider provider = providerMap.get(config.getProvider());
            if (provider == null) {
                log.error("不支持的供应商: provider={}", config.getProvider());
                throw new SystemException("不支持的供应商: " + config.getProvider());
            }

            // 4. 创建模型实例
            Object modelInstance = createModelInstance(provider, config, modelType);

            // 5. 包装为客户端
            return wrapModelClient(modelInstance, config, modelType);

        } catch (Exception e) {
            log.error("创建 AI 模型客户端失败: modelCode={}, modelType={}", modelCode, modelType, e);

            // 尝试降级
            if (aiProperties.getFallback().isEnabled()) {
                return tryFallback(modelCode, modelType);
            }

            throw new SystemException("创建 AI 模型客户端失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建模型实例
     *
     * @param provider  供应商
     * @param config    配置
     * @param modelType 模型类型
     * @return 模型实例
     */
    private Object createModelInstance(ModelProvider provider, AiModelConfig config, ModelType modelType) {
        return switch (modelType) {
            case TEXT_GENERATION, CODE_GENERATION, MULTIMODAL -> provider.createChatModel(config);
            case IMAGE_GENERATION -> provider.createImageModel(config);
            case EMBEDDING -> provider.createEmbeddingModel(config);
            default -> throw new SystemException("不支持的模型类型: " + modelType);
        };
    }

    /**
     * 包装模型客户端
     *
     * @param modelInstance 模型实例
     * @param config        配置
     * @param modelType     模型类型
     * @return 模型客户端
     */
    private AiModelClient<?, ?> wrapModelClient(Object modelInstance, AiModelConfig config, ModelType modelType) {
        return switch (modelType) {
            case TEXT_GENERATION, CODE_GENERATION, MULTIMODAL ->
                    new ChatModelClient((ChatModel) modelInstance, config.getProvider(), config.getModelCode());
            case IMAGE_GENERATION ->
                    new ImageModelClient((ImageModel) modelInstance, config.getProvider(), config.getModelCode());
            case EMBEDDING ->
                    new EmbeddingModelClient((EmbeddingModel) modelInstance, config.getProvider(), config.getModelCode());
            default -> throw new SystemException("不支持的模型类型: " + modelType);
        };
    }

    /**
     * 尝试降级到备用模型
     *
     * @param modelCode 原模型编码
     * @param modelType 模型类型
     * @return 降级模型客户端
     */
    private AiModelClient<?, ?> tryFallback(String modelCode, ModelType modelType) {
        try {
            log.warn("尝试降级模型: modelCode={}, modelType={}", modelCode, modelType);

            // 从数据库查询降级模型
            AiModelConfig fallbackConfig = configRepository.findFallbackModel(modelCode);
            if (fallbackConfig != null) {
                log.info("使用数据库配置的降级模型: fallbackModelCode={}", fallbackConfig.getModelCode());
                return createClient(fallbackConfig.getModelCode(), modelType);
            }

            // 使用配置文件中的默认模型
            String defaultModelCode = switch (modelType) {
                case TEXT_GENERATION, CODE_GENERATION, MULTIMODAL -> aiProperties.getFallback().getDefaultChatModel();
                case IMAGE_GENERATION -> aiProperties.getFallback().getDefaultImageModel();
                case EMBEDDING -> aiProperties.getFallback().getDefaultEmbeddingModel();
                default -> null;
            };

            if (defaultModelCode != null && !defaultModelCode.equals(modelCode)) {
                log.info("使用配置文件的默认模型: defaultModelCode={}", defaultModelCode);
                return createClient(defaultModelCode, modelType);
            }

        } catch (Exception e) {
            log.error("降级模型失败", e);
        }

        throw new SystemException("模型降级失败");
    }

    /**
     * 清除指定模型的缓存
     *
     * @param modelCode 模型编码
     */
    public void evictCache(String modelCode) {
        clientCache.invalidate(modelCode);
        log.info("已清除模型缓存: modelCode={}", modelCode);
    }

    /**
     * 清除所有缓存
     */
    public void evictAllCache() {
        clientCache.invalidateAll();
        log.info("已清除所有模型缓存");
    }

    /**
     * 获取缓存统计信息
     *
     * @return 缓存统计
     */
    public Map<String, Object> getCacheStats() {
        var stats = clientCache.stats();
        return Map.of(
                "hitCount", stats.hitCount(),
                "missCount", stats.missCount(),
                "hitRate", stats.hitRate(),
                "evictionCount", stats.evictionCount(),
                "size", clientCache.estimatedSize()
        );
    }
}

