package cn.refinex.mq.redis.config;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.HostInfo;
import cn.hutool.system.SystemUtil;
import cn.refinex.mq.redis.config.properties.RefinexRedisMQProperties;
import cn.refinex.mq.redis.core.RedisMQTemplate;
import cn.refinex.mq.redis.core.job.RedisPendingMessageResendJob;
import cn.refinex.mq.redis.core.job.RedisStreamMessageCleanupJob;
import cn.refinex.mq.redis.core.pubsub.AbstractRedisChannelMessageListener;
import cn.refinex.mq.redis.core.stream.AbstractRedisStreamMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Redis MQ 消费者自动配置类
 *
 * <p>该配置类负责初始化 Redis 消息队列的消费者相关组件，包括：</p>
 * <ul>
 *     <li>Redis Pub/Sub 广播消费容器</li>
 *     <li>Redis Stream 集群消费容器</li>
 *     <li>Pending 消息重发定时任务</li>
 *     <li>Stream 消息清理定时任务</li>
 * </ul>
 *
 * <h3>前置条件：</h3>
 * <ul>
 *     <li>Redis 版本 >= 5.0.0（Stream 功能需要）</li>
 *     <li>已配置 RedisTemplate 和 RedissonClient</li>
 *     <li>存在对应的消息监听器实现</li>
 * </ul>
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@EnableScheduling
@AutoConfiguration
@EnableConfigurationProperties(RefinexRedisMQProperties.class)
public class RefinexRedisMQConsumerAutoConfiguration {

    /**
     * Redis 最低版本要求
     */
    private static final int MIN_REDIS_VERSION = 5;

    /**
     * 版本校验文档链接（可替换为实际文档地址）
     */
    private static final String VERSION_DOC_URL = "https://redis.io/download";

