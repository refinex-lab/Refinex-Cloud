package cn.refinex.ai.config;

import cn.refinex.ai.config.properties.AiProperties;
import cn.refinex.ai.core.factory.AiModelFactory;
import cn.refinex.ai.core.provider.ModelProvider;
import cn.refinex.ai.repository.AiModelConfigRepository;
import cn.refinex.common.jdbc.service.SensitiveDataService;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

/**
 * AI 模块自动配置类
 * <p>
 * 负责注册 AI 模块的核心 Bean，包括配置属性、模型工厂等
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(AiProperties.class)
@ConditionalOnProperty(prefix = "refinex.ai", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AiAutoConfiguration {

    /**
     * 配置 Spring AI 通用 RestClient.Builder
     * <p>
     * 用于构建各供应商的 HTTP 客户端，统一配置超时、重试等参数
     * </p>
     *
     * @param aiProperties AI 配置属性
     * @return RestClient.Builder
     */
    @Bean
    public RestClient.Builder aiRestClientBuilder(AiProperties aiProperties) {
        Duration timeout = aiProperties.getDefaultConfig().getTimeout();
        return RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory(
                        HttpClient.newBuilder()
                                .connectTimeout(timeout)
                                .build()
                ));
    }

    /**
     * 注册 AI 模型工厂
     *
     * @param configRepository     模型配置仓库
     * @param sensitiveDataService 敏感数据服务
     * @param aiProperties         AI 配置属性
     * @param providers            模型供应商列表
     * @return AI 模型工厂
     */
    @Bean
    public AiModelFactory aiModelFactory(
            AiModelConfigRepository configRepository,
            SensitiveDataService sensitiveDataService,
            AiProperties aiProperties,
            List<ModelProvider> providers) {
        log.info("注册 AI 模型工厂，支持 {} 个供应商", providers.size());
        return new AiModelFactory(configRepository, sensitiveDataService, aiProperties, providers);
    }

    /**
     * 注册 ObservationRegistry, 用于 AI 模型的监控和观测
     * <p>
     * 该 Bean 用于记录 AI 模型的调用指标，如请求时间、响应时间等
     * </p>
     *
     * @return ObservationRegistry
     */
    @Bean
    @ConditionalOnMissingBean
    public ObservationRegistry observationRegistry() {
        return ObservationRegistry.create();
    }


    /**
     * 注册 ToolCallingManager，用于管理 AI 工具调用
     * <p>
     * 如果应用中没有自定义的 ToolCallingManager，则使用默认实现
     * </p>
     *
     * @return ToolCallingManager
     */
    @Bean
    @ConditionalOnMissingBean
    public ToolCallingManager toolCallingManager() {
        // ToolCallingManager 用于管理 AI 工具调用（Function Calling）
        // 暂时不需要工具调用功能，先返回一个空实现
        return ToolCallingManager.builder().build();
    }

    /**
     * 配置完成回调
     *
     * @param aiProperties AI 配置属性
     * @return 配置完成回调实例
     */
    @Bean
    public AiConfigurationCallback aiConfigurationCallback(AiProperties aiProperties) {
        return new AiConfigurationCallback(aiProperties);
    }

    /**
     * AI 配置回调类
     */
    public static class AiConfigurationCallback {
        public AiConfigurationCallback(AiProperties aiProperties) {
            log.info("=== AI 模块配置完成 ===");
            log.info("启用状态: {}", aiProperties.isEnabled());
            log.info("请求超时: {}秒", aiProperties.getDefaultConfig().getTimeout().getSeconds());
            log.info("熔断阈值: {}", aiProperties.getDefaultConfig().getCircuitBreakerThreshold());
            log.info("缓存启用: {}", aiProperties.getCache().isEnabled());
            log.info("缓存容量: {}", aiProperties.getCache().getMaxSize());
            log.info("缓存过期: {}小时", aiProperties.getCache().getTtl());
            log.info("默认聊天模型: {}", aiProperties.getFallback().getDefaultChatModel());
            log.info("注意: 重试配置请使用 Spring AI 官方的 spring.ai.retry.* 配置项");
            log.info("=========================");
        }
    }
}

