package cn.refinex.ai.core.factory;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.refinex.ai.config.properties.AiProperties;
import cn.refinex.ai.enums.VectorStoreType;
import cn.refinex.common.exception.SystemException;
import cn.refinex.common.utils.spring.SpringUtils;
import io.micrometer.observation.ObservationRegistry;
import io.milvus.client.MilvusServiceClient;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.ai.vectorstore.milvus.autoconfigure.MilvusServiceClientConnectionDetails;
import org.springframework.ai.vectorstore.milvus.autoconfigure.MilvusServiceClientProperties;
import org.springframework.ai.vectorstore.milvus.autoconfigure.MilvusVectorStoreAutoConfiguration;
import org.springframework.ai.vectorstore.milvus.autoconfigure.MilvusVectorStoreProperties;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.ai.vectorstore.qdrant.autoconfigure.QdrantVectorStoreAutoConfiguration;
import org.springframework.ai.vectorstore.qdrant.autoconfigure.QdrantVectorStoreProperties;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.vectorstore.redis.autoconfigure.RedisVectorStoreProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPooled;

import java.io.File;
import java.util.Map;
import java.util.Objects;

/**
 * 向量存储工厂
 * <p>
 * 提供动态创建 VectorStore 的能力，支持为不同的 EmbeddingModel 创建独立的 VectorStore 实例
 * 参考: <a href="https://github.com/YunaiV/yudao-cloud">...</a> 的 {@code cn.iocoder.yudao.module.ai.framework.ai.core.AiModelFactoryImpl}
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
public class VectorStoreFactory {

    private final AiProperties aiProperties;
    private final ObjectProvider<ObservationRegistry> observationRegistryProvider;
    private final ObjectProvider<VectorStoreObservationConvention> customObservationConventionProvider;
    private final ObjectProvider<BatchingStrategy> batchingStrategyProvider;

    public VectorStoreFactory(
            AiProperties aiProperties,
            ObjectProvider<ObservationRegistry> observationRegistryProvider,
            ObjectProvider<VectorStoreObservationConvention> customObservationConventionProvider,
            ObjectProvider<BatchingStrategy> batchingStrategyProvider) {
        this.aiProperties = aiProperties;
        this.observationRegistryProvider = observationRegistryProvider;
        this.customObservationConventionProvider = customObservationConventionProvider;
        this.batchingStrategyProvider = batchingStrategyProvider;
    }

    /**
     * 创建向量存储实例
     * <p>
     * 根据配置类型动态创建 VectorStore，支持为不同的 EmbeddingModel 创建独立的实例
     * </p>
     *
     * @param embeddingModel 嵌入模型
     * @return 向量存储实例
     */
    public VectorStore createVectorStore(EmbeddingModel embeddingModel, Map<String, Class<?>> metadataFields) {
        VectorStoreType type = aiProperties.getVectorStore().getType();

        log.debug("创建向量存储实例，类型: {}, EmbeddingModel: {}", type.getDescription(), embeddingModel.getClass().getSimpleName());

        return switch (type) {
            case SIMPLE -> createSimpleVectorStore(embeddingModel);
            case REDIS -> createRedisVectorStore(embeddingModel, metadataFields);
            case QDRANT -> createQdrantVectorStore(embeddingModel);
            case MILVUS -> createMilvusVectorStore(embeddingModel);
        };
    }

    /**
     * 创建 SimpleVectorStore 实例
     * <p>
     * 每次调用都创建新实例，支持使用不同的 EmbeddingModel
     * </p>
     *
     * @param embeddingModel 嵌入模型
     * @return SimpleVectorStore 实例
     */
    private VectorStore createSimpleVectorStore(EmbeddingModel embeddingModel) {
        AiProperties.VectorStoreConfig.SimpleConfig simpleConfig = aiProperties.getVectorStore().getSimple();
        String storePath = simpleConfig.getStorePath();

        // 确保存储目录存在
        File storeDir = new File(storePath);
        if (!storeDir.exists()) {
            boolean created = storeDir.mkdirs();
            if (created) {
                log.debug("创建向量存储目录: {}", storePath);
            }
        }

        // 创建存储文件路径
        File storeFile = new File(storeDir, "vector-store.json");

        // 创建 SimpleVectorStore 实例
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();

        // 如果存储文件存在，加载数据
        if (storeFile.exists()) {
            try {
                vectorStore.load(storeFile);
                log.debug("从文件加载向量存储数据: {}", storeFile.getAbsolutePath());
            } catch (Exception e) {
                log.error("加载向量存储数据失败: {}", e.getMessage(), e);
            }
        }

        log.debug("成功创建 SimpleVectorStore 实例");
        return vectorStore;
    }

    /**
     * 创建 Redis VectorStore 实例
     *
     * @param embeddingModel 嵌入模型
     * @param metadataFields 元数据字段映射，键为字段名，值为字段类型
     * @return RedisVectorStore 实例
     */
    private VectorStore createRedisVectorStore(EmbeddingModel embeddingModel, Map<String, Class<?>> metadataFields) {
        try {
            // 基于 Spring Boot 自动配置的 RedisProperties 创建 JedisPooled 实例
            RedisProperties redisProperties = SpringUtils.getBean(RedisProperties.class);
            JedisPooled jedisPooled = new JedisPooled(
                    redisProperties.getHost(),
                    redisProperties.getPort(),
                    redisProperties.getUsername(),
                    redisProperties.getPassword()
            );

            // 获取 RedisVectorStore 配置属性
            RedisVectorStoreProperties properties = SpringUtil.getBean(RedisVectorStoreProperties.class);
            // 创建 RedisVectorStore 实例
            RedisVectorStore redisVectorStore = RedisVectorStore.builder(jedisPooled, embeddingModel)
                    // 索引名称
                    .indexName(properties.getIndexName())
                    // 索引前缀
                    .prefix(properties.getPrefix())
                    // 是否初始化模式
                    .initializeSchema(properties.isInitializeSchema())
                    // 元数据字段
                    .metadataFields(
                            metadataFields.entrySet().stream()
                                    .map(entry -> {
                                        String fieldName = entry.getKey();
                                        Class<?> fieldType = entry.getValue();

                                        if (Number.class.isAssignableFrom(fieldType)) {
                                            return RedisVectorStore.MetadataField.numeric(fieldName);
                                        }
                                        if (Boolean.class.isAssignableFrom(fieldType)) {
                                            return RedisVectorStore.MetadataField.tag(fieldName);
                                        }

                                        return RedisVectorStore.MetadataField.text(fieldName);
                                    }).toList())
                    .observationRegistry(Objects.requireNonNull(observationRegistryProvider.getIfAvailable()))
                    .customObservationConvention(customObservationConventionProvider.getIfAvailable())
                    .batchingStrategy(Objects.requireNonNull(batchingStrategyProvider.getIfAvailable()))
                    .build();

            // 初始化索引
            redisVectorStore.afterPropertiesSet();
            return redisVectorStore;

        } catch (Exception e) {
            log.error("创建 RedisVectorStore 失败: {}", e.getMessage(), e);
            throw new SystemException("创建 RedisVectorStore 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建 Qdrant VectorStore 实例
     *
     * @param embeddingModel 嵌入模型
     * @return QdrantVectorStore 实例
     */
    private VectorStore createQdrantVectorStore(EmbeddingModel embeddingModel) {
        try {
            // 获取 Qdrant 配置
            QdrantVectorStoreAutoConfiguration configuration = new QdrantVectorStoreAutoConfiguration();
            // 获取 Qdrant 配置属性
            QdrantVectorStoreProperties properties = SpringUtil.getBean(QdrantVectorStoreProperties.class);
            // 创建 Qdrant gRPC 客户端构建器
            QdrantGrpcClient.Builder grpcClientBuilder = QdrantGrpcClient.newBuilder(
                    properties.getHost(),
                    properties.getPort(),
                    properties.isUseTls()
            );

            // 设置 Qdrant API 密钥
            if (StrUtil.isNotEmpty(properties.getApiKey())) {
                grpcClientBuilder.withApiKey(properties.getApiKey());
            }

            // 构建 Qdrant gRPC 客户端
            QdrantClient qdrantClient = new QdrantClient(grpcClientBuilder.build());

            // 创建 QdrantVectorStore 对象
            QdrantVectorStore vectorStore = configuration.vectorStore(
                    embeddingModel,
                    properties,
                    qdrantClient,
                    observationRegistryProvider,
                    customObservationConventionProvider,
                    batchingStrategyProvider.getIfAvailable()
            );

            // 初始化索引
            vectorStore.afterPropertiesSet();
            return vectorStore;

        } catch (Exception e) {
            log.error("创建 QdrantVectorStore 失败: {}", e.getMessage(), e);
            throw new SystemException("创建 QdrantVectorStore 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建 Milvus VectorStore 实例
     *
     * @param embeddingModel 嵌入模型
     * @return MilvusVectorStore 实例
     */
    private VectorStore createMilvusVectorStore(EmbeddingModel embeddingModel) {
        try {
            // 创建 MilvusVectorStore 配置
            MilvusVectorStoreAutoConfiguration configuration = new MilvusVectorStoreAutoConfiguration();
            // 获取 Milvus 配置属性
            MilvusVectorStoreProperties serverProperties = SpringUtil.getBean(MilvusVectorStoreProperties.class);
            // 获取 Milvus 客户端配置属性
            MilvusServiceClientProperties clientProperties = SpringUtil.getBean(MilvusServiceClientProperties.class);

            // 创建 Milvus 客户端
            MilvusServiceClient milvusClient = configuration.milvusClient(serverProperties, clientProperties,
                    new MilvusServiceClientConnectionDetails() {
                        @Override
                        public String getHost() {
                            return clientProperties.getHost();
                        }

                        @Override
                        public int getPort() {
                            return clientProperties.getPort();
                        }
                    }
            );

            // 创建 MilvusVectorStore 对象
            MilvusVectorStore vectorStore = configuration.vectorStore(
                    milvusClient,
                    embeddingModel,
                    serverProperties,
                    batchingStrategyProvider.getIfAvailable(),
                    observationRegistryProvider,
                    customObservationConventionProvider
            );

            // 初始化索引
            vectorStore.afterPropertiesSet();
            return vectorStore;

        } catch (Exception e) {
            log.error("创建 MilvusVectorStore 失败: {}", e.getMessage(), e);
            throw new SystemException("创建 MilvusVectorStore 失败: " + e.getMessage(), e);
        }
    }

}
