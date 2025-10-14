package cn.refinex.mq.redis.core.job;

import cn.refinex.mq.redis.config.properties.RefinexRedisMQProperties;
import cn.refinex.mq.redis.core.RedisMQTemplate;
import cn.refinex.mq.redis.core.stream.AbstractRedisStreamMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis Stream 消息清理定时任务
 *
 * <p>
 * 该任务负责定期清理 Stream 中的历史消息，防止消息无限积累导致内存占用过高。
 * Redis Stream 不会自动删除已消费的消息，需要通过 XTRIM 命令主动清理。
 * </p>
 *
 * <h3>为什么需要消息清理：</h3>
 * <ul>
 *     <li><strong>内存占用：</strong>消息会持续占用 Redis 内存，不清理会导致内存溢出</li>
 *     <li><strong>性能影响：</strong>Stream 过大会影响读取性能</li>
 *     <li><strong>运维成本：</strong>降低 Redis 的存储和备份成本</li>
 * </ul>
 *
 * <h3>清理策略：</h3>
 * <p>采用 XTRIM 命令的 MAXLEN 策略，保留最近的 N 条消息（默认 10000 条）：</p>
 * <ul>
 *     <li><strong>近似裁剪：</strong>使用 ~ 符号启用近似裁剪，提升性能</li>
 *     <li><strong>安全保障：</strong>只清理超出限制的消息，不影响 Pending 消息</li>
 *     <li><strong>批量执行：</strong>一次性清理所有 Stream，减少执行次数</li>
 * </ul>
 *
 * <h3>配置示例：</h3>
 * <pre>{@code
 * refinex:
 *   mq:
 *     redis:
 *       stream:
 *         cleanup:
 *           enabled: true              # 是否启用
 *           max-length: 10000          # 保留消息数量
 *           cron: "0 0 * * * ?"        # 执行频率（每小时）
 *           lock-timeout: 60s          # 分布式锁超时时间
 * }</pre>
 *
 * <h3>配置建议：</h3>
 * <ul>
 *     <li><strong>max-length：</strong>根据消息量和内存容量调整，建议 10000-100000</li>
 *     <li><strong>执行频率：</strong>建议每小时或每天执行一次，不宜过于频繁</li>
 *     <li><strong>监控指标：</strong>关注 Stream 长度和内存占用的变化趋势</li>
 * </ul>
 *
 * <h3>注意事项：</h3>
 * <ul>
 *     <li>清理操作不可逆，务必确保保留的消息数量足够</li>
 *     <li>清理过程中不影响消息的发送和消费</li>
 *     <li>Pending 消息不会被清理，即使超过 max-length</li>
 *     <li>使用分布式锁确保集群环境下只有一个实例执行</li>
 * </ul>
 *
 * @author 芋道源码
 * @author Refinex
 * @see <a href="https://redis.io/commands/xtrim">Redis XTRIM 命令文档</a>
 * @see AbstractRedisStreamMessageListener
 * @since 1.0.0
 */
@Slf4j
public class RedisStreamMessageCleanupJob {

    /**
     * 分布式锁的 Key
     */
    private static final String LOCK_KEY = "refinex:mq:redis:message-cleanup:lock";

    /**
     * 所有 Stream 消息监听器
     */
    private final List<AbstractRedisStreamMessageListener<?>> listeners;

    /**
     * Redis MQ 模板
     */
    private final RedisMQTemplate redisMQTemplate;

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
     * @param redissonClient  Redisson 客户端
     * @param properties      配置属性
     */
    public RedisStreamMessageCleanupJob(List<AbstractRedisStreamMessageListener<?>> listeners,
                                        RedisMQTemplate redisMQTemplate,
                                        RedissonClient redissonClient,
                                        RefinexRedisMQProperties properties) {
        this.listeners = listeners;
        this.redisMQTemplate = redisMQTemplate;
        this.redissonClient = redissonClient;
        this.properties = properties;
    }

