package cn.refinex.mq.redis.core.job;

import cn.hutool.core.collection.CollUtil;
import cn.refinex.mq.redis.config.properties.RefinexRedisMQProperties;
import cn.refinex.mq.redis.core.RedisMQTemplate;
import cn.refinex.mq.redis.core.stream.AbstractRedisStreamMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis Stream Pending 消息重发定时任务
 *
 * <p>
 * 该任务负责处理消费者崩溃后未确认的消息，确保消息不会丢失。
 * 当消费者处理消息过程中发生宕机或异常时，消息会停留在 Pending 列表中，
 * 本任务会定期扫描超时的 Pending 消息并重新投递。
 * </p>
 *
 * <h3>工作原理：</h3>
 * <ol>
 *     <li>定时扫描所有 Stream 的消费者组</li>
 *     <li>获取每个消费者的 Pending 消息列表</li>
 *     <li>识别超过超时时间仍未确认的消息</li>
 *     <li>将超时消息重新投递到 Stream</li>
 *     <li>确认原消息，从 Pending 列表中移除</li>
 * </ol>
 *
 * <h3>为什么需要重发机制：</h3>
 * <p>在分布式环境中，消费者可能因为以下原因无法完成消息处理：</p>
 * <ul>
 *     <li>消费者进程崩溃或重启</li>
 *     <li>网络故障导致 ACK 失败</li>
 *     <li>处理超时但未完成</li>
 *     <li>消费者被强制停止</li>
 * </ul>
 *
 * <h3>配置示例：</h3>
 * <pre>{@code
 * refinex:
 *   mq:
 *     redis:
 *       stream:
 *         pending-message:
 *           enabled: true              # 是否启用
 *           expire-time: 5m            # 消息超时时间
 *           cron: "35 * * * * ?"       # 执行频率
 *           lock-timeout: 30s          # 分布式锁超时时间
 * }</pre>
 *
 * <h3>重要说明：</h3>
 * <ul>
 *     <li><strong>分布式锁：</strong>使用 Redisson 分布式锁，确保集群环境下只有一个实例执行</li>
 *     <li><strong>超时时间：</strong>应大于消息的正常处理时间，避免误重发</li>
 *     <li><strong>执行频率：</strong>不宜过高，建议 1-5 分钟执行一次</li>
 *     <li><strong>重发策略：</strong>重新投递消息，而不是直接分配给其他消费者</li>
 * </ul>
 *
 * @author 芋道源码
 * @author Refinex
 * @see AbstractRedisStreamMessageListener
 * @see RefinexRedisMQProperties
 * @since 1.0.0
 */
@Slf4j
public class RedisPendingMessageResendJob {

    /**
     * 分布式锁的 Key
     */
    private static final String LOCK_KEY = "refinex:mq:redis:pending-message-resend:lock";

    /**
     * 所有 Stream 消息监听器
     */
    private final List<AbstractRedisStreamMessageListener<?>> listeners;

    /**
     * Redis MQ 模板
     */
    private final RedisMQTemplate redisMQTemplate;

    /**
     * 消费者组名称（通常是应用名）
     */
    private final String groupName;

    /**
     * Redisson 客户端，用于分布式锁
     */
    private final RedissonClient redissonClient;

    /**
     * 配置属性
     */
    private final RefinexRedisMQProperties properties;

    /**
     * 构造函数
     *
     * @param listeners       Stream 消息监听器列表
     * @param redisMQTemplate Redis MQ 模板
     * @param groupName       消费者组名称
     * @param redissonClient  Redisson 客户端
     * @param properties      配置属性
     */
    public RedisPendingMessageResendJob(List<AbstractRedisStreamMessageListener<?>> listeners,
                                        RedisMQTemplate redisMQTemplate,
                                        String groupName,
                                        RedissonClient redissonClient,
                                        RefinexRedisMQProperties properties) {
        this.listeners = listeners;
        this.redisMQTemplate = redisMQTemplate;
        this.groupName = groupName;
        this.redissonClient = redissonClient;
        this.properties = properties;
    }

