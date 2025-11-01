package cn.refinex.ai.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * AI 模块配置属性
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "refinex.ai")
public class AiProperties {

    /**
     * 是否启用 AI 模块
     */
    private boolean enabled = true;

    /**
     * 默认配置
     */
    private DefaultConfig defaultConfig = new DefaultConfig();

    /**
     * 模型实例缓存配置
     */
    private CacheConfig cache = new CacheConfig();

    /**
     * 降级配置
     */
    private FallbackConfig fallback = new FallbackConfig();

    /**
     * 默认配置
     */
    @Data
    public static class DefaultConfig {
        /**
         * 请求超时时间, 单位：秒, 默认 60 秒
         */
        private Duration timeout = Duration.ofSeconds(60);

        /**
         * 熔断器阈值（连续失败次数）, 默认 10 次
         * <p>
         * 注意：重试相关配置请使用 Spring AI 官方的 spring.ai.retry.* 配置项
         * </p>
         */
        private int circuitBreakerThreshold = 10;
    }

    /**
     * 模型实例缓存配置
     */
    @Data
    public static class CacheConfig {
        /**
         * 缓存过期时间（小时）, 默认 24 小时
         */
        private int ttl = 24;

        /**
         * 最大缓存容量, 默认 100
         */
        private int maxSize = 100;

        /**
         * 是否启用缓存, 默认 true
         */
        private boolean enabled = true;
    }

    /**
     * 降级配置
     */
    @Data
    public static class FallbackConfig {
        /**
         * 默认聊天模型编码, 默认 gpt-3.5-turbo
         */
        private String defaultChatModel = "gpt-3.5-turbo";

        /**
         * 默认图像模型编码, 默认 dall-e-3
         */
        private String defaultImageModel = "dall-e-3";

        /**
         * 默认嵌入模型编码, 默认 text-embedding-ada-002
         */
        private String defaultEmbeddingModel = "text-embedding-ada-002";

        /**
         * 是否启用降级, 默认 true
         */
        private boolean enabled = true;
    }
}