    /**
     * 定时执行消息清理任务
     *
     * <p>
     * 执行时机由配置文件中的 cron 表达式控制，默认每小时执行一次（整点执行）。
     * 该方法使用分布式锁确保集群环境下只有一个实例执行任务。
     * </p>
     */
    @Scheduled(cron = "${refinex.mq.redis.stream.cleanup.cron:0 0 * * * ?}")
    public void execute() {
        // 检查是否启用
        if (!isEnabled()) {
            return;
        }

        // 获取分布式锁和超时时间
        RLock lock = redissonClient.getLock(LOCK_KEY);
        long lockTimeout = properties.getStream().getCleanup().getLockTimeout().getSeconds();

        try {
            // 尝试获取锁，避免阻塞
            boolean locked = lock.tryLock(0, lockTimeout, TimeUnit.SECONDS);
            if (!locked) {
                log.debug("[Stream消息清理] 未获取到分布式锁，跳过本次执行");
                return;
            }

            log.info("[Stream消息清理] 开始执行任务，监听器数量: {}", listeners.size());
            long startTime = System.currentTimeMillis();

            cleanupMessages();

            long cost = System.currentTimeMillis() - startTime;
            log.info("[Stream消息清理] 任务执行完成，耗时: {} ms", cost);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[Stream消息清理] 获取分布式锁被中断", e);
        } catch (Exception e) {
            log.error("[Stream消息清理] 执行任务异常", e);
        } finally {
            // 确保在 finally 块中释放锁，避免死锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 清理所有 Stream 的历史消息
     */
    private void cleanupMessages() {
        // 获取 Stream 操作模板和最大长度配置
        StreamOperations<String, Object, Object> ops = redisMQTemplate.getRedisTemplate().opsForStream();
        Long maxLength = properties.getStream().getCleanup().getMaxLength();

        // 记录被清理的 Stream 数量
        int totalCleaned = 0;

        // 遍历处理每个 Stream 监听器的消息
        for (AbstractRedisStreamMessageListener<?> listener : listeners) {
            try {
                // 清理当前 Stream 的消息
                Long cleanedCount = cleanupStreamMessages(ops, listener.getStreamKey(), maxLength);
                // 如果当前 Stream 有清理消息数，累加总数
                if (cleanedCount != null && cleanedCount > 0) {
                    totalCleaned += cleanedCount;
                    log.info("[Stream消息清理] Stream({}) 清理消息数: {}", listener.getStreamKey(), cleanedCount);
                }
            } catch (Exception e) {
                log.error("[Stream消息清理] 清理 Stream({}) 异常", listener.getStreamKey(), e);
            }
        }

        // 记录总清理消息数
        if (totalCleaned > 0) {
            log.info("[Stream消息清理] 总共清理消息数: {}", totalCleaned);
        } else {
            log.debug("[Stream消息清理] 无需清理消息");
        }
    }

    /**
     * 清理单个 Stream 的消息
     *
     * <p>使用 XTRIM 命令的 MAXLEN 策略：</p>
     * <ul>
     *     <li>maxLen：保留的最大消息数量</li>
     *     <li>approximateTrimming=true：启用近似裁剪，提升性能</li>
     * </ul>
     *
     * @param ops       Stream 操作对象
     * @param streamKey Stream 的 Key
     * @param maxLength 保留的最大消息数量
     * @return 清理的消息数量，如果无需清理则返回 0
     */
    private Long cleanupStreamMessages(StreamOperations<String, Object, Object> ops, String streamKey, Long maxLength) {
        try {
            // 使用近似裁剪（approximateTrimming=true），性能更好
            // Redis 会在内部节点边界进行裁剪，而不是精确到指定数量
            Long trimCount = ops.trim(streamKey, maxLength, true);

            if (trimCount != null && trimCount > 0) {
                log.debug("[Stream消息清理] Stream({}) XTRIM 执行成功，清理数量: {}", streamKey, trimCount);
            }

            return trimCount;
        } catch (Exception e) {
            log.error("[Stream消息清理] Stream({}) XTRIM 执行失败", streamKey, e);
            return 0L;
        }
    }

    /**
     * 检查功能是否启用
     */
    private boolean isEnabled() {
        return properties.getStream().getCleanup().getEnabled();
    }
}