    /**
     * 创建 Redis Pub/Sub 广播消费容器
     *
     * <p>该容器负责管理所有 Pub/Sub 模式的消息监听器，当有消息发布到对应 Channel 时，自动触发监听器处理。</p>
     *
     * <h4>工作原理：</h4>
     * <ol>
     *     <li>扫描所有 AbstractRedisChannelMessageListener 实现类</li>
     *     <li>为每个监听器订阅对应的 Redis Channel</li>
     *     <li>消息到达时自动路由到对应的监听器</li>
     * </ol>
     *
     * @param redisMQTemplate Redis MQ 模板
     * @param listeners       所有 Channel 消息监听器
     * @return RedisMessageListenerContainer 实例
     */
    @Bean
    @ConditionalOnBean(AbstractRedisChannelMessageListener.class)
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisMQTemplate redisMQTemplate, List<AbstractRedisChannelMessageListener<?>> listeners) {
        log.info("[Redis MQ] 开始初始化 Pub/Sub 消费容器，监听器数量: {}", listeners.size());

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisMQTemplate.getRedisTemplate().getRequiredConnectionFactory());

        listeners.forEach(listener -> registerChannelListener(container, redisMQTemplate, listener));

        log.info("[Redis MQ] Pub/Sub 消费容器初始化完成");
        return container;
    }

    /**
     * 注册单个 Channel 监听器
     *
     * @param container       Redis 消息监听容器
     * @param redisMQTemplate Redis MQ 模板
     * @param listener        消息监听器实例
     */
    private void registerChannelListener(RedisMessageListenerContainer container,
                                         RedisMQTemplate redisMQTemplate,
                                         AbstractRedisChannelMessageListener<?> listener) {
        try {
            listener.setRedisMQTemplate(redisMQTemplate);
            container.addMessageListener(listener, new ChannelTopic(listener.getChannel()));
            log.info("[Redis MQ] 注册 Channel 监听器成功 - Channel: {}, 监听器: {}", listener.getChannel(), listener.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("[Redis MQ] 注册 Channel 监听器失败 - Channel: {}, 监听器: {}", listener.getChannel(), listener.getClass().getSimpleName(), e);
            throw new IllegalStateException("注册 Channel 监听器失败", e);
        }
    }

    /**
     * 创建 Redis Stream 集群消费容器
     *
     * <p>该容器负责管理所有 Stream 模式的消息监听器，支持消费者组模式，实现集群环境下的负载均衡消费。</p>
     *
     * <h4>Stream 消费特性：</h4>
     * <ul>
     *     <li>支持消费者组（Consumer Group）模式</li>
     *     <li>消息在组内消费者间负载均衡分配</li>
     *     <li>支持消息确认（ACK）机制</li>
     *     <li>未确认消息会保留在 Pending 列表中</li>
     *     <li>消费者宕机后消息可被其他消费者接管</li>
     * </ul>
     *
     * <h4>重要说明：</h4>
     * <p>Stream 模式要求 Redis 版本 >= 5.0.0，启动时会自动校验版本。</p>
     *
     * @param redisMQTemplate Redis MQ 模板
     * @param listeners       所有 Stream 消息监听器
     * @param properties      Redis MQ 配置属性
     * @return StreamMessageListenerContainer 实例
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnBean(AbstractRedisStreamMessageListener.class)
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> redisStreamMessageListenerContainer(
            RedisMQTemplate redisMQTemplate,
            List<AbstractRedisStreamMessageListener<?>> listeners,
            RefinexRedisMQProperties properties) {

        log.info("[Redis MQ] 开始初始化 Stream 消费容器，监听器数量: {}", listeners.size());

        // 获取 Redis 模板, 校验 Redis 版本
        RedisTemplate<String, ?> redisTemplate = redisMQTemplate.getRedisTemplate();
        validateRedisVersion(redisTemplate);

        // 创建 Stream 消费容器
        StreamMessageListenerContainer<String, ObjectRecord<String, String>> container =
                createStreamContainer(redisTemplate, properties);

        // 为每个监听器注册 Stream 消息监听器
        String consumerName = buildConsumerName();
        listeners.forEach(listener -> registerStreamListener(container, redisTemplate, redisMQTemplate,listener, consumerName));

        log.info("[Redis MQ] Stream 消费容器初始化完成");
        return container;
    }

    /**
     * 创建 Stream 消费容器
     * <p>
     * 该容器负责管理所有 Stream 模式的消息监听器，支持消费者组模式，实现集群环境下的负载均衡消费。
     *
     * @param redisTemplate Redis 模板
     * @param properties    Redis MQ 配置属性
     * @return StreamMessageListenerContainer 实例
     */
    private StreamMessageListenerContainer<String, ObjectRecord<String, String>> createStreamContainer(
            RedisTemplate<String, ?> redisTemplate,
            RefinexRedisMQProperties properties) {

        // 获取 Stream 模式的批量处理大小
        Integer batchSize = properties.getStream().getBatchSize();

        // 创建 Stream 消费容器选项
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        // 设置批量处理大小
                        .batchSize(batchSize)
                        // 设置目标消息类型为 String
                        .targetType(String.class)
                        .build();

        // 创建 Stream 消费容器
        return StreamMessageListenerContainer.create(redisTemplate.getRequiredConnectionFactory(), options);
    }

    /**
     * 注册 Stream 消息监听器
     * <p>
     * 该方法负责为每个 Stream 消息监听器创建一个消费者组，并将其注册到 Stream 消费容器中。
     * 每个监听器会在独立的消费者组中运行，支持负载均衡消费。
     *
     * @param container       Stream 消费容器
     * @param redisTemplate   Redis 模板
     * @param redisMQTemplate Redis MQ 模板
     * @param listener        Stream 消息监听器
     * @param consumerName    消费者名称
     */
    private void registerStreamListener(StreamMessageListenerContainer<String, ObjectRecord<String, String>> container,
                                        RedisTemplate<String, ?> redisTemplate,
                                        RedisMQTemplate redisMQTemplate,
                                        AbstractRedisStreamMessageListener<?> listener,
                                        String consumerName) {
        try {
            log.info("[Redis MQ] 开始注册 Stream 监听器 - StreamKey: {}, 监听器: {}",
                    listener.getStreamKey(), listener.getClass().getSimpleName());

            // 创建消费者组（如果不存在）
            createConsumerGroupIfAbsent(redisTemplate, listener);
            // 设置 Redis MQ 模板
            listener.setRedisMQTemplate(redisMQTemplate);

            // 创建消费者实例
            Consumer consumer = Consumer.from(listener.getGroup(), consumerName);
            // 设置 Stream 偏移量（从最新消息开始消费）
            StreamOffset<String> streamOffset = StreamOffset.create(listener.getStreamKey(), ReadOffset.lastConsumed());

            // 创建 Stream 读取请求构建器
            StreamMessageListenerContainer.StreamReadRequestBuilder<String> builder =
                    StreamMessageListenerContainer.StreamReadRequest.builder(streamOffset)
                            // 设置消费者实例
                            .consumer(consumer)
                            // 禁用自动确认，手动确认消息处理结果
                            .autoAcknowledge(false)
                            // 当发生错误时，不取消消费者组，继续监听
                            .cancelOnError(throwable -> false);

            // 注册 Stream 读取请求到容器
            container.register(builder.build(), listener);

            log.info("[Redis MQ] 注册 Stream 监听器成功 - StreamKey: {}, 消费者组: {}, 消费者: {}",
                    listener.getStreamKey(), listener.getGroup(), consumerName);
        } catch (Exception e) {
            log.error("[Redis MQ] 注册 Stream 监听器失败 - StreamKey: {}, 监听器: {}",
                    listener.getStreamKey(), listener.getClass().getSimpleName(), e);
            throw new IllegalStateException("注册 Stream 监听器失败", e);
        }
    }

    /**
     * 创建消费者组（如果不存在）
     * <p>
     * 该方法负责检查指定的 Stream 键和消费者组是否已存在。如果不存在，则创建一个新的消费者组。
     * 该操作确保每个 Stream 监听器都在独立的消费者组中运行，支持负载均衡消费。
     *
     * @param redisTemplate Redis 模板
     * @param listener      Stream 消息监听器
     */
    private void createConsumerGroupIfAbsent(RedisTemplate<String, ?> redisTemplate, AbstractRedisStreamMessageListener<?> listener) {
        try {
            redisTemplate.opsForStream().createGroup(listener.getStreamKey(), listener.getGroup());
            log.debug("[Redis MQ] 创建消费者组成功 - StreamKey: {}, Group: {}", listener.getStreamKey(), listener.getGroup());
        } catch (Exception e) {
            log.debug("[Redis MQ] 消费者组已存在，跳过创建 - StreamKey: {}, Group: {}", listener.getStreamKey(), listener.getGroup());
        }
    }

    /**
     * 创建 Pending 消息重发任务
     *
     * <p>该任务负责扫描所有 Stream 的 Pending 列表，将超时未确认的消息重新投递到 Stream 中，确保消息不丢失。</p>
     *
     * <h4>工作机制：</h4>
     * <ol>
     *     <li>定时扫描所有 Stream 的 Pending 列表</li>
     *     <li>识别超时未确认的消息</li>
     *     <li>将超时消息重新投递</li>
     *     <li>确认原消息已处理</li>
     * </ol>
     *
     * @param listeners      Stream 消息监听器列表
     * @param redisTemplate  Redis MQ 模板
     * @param groupName      消费者组名称
     * @param redissonClient Redisson 客户端（用于分布式锁）
     * @param properties     配置属性
     * @return RedisPendingMessageResendJob 实例
     */
    @Bean
    @ConditionalOnBean(AbstractRedisStreamMessageListener.class)
    @ConditionalOnProperty(prefix = "refinex.mq.redis.stream.pending-message", name = "enabled", havingValue = "true", matchIfMissing = true)
    public RedisPendingMessageResendJob redisPendingMessageResendJob(
            List<AbstractRedisStreamMessageListener<?>> listeners,
            RedisMQTemplate redisTemplate,
            @Value("${spring.application.name}") String groupName,
            RedissonClient redissonClient,
            RefinexRedisMQProperties properties) {

        log.info("[Redis MQ] 初始化 Pending 消息重发任务");
        return new RedisPendingMessageResendJob(listeners, redisTemplate, groupName, redissonClient, properties);
    }

    /**
     * 创建 Stream 消息清理任务
     *
     * <p>该任务负责定期清理 Stream 中的历史消息，防止消息积压导致内存占用过高。</p>
     *
     * <h4>清理策略：</h4>
     * <ul>
     *     <li>只保留最近的 N 条消息（可配置）</li>
     *     <li>采用近似裁剪策略，提高性能</li>
     *     <li>不影响正在处理的消息</li>
     * </ul>
     *
     * @param listeners      Stream 消息监听器列表
     * @param redisTemplate  Redis MQ 模板
     * @param redissonClient Redisson 客户端
     * @param properties     配置属性
     * @return RedisStreamMessageCleanupJob 实例
     */
    @Bean
    @ConditionalOnBean(AbstractRedisStreamMessageListener.class)
    @ConditionalOnProperty(prefix = "refinex.mq.redis.stream.cleanup", name = "enabled", havingValue = "true", matchIfMissing = true)
    public RedisStreamMessageCleanupJob redisStreamMessageCleanupJob(
            List<AbstractRedisStreamMessageListener<?>> listeners,
            RedisMQTemplate redisTemplate,
            RedissonClient redissonClient,
            RefinexRedisMQProperties properties) {

        log.info("[Redis MQ] 初始化 Stream 消息清理任务");
        return new RedisStreamMessageCleanupJob(listeners, redisTemplate, redissonClient, properties);
    }

    /**
     * 构建消费者名称
     *
     * <p>使用 "IP地址@进程ID" 的格式，确保在集群环境中每个消费者都有唯一标识。参考 RocketMQ 的 clientId 实现方式。</p>
     *
     * @return 消费者名称，格式：IP@PID
     */
    private static String buildConsumerName() {
        String hostAddress = Optional.ofNullable(SystemUtil.getHostInfo())
                .map(HostInfo::getAddress)
                .orElse("unknown");
        long pid = SystemUtil.getCurrentPID();

        return String.format("%s@%d", hostAddress, pid);
    }

    /**
     * 校验 Redis 版本
     *
     * <p>Redis Stream 功能需要 5.0.0 及以上版本，如果版本不满足要求，将抛出异常终止启动。</p>
     *
     * @param redisTemplate Redis 模板
     * @throws IllegalStateException 如果 Redis 版本不满足要求
     */
    private static void validateRedisVersion(RedisTemplate<String, ?> redisTemplate) {
        try {
            // 执行 INFO 命令获取 Redis 版本信息
            Properties info = redisTemplate.execute((RedisCallback<Properties>) RedisServerCommands::info);
            if (info == null) {
                log.warn("[Redis MQ] 无法获取 Redis 版本信息，跳过版本校验");
                return;
            }

            // 从版本信息中提取主版本号
            String version = MapUtil.getStr(info, "redis_version");
            if (StrUtil.isBlank(version)) {
                log.warn("[Redis MQ] Redis 版本信息为空，跳过版本校验");
                return;
            }

            // 解析主版本号并校验是否满足要求
            int majorVersion = Integer.parseInt(StrUtil.subBefore(version, '.', false));
            if (majorVersion < MIN_REDIS_VERSION) {
                String errorMsg = String.format(
                        "当前 Redis 版本为 %s，低于最低要求版本 %d.0.0。Redis Stream 功能需要 5.0.0 及以上版本，请参考文档升级：%s",
                        version, MIN_REDIS_VERSION, VERSION_DOC_URL);
                throw new IllegalStateException(errorMsg);
            }

            log.info("[Redis MQ] Redis 版本校验通过，当前版本: {}", version);
        } catch (NumberFormatException e) {
            log.error("[Redis MQ] 解析 Redis 版本号失败", e);
            throw new IllegalStateException("解析 Redis 版本号失败", e);
        }
    }
}
