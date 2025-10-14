package cn.refinex.mq.redis.core;

import cn.refinex.common.exception.SystemException;
import cn.refinex.common.json.utils.JsonUtils;
import cn.refinex.mq.redis.core.interceptor.RedisMessageInterceptor;
import cn.refinex.mq.redis.core.message.AbstractRedisMessage;
import cn.refinex.mq.redis.core.pubsub.AbstractRedisChannelMessage;
import cn.refinex.mq.redis.core.stream.AbstractRedisStreamMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Redis MQ 操作模板类
 *
 * <p>
 * Redis 消息队列的核心操作类，提供统一的消息发送接口，
 * 支持 Pub/Sub 和 Stream 两种消息模式，并集成拦截器机制。
 * </p>
 *
 * <h3>支持的消息模式：</h3>
 * <ul>
 *     <li><strong>Pub/Sub 模式：</strong>广播消息，所有订阅者都会收到</li>
 *     <li><strong>Stream 模式：</strong>可靠消息队列，支持消费者组和负载均衡</li>
 * </ul>
 *
 * <h3>核心功能：</h3>
 * <ul>
 *     <li>消息发送（支持两种模式）</li>
 *     <li>消息序列化（JSON 格式）</li>
 *     <li>拦截器链执行</li>
 *     <li>异常处理和日志记录</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * @Service
 * public class MessageService {
 *
 *     @Autowired
 *     private RedisMQTemplate redisMQTemplate;
 *
 *     // 发送 Pub/Sub 广播消息
 *     public void sendBroadcast() {
 *         CacheRefreshMessage message = new CacheRefreshMessage();
 *         message.setCacheKey("user:1001");
 *         redisMQTemplate.send(message);
 *         // 所有订阅者都会收到
 *     }
 *
 *     // 发送 Stream 可靠消息
 *     public RecordId sendReliable() {
 *         OrderCreatedMessage message = new OrderCreatedMessage();
 *         message.setOrderId(1001L);
 *         message.addHeader("traceId", "abc-123");
 *         RecordId recordId = redisMQTemplate.send(message);
 *         // 返回消息记录ID，可用于追踪
 *         return recordId;
 *     }
 * }
 * }</pre>
 *
 * <h3>拦截器机制：</h3>
 * <p>拦截器在消息发送前后自动执行，实现横切关注点：</p>
 * <ul>
 *     <li><strong>发送前：</strong>按注册顺序正向执行 sendMessageBefore</li>
 *     <li><strong>发送后：</strong>按注册顺序反向执行 sendMessageAfter</li>
 *     <li><strong>执行保证：</strong>即使发送失败，sendMessageAfter 也会在 finally 中执行</li>
 * </ul>
 *
 * <h3>拦截器应用场景：</h3>
 * <pre>{@code
 * @Component
 * @Order(1)
 * public class TenantInterceptor implements RedisMessageInterceptor {
 *     @Override
 *     public void sendMessageBefore(AbstractRedisMessage message) {
 *         // 添加租户ID到消息头
 *         Long tenantId = TenantContextHolder.getTenantId();
 *         if (tenantId != null) {
 *             message.addHeader("tenantId", String.valueOf(tenantId));
 *         }
 *     }
 * }
 * }</pre>
 *
 * <h3>两种消息模式对比：</h3>
 * <table border="1">
 *   <tr>
 *     <th>特性</th>
 *     <th>Pub/Sub</th>
 *     <th>Stream</th>
 *   </tr>
 *   <tr>
 *     <td>持久化</td>
 *     <td>❌ 不持久化</td>
 *     <td>✅ 持久化存储</td>
 *   </tr>
 *   <tr>
 *     <td>消息可靠性</td>
 *     <td>❌ 可能丢失</td>
 *     <td>✅ 可靠传递</td>
 *   </tr>
 *   <tr>
 *     <td>消费模式</td>
 *     <td>广播（所有订阅者）</td>
 *     <td>负载均衡（消费者组）</td>
 *   </tr>
 *   <tr>
 *     <td>消息确认</td>
 *     <td>❌ 不支持</td>
 *     <td>✅ 支持 ACK</td>
 *   </tr>
 *   <tr>
 *     <td>重试机制</td>
 *     <td>❌ 不支持</td>
 *     <td>✅ 支持重投</td>
 *   </tr>
 *   <tr>
 *     <td>适用场景</td>
 *     <td>缓存刷新、配置更新</td>
 *     <td>订单处理、异步任务</td>
 *   </tr>
 * </table>
 *
 * <h3>注意事项：</h3>
 * <ul>
 *     <li>消息会被序列化为 JSON，避免在消息中存储不可序列化的对象</li>
 *     <li>Pub/Sub 模式不保证消息送达，适合可容忍丢失的场景</li>
 *     <li>Stream 模式会占用 Redis 内存，需要定期清理历史消息</li>
 *     <li>拦截器执行异常会中断消息发送，需谨慎处理</li>
 *     <li>大量消息发送时注意 Redis 性能和网络带宽</li>
 * </ul>
 *
 * @author 芋道源码
 * @author Refinex
 * @see AbstractRedisChannelMessage
 * @see AbstractRedisStreamMessage
 * @see RedisMessageInterceptor
 * @since 1.0.0
 */
