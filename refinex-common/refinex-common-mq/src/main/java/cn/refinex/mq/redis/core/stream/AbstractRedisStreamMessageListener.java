package cn.refinex.mq.redis.core.stream;

import cn.hutool.core.util.TypeUtil;
import cn.refinex.common.json.utils.JsonUtils;
import cn.refinex.mq.redis.core.RedisMQTemplate;
import cn.refinex.mq.redis.core.interceptor.RedisMessageInterceptor;
import cn.refinex.mq.redis.core.message.AbstractRedisMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Redis Stream 消息监听器抽象类
 *
 * <p>用于实现 Redis Stream 模式的集群消费。在消费者组内，每条消息只会被一个消费者处理，实现负载均衡。</p>
 *
 * <h3>核心功能：</h3>
 * <ul>
 *     <li>自动解析泛型，获取消息类型</li>
 *     <li>自动反序列化 JSON 消息</li>
 *     <li>自动执行拦截器链</li>
 *     <li>自动 ACK 消息确认</li>
 *     <li>提供模板方法，简化业务实现</li>
 * </ul>
 *
 * <h3>工作流程：</h3>
 * <ol>
 *     <li>从 Redis Stream 接收消息</li>
 *     <li>将 JSON 消息体反序列化为具体的消息对象</li>
 *     <li>执行消费前拦截器（consumeMessageBefore）</li>
 *     <li>调用子类实现的 onMessage 方法处理业务逻辑</li>
 *     <li>自动 ACK 确认消息已处理</li>
 *     <li>执行消费后拦截器（consumeMessageAfter）</li>
 * </ol>
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractRedisStreamMessageListener<T extends AbstractRedisStreamMessage> implements StreamListener<String, ObjectRecord<String, String>> {

    /**
     * 消息类型的 Class 对象
     */
    private final Class<T> messageType;

    /**
     * Redis Stream Key
     */
    @Getter
    private final String streamKey;

    /**
     * 消费者组名称，默认使用应用名称
     */
    @Value("${spring.application.name}")
    @Getter
    private String group;

    /**
     * Redis MQ 模板，用于访问拦截器和执行 ACK
     */
    @Setter
    private RedisMQTemplate redisMQTemplate;

    /**
     * 构造函数
     *
     * <p>自动解析泛型类型，获取消息类型和 Stream Key。</p>
     *
     * @throws IllegalStateException 如果无法解析泛型类型
     */
    @SneakyThrows
    protected AbstractRedisStreamMessageListener() {
        this.messageType = getMessageClass();
        this.streamKey = messageType.getDeclaredConstructor().newInstance().getStreamKey();
        log.debug("[Stream监听器] 初始化完成 - StreamKey: {}, 监听器: {}", streamKey, getClass().getSimpleName());
    }

    /**
     * 处理接收到的 Redis Stream 消息
     *
     * <p>该方法由 Spring Data Redis 框架调用，负责消息的反序列化、业务处理、ACK 确认和拦截器执行。</p>
     *
     * @param message Redis Stream 消息记录
     */
    @Override
    public final void onMessage(ObjectRecord<String, String> message) {
        T messageObj = null;
        boolean success = false;

        try {
            // 反序列化消息
            messageObj = JsonUtils.convert(message.getValue(), messageType);
            if (messageObj == null) {
                log.warn("[Stream监听器] 消息反序列化结果为 null - StreamKey: {}, MessageId: {}", streamKey, message.getId());
                // 空消息直接 ACK，避免重复处理
                acknowledgeMessage(message);
                return;
            }

            log.debug("[Stream监听器] 接收到消息 - StreamKey: {}, MessageId: {}, 消息类型: {}", streamKey, message.getId(), messageType.getSimpleName());

            // 执行消费前拦截器
            consumeMessageBefore(messageObj);

            // 处理业务逻辑
            this.onMessage(messageObj);

            // 业务处理成功，ACK 消息
            acknowledgeMessage(message);
            success = true;

            log.debug("[Stream监听器] 消息处理成功 - StreamKey: {}, MessageId: {}", streamKey, message.getId());
        } catch (Exception e) {
            log.error("[Stream监听器] 处理消息异常 - StreamKey: {}, MessageId: {}, 消息类型: {}", streamKey, message.getId(), messageType.getSimpleName(), e);
            // 不 ACK，消息会保留在 Pending 列表，超时后会被重新投递
        } finally {
            // 执行消费后拦截器（即使发生异常也要执行）
            if (messageObj != null) {
                consumeMessageAfter(messageObj);
            }

            // 记录处理结果
            if (!success) {
                log.warn("[Stream监听器] 消息未被确认，将进入 Pending 列表 - StreamKey: {}, MessageId: {}", streamKey, message.getId());
            }
        }
    }

    /**
     * 处理具体的业务消息
     *
     * <p>子类必须实现该方法，处理具体的业务逻辑。</p>
     *
     * <p><strong>返回值和异常处理：</strong></p>
     * <ul>
     *     <li><strong>正常返回：</strong>消息会被自动 ACK，从 Pending 列表移除</li>
     *     <li><strong>抛出异常：</strong>消息不会被 ACK，保留在 Pending 列表，超时后重试</li>
     * </ul>
     *
     * <p><strong>幂等性要求：</strong></p>
     * <ul>
     *     <li>由于可能重复消费，该方法必须保证幂等性</li>
     *     <li>建议使用唯一标识（如订单号）进行去重</li>
     *     <li>可以在数据库中记录处理状态，避免重复处理</li>
     * </ul>
     *
     * <p><strong>注意事项：</strong></p>
     * <ul>
     *     <li>该方法应该尽快返回，避免阻塞其他消息的处理</li>
     *     <li>如果处理耗时较长，建议异步处理或使用线程池</li>
     *     <li>根据业务需求决定是否抛出异常触发重试</li>
     *     <li>重试次数过多的消息应该转入死信队列</li>
     * </ul>
     *
     * @param message 已反序列化的消息对象
     * @throws Exception 抛出异常会导致消息不被 ACK，超时后会重试
     */
    public abstract void onMessage(T message);

    /**
     * 确认消息已处理
     *
     * <p>发送 ACK 到 Redis，将消息从 Pending 列表中移除。</p>
     *
     * @param message Redis Stream 消息记录
     */
    private void acknowledgeMessage(ObjectRecord<String, String> message) {
        try {
            Long ackCount = redisMQTemplate.getRedisTemplate()
                    .opsForStream()
                    .acknowledge(group, message);

            if (ackCount > 0) {
                log.debug("[Stream监听器] 消息 ACK 成功 - StreamKey: {}, MessageId: {}, ACK数量: {}", streamKey, message.getId(), ackCount);
            } else {
                log.warn("[Stream监听器] 消息 ACK 返回 0，可能已被确认 - StreamKey: {}, MessageId: {}", streamKey, message.getId());
            }
        } catch (Exception e) {
            log.error("[Stream监听器] 消息 ACK 失败 - StreamKey: {}, MessageId: {}", streamKey, message.getId(), e);
            // ACK 失败不影响业务处理结果，消息会在超时后被重新投递
        }
    }

    /**
     * 通过解析类的泛型，获取消息类型
     *
     * @return 消息类型的 Class 对象
     * @throws IllegalStateException 如果无法解析泛型类型
     */
    @SuppressWarnings("unchecked")
    private Class<T> getMessageClass() {
        Type type = TypeUtil.getTypeArgument(getClass(), 0);
        if (type == null) {
            throw new IllegalStateException(String.format(
                    "监听器 %s 必须指定泛型类型，例如: extends AbstractRedisStreamMessageListener<YourMessage>",
                    getClass().getName()));
        }
        return (Class<T>) type;
    }

    /**
     * 执行消费前拦截器
     *
     * <p>在消息处理前调用，用于执行一些预处理逻辑，如日志记录、权限验证等。</p>
     *
     * @param message 待处理的消息对象，必须是 {@link AbstractRedisMessage} 类型的实例
     */
    private void consumeMessageBefore(AbstractRedisMessage message) {
        if (redisMQTemplate == null) {
            return;
        }

        // 获取拦截器列表
        List<RedisMessageInterceptor> interceptors = redisMQTemplate.getInterceptors();

        // 正序执行
        for (RedisMessageInterceptor interceptor : interceptors) {
            try {
                interceptor.consumeMessageBefore(message);
            } catch (Exception e) {
                log.error("[Stream监听器] 执行拦截器 consumeMessageBefore 异常 - 拦截器: {}", interceptor.getClass().getSimpleName(), e);
            }
        }
    }

    /**
     * 执行消费后拦截器
     *
     * <p>在消息处理后调用，用于执行一些后处理逻辑，如日志记录、资源清理等。</p>
     *
     * @param message 已处理的消息对象，必须是 {@link AbstractRedisMessage} 类型的实例
     */
    private void consumeMessageAfter(AbstractRedisMessage message) {
        if (redisMQTemplate == null) {
            return;
        }

        // 获取拦截器列表
        List<RedisMessageInterceptor> interceptors = redisMQTemplate.getInterceptors();

        // 倒序执行
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            try {
                interceptors.get(i).consumeMessageAfter(message);
            } catch (Exception e) {
                log.error("[Stream监听器] 执行拦截器 consumeMessageAfter 异常 - 拦截器: {}", interceptors.get(i).getClass().getSimpleName(), e);
            }
        }
    }
}