    /**
     * 定时执行消息重发任务
     *
     * <p>
     * 执行时机由配置文件中的 cron 表达式控制，默认每分钟的第 35 秒执行。
     * 选择第 35 秒是为了避开整点任务过多导致的资源竞争。
     * </p>
     *
     * <p>
     * 该方法使用分布式锁确保集群环境下只有一个实例执行任务，
     * 采用非阻塞的 tryLock 方式，如果获取锁失败则直接跳过本次执行。
     * </p>
     */
    @Scheduled(cron = "${refinex.mq.redis.stream.pending-message.cron:35 * * * * ?}")
    public void execute() {
        // 检查功能是否启用
        if (!isEnabled()) {
            return;
        }

        // 获取分布式锁和设置超时时间
        RLock lock = redissonClient.getLock(LOCK_KEY);
        long lockTimeout = properties.getStream().getPendingMessage().getLockTimeout().getSeconds();

        try {
            // 尝试获取锁，避免阻塞
            boolean locked = lock.tryLock(0, lockTimeout, TimeUnit.SECONDS);
            if (!locked) {
                log.debug("[Pending消息重发] 未获取到分布式锁，跳过本次执行");
                return;
            }

            log.info("[Pending消息重发] 开始执行任务，监听器数量: {}", listeners.size());
            long startTime = System.currentTimeMillis();

            processPendingMessages();

            long cost = System.currentTimeMillis() - startTime;
            log.info("[Pending消息重发] 任务执行完成，耗时: {} ms", cost);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[Pending消息重发] 获取分布式锁被中断", e);
        } catch (Exception e) {
            log.error("[Pending消息重发] 执行任务异常", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 处理所有 Pending 消息
     */
    private void processPendingMessages() {
        // 获取 Stream 操作对象
        StreamOperations<String, Object, Object> ops = redisMQTemplate.getRedisTemplate().opsForStream();
        // 遍历所有监听器，处理其对应的 Pending 消息
        listeners.forEach(listener -> {
            try {
                processPendingMessagesForListener(ops, listener);
            } catch (Exception e) {
                log.error("[Pending消息重发] 处理 Stream({}) 的 Pending 消息异常", listener.getStreamKey(), e);
            }
        });
    }

    /**
     * 处理单个监听器的 Pending 消息
     *
     * @param ops      Stream 操作对象
     * @param listener 消息监听器
     */
    private void processPendingMessagesForListener(StreamOperations<String, Object, Object> ops, AbstractRedisStreamMessageListener<?> listener) {
        // 获取 Stream 键
        String streamKey = listener.getStreamKey();

        // 获取消费者组的 Pending 消息摘要
        PendingMessagesSummary summary = ops.pending(streamKey, groupName);
        // 如果没有 Pending 消息，则直接返回
        if (summary == null || summary.getTotalPendingMessages() == 0) {
            log.debug("[Pending消息重发] Stream({}) 无 Pending 消息", streamKey);
            return;
        }

        log.info("[Pending消息重发] Stream({}) 总 Pending 消息数: {}", streamKey, summary.getTotalPendingMessages());

        // 遍历每个消费者的 Pending 消息，处理其消息
        Map<String, Long> pendingPerConsumer = summary.getPendingMessagesPerConsumer();
        pendingPerConsumer.forEach((consumerName, count) ->
                processConsumerPendingMessages(ops, streamKey, consumerName, count));
    }

    /**
     * 处理单个消费者的 Pending 消息
     *
     * @param ops          Stream 操作对象
     * @param streamKey    Stream 键
     * @param consumerName 消费者名称
     * @param pendingCount Pending 消息数量
     */
    private void processConsumerPendingMessages(StreamOperations<String, Object, Object> ops, String streamKey, String consumerName, Long pendingCount) {
        // 如果 Pending 消息数为 0，则直接返回
        if (pendingCount == null || pendingCount == 0) {
            return;
        }

        log.info("[Pending消息重发] Stream({}) 消费者({}) Pending 消息数: {}", streamKey, consumerName, pendingCount);

        try {
            // 获取该消费者的所有 Pending 消息详情
            PendingMessages pendingMessages = ops.pending(
                    streamKey,
                    Consumer.from(groupName, consumerName),
                    Range.unbounded(),
                    pendingCount
            );

            // 如果没有 Pending 消息，则直接返回
            if (CollUtil.isEmpty(pendingMessages)) {
                return;
            }

            // 处理每条 Pending 消息
            int resendCount = 0;
            // 获取配置的过期时间，默认 5 分钟
            long expireSeconds = properties.getStream().getPendingMessage().getExpireTime().getSeconds();

            // 遍历每个 Pending 消息，判断是否需要重发
            for (PendingMessage pendingMessage : pendingMessages) {
                // 如果消息过期时间超过配置的过期时间，则需要重发
                if (shouldResend(pendingMessage, expireSeconds)) {
                    resendMessage(ops, streamKey, pendingMessage);
                    resendCount++;
                }
            }

            if (resendCount > 0) {
                log.info("[Pending消息重发] Stream({}) 消费者({}) 重发消息数: {}", streamKey, consumerName, resendCount);
            }

        } catch (Exception e) {
            log.error("[Pending消息重发] 处理消费者({}) 的 Pending 消息异常", consumerName, e);
        }
    }

    /**
     * 判断消息是否应该重发
     *
     * @param pendingMessage Pending 消息对象
     * @param expireSeconds  配置的过期时间（秒）
     * @return 如果消息过期时间超过配置的过期时间，则返回 true，否则返回 false
     */
    private boolean shouldResend(PendingMessage pendingMessage, long expireSeconds) {
        long elapsedSeconds = pendingMessage.getElapsedTimeSinceLastDelivery().getSeconds();
        return elapsedSeconds >= expireSeconds;
    }

    /**
     * 重发单个 Pending 消息
     *
     * @param ops            Stream 操作对象
     * @param streamKey      Stream 键
     * @param pendingMessage Pending 消息对象
     */
    private void resendMessage(StreamOperations<String, Object, Object> ops, String streamKey, PendingMessage pendingMessage) {
        try {
            // 获取 Pending 消息的 ID 字符串
            String messageId = pendingMessage.getIdAsString();

            // 获取原消息内容
            List<MapRecord<String, Object, Object>> records = ops.range(
                    streamKey,
                    Range.of(Range.Bound.inclusive(messageId), Range.Bound.inclusive(messageId))
            );

            // 如果记录为空，说明消息已被删除，ACK 原消息，避免一直重试
            if (CollUtil.isEmpty(records)) {
                log.warn("[Pending消息重发] 消息({}) 不存在，可能已被删除", messageId);
                // ACK 原消息，避免一直重试
                ops.acknowledge(groupName, streamKey, messageId);
                return;
            }

            // 获取原消息记录中的第一个元素（因为范围查询返回的是一个列表）, 但是列表中只有一个元素
            MapRecord<String, Object, Object> originalRecord = records.get(0);

            // 重新投递消息（创建新消息）
            RecordId newRecordId = ops.add(StreamRecords.newRecord()
                    .ofObject(originalRecord.getValue())
                    .withStreamKey(streamKey));

            // 确认原消息
            Long ackCount = ops.acknowledge(groupName, originalRecord);
            log.info("[Pending消息重发] 消息重发成功 - 原消息ID: {}, 新消息ID: {}, ACK结果: {}", messageId, newRecordId, ackCount);

        } catch (Exception e) {
            log.error("[Pending消息重发] 重发消息({}) 失败", pendingMessage.getIdAsString(), e);
        }
    }

    /**
     * 检查功能是否启用
     */
    private boolean isEnabled() {
        return properties.getStream().getPendingMessage().getEnabled();
    }
}