@Slf4j
public class RedisMQTemplate {

    /**
     * Spring Data Redis 模板，用于底层 Redis 操作
     */
    @Getter
    private final RedisTemplate<String, ?> redisTemplate;

    /**
     * 消息拦截器列表
     */
    @Getter
    private final List<RedisMessageInterceptor> interceptors = new ArrayList<>();

    /**
     * 构造函数
     *
     * @param redisTemplate Redis 模板对象
     */
    public RedisMQTemplate(RedisTemplate<String, ?> redisTemplate) {
        this.redisTemplate = redisTemplate;
        log.info("[RedisMQTemplate] 初始化完成");
    }

    /**
     * 发送 Pub/Sub 广播消息
     *
     * <p>将消息发布到 Redis 的 Pub/Sub Channel，所有订阅该 Channel 的消费者都会收到消息。</p>
     *
     * <h4>工作流程：</h4>
     * <ol>
     *     <li>执行发送前拦截器</li>
     *     <li>将消息序列化为 JSON</li>
     *     <li>发布到指定 Channel</li>
     *     <li>执行发送后拦截器</li>
     * </ol>
     *
     * <h4>特性说明：</h4>
     * <ul>
     *     <li><strong>广播模式：</strong>所有订阅者都会收到消息</li>
     *     <li><strong>不持久化：</strong>消息不会被 Redis 存储，消费者离线会丢失</li>
     *     <li><strong>无返回值：</strong>无法确认消息是否被消费</li>
     *     <li><strong>实时性好：</strong>延迟极低，适合实时通知</li>
     * </ul>
     *
     * <h4>适用场景：</h4>
     * <ul>
     *     <li>缓存失效通知：通知所有节点刷新本地缓存</li>
     *     <li>配置变更：通知所有节点重新加载配置</li>
     *     <li>状态同步：多节点之间的状态同步</li>
     * </ul>
     *
     * <h4>使用示例：</h4>
     * <pre>{@code
     * CacheRefreshMessage message = new CacheRefreshMessage();
     * message.setCacheKey("user:1001");
     * message.setOperation("refresh");
     * redisMQTemplate.send(message);
     * }</pre>
     *
     * @param message 要发送的消息对象，必须继承自 AbstractRedisChannelMessage
     * @param <T>     消息类型
     * @throws IllegalArgumentException 如果消息为 null
     * @throws SystemException          如果发送失败（拦截器异常或 Redis 异常）
     */
    public <T extends AbstractRedisChannelMessage> void send(T message) {
        if (message == null) {
            throw new IllegalArgumentException("发送的消息不能为 null");
        }

        String channel = message.getChannel();

        try {
            log.debug("[RedisMQTemplate] 准备发送 Pub/Sub 消息 - Channel: {}, 消息类型: {}", channel, message.getClass().getSimpleName());

            // 执行发送前拦截器
            sendMessageBefore(message);

            // 序列化并发送消息
            String jsonMessage = JsonUtils.toJson(message);
            redisTemplate.convertAndSend(channel, jsonMessage);

            log.info("[RedisMQTemplate] Pub/Sub 消息发送成功 - Channel: {}, 消息类型: {}", channel, message.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("[RedisMQTemplate] Pub/Sub 消息发送失败 - Channel: {}, 消息类型: {}", channel, message.getClass().getSimpleName(), e);
            throw new SystemException("Pub/Sub 消息发送失败", e);
        } finally {
            // 执行发送后拦截器（无论成功或失败都执行）
            sendMessageAfter(message);
        }
    }

    /**
     * 发送 Stream 可靠消息
     *
     * <p>将消息添加到 Redis Stream，支持持久化存储、消费者组和负载均衡。</p>
     *
     * <h4>工作流程：</h4>
     * <ol>
     *     <li>执行发送前拦截器</li>
     *     <li>将消息序列化为 JSON</li>
     *     <li>添加到指定 Stream</li>
     *     <li>返回消息记录 ID</li>
     *     <li>执行发送后拦截器</li>
     * </ol>
     *
     * <h4>特性说明：</h4>
     * <ul>
     *     <li><strong>持久化：</strong>消息会被 Redis 持久化存储</li>
     *     <li><strong>负载均衡：</strong>消费者组内的消费者负载均衡消费</li>
     *     <li><strong>消息确认：</strong>支持 ACK 机制，确保消息被正确处理</li>
     *     <li><strong>重试机制：</strong>未 ACK 的消息会被重新投递</li>
     *     <li><strong>可追踪：</strong>返回消息 ID，可用于追踪和查询</li>
     * </ul>
     *
     * <h4>适用场景：</h4>
     * <ul>
     *     <li>订单处理：需要可靠传递，不能丢失</li>
     *     <li>异步任务：如邮件发送、短信通知</li>
     *     <li>数据同步：确保数据一致性</li>
     *     <li>事件溯源：保留历史事件记录</li>
     * </ul>
     *
     * <h4>使用示例：</h4>
     * <pre>{@code
     * OrderCreatedMessage message = new OrderCreatedMessage();
     * message.setOrderId(1001L);
     * message.setAmount(new BigDecimal("299.00"));
     * message.addHeader("traceId", "abc-123");
     *
     * RecordId recordId = redisMQTemplate.send(message);
     * log.info("消息发送成功，ID: {}", recordId);
     * }</pre>
     *
     * @param message 要发送的消息对象，必须继承自 AbstractRedisStreamMessage
     * @param <T>     消息类型
     * @return 消息记录的 ID，可用于追踪和查询
     * @throws IllegalArgumentException 如果消息为 null
     * @throws SystemException          如果发送失败（拦截器异常或 Redis 异常）
     */
    public <T extends AbstractRedisStreamMessage> RecordId send(T message) {
        if (message == null) {
            throw new IllegalArgumentException("发送的消息不能为 null");
        }

        String streamKey = message.getStreamKey();
        RecordId recordId = null;

        try {
            log.debug("[RedisMQTemplate] 准备发送 Stream 消息 - StreamKey: {}, 消息类型: {}",
                    streamKey, message.getClass().getSimpleName());

            // 执行发送前拦截器
            sendMessageBefore(message);

            // 序列化并发送消息
            String jsonMessage = JsonUtils.toJson(message);
            recordId = redisTemplate.opsForStream().add(
                    StreamRecords.newRecord()
                            .ofObject(jsonMessage)
                            .withStreamKey(streamKey));

            log.info("[RedisMQTemplate] Stream 消息发送成功 - StreamKey: {}, MessageId: {}, 消息类型: {}",
                    streamKey, recordId, message.getClass().getSimpleName());

            return recordId;

        } catch (Exception e) {
            log.error("[RedisMQTemplate] Stream 消息发送失败 - StreamKey: {}, 消息类型: {}",
                    streamKey, message.getClass().getSimpleName(), e);
            throw new SystemException("Stream 消息发送失败", e);
        } finally {
            // 执行发送后拦截器（无论成功或失败都执行）
            sendMessageAfter(message);
        }
    }

    /**
     * 添加消息拦截器
     *
     * <p>拦截器会按添加顺序执行，建议使用 Spring 的 @Order 注解控制顺序。</p>
     *
     * <h4>拦截器执行顺序：</h4>
     * <ul>
     *     <li><strong>发送前：</strong>按添加顺序正向执行</li>
     *     <li><strong>发送后：</strong>按添加顺序反向执行（类似 AOP）</li>
     * </ul>
     *
     * <h4>使用示例：</h4>
     * <pre>{@code
     * @Component
     * @Order(1)
     * public class TenantInterceptor implements RedisMessageInterceptor {
     *     // 实现拦截器方法
     * }
     *
     * // 拦截器会自动注册，无需手动添加
     * }</pre>
     *
     * @param interceptor 要添加的拦截器
     */
    public void addInterceptor(RedisMessageInterceptor interceptor) {
        if (interceptor != null) {
            interceptors.add(interceptor);
            log.debug("[RedisMQTemplate] 添加消息拦截器: {}", interceptor.getClass().getSimpleName());
        }
    }

    /**
     * 执行发送前拦截器
     *
     * <p>
     * 按注册顺序正向执行所有拦截器的 sendMessageBefore 方法。
     * 任何拦截器抛出的异常都会中断消息发送。
     * </p>
     *
     * @param message 消息对象
     */
    private void sendMessageBefore(AbstractRedisMessage message) {
        for (RedisMessageInterceptor interceptor : interceptors) {
            try {
                interceptor.sendMessageBefore(message);
            } catch (Exception e) {
                log.error("[RedisMQTemplate] 执行拦截器 sendMessageBefore 异常 - 拦截器: {}", interceptor.getClass().getSimpleName(), e);
                throw new SystemException("拦截器执行失败", e);
            }
        }
    }

    /**
     * 执行发送后拦截器
     *
     * <p>
     * 按注册顺序反向执行所有拦截器的 sendMessageAfter 方法。
     * 该方法在 finally 块中执行，确保即使发送失败也能执行清理逻辑。
     * </p>
     *
     * <p>拦截器中的异常会被捕获并记录日志，不会影响其他拦截器的执行。</p>
     *
     * @param message 消息对象
     */
    private void sendMessageAfter(AbstractRedisMessage message) {
        // 倒序执行，类似 AOP 的环绕通知
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            try {
                interceptors.get(i).sendMessageAfter(message);
            } catch (Exception e) {
                log.error("[RedisMQTemplate] 执行拦截器 sendMessageAfter 异常 - 拦截器: {}", interceptors.get(i).getClass().getSimpleName(), e);
                // 不抛出异常，避免影响其他拦截器
            }
        }
    }
}
