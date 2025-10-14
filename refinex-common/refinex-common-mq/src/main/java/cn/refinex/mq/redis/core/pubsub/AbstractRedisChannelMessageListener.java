package cn.refinex.mq.redis.core.pubsub;

import cn.hutool.core.util.TypeUtil;
import cn.refinex.common.json.utils.JsonUtils;
import cn.refinex.mq.redis.core.RedisMQTemplate;
import cn.refinex.mq.redis.core.interceptor.RedisMessageInterceptor;
import cn.refinex.mq.redis.core.message.AbstractRedisMessage;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Redis Pub/Sub Channel 消息监听器抽象类
 *
 * <p>用于实现 Redis Pub/Sub 模式的广播消费。所有订阅同一 Channel 的监听器都会收到消息，实现广播效果。</p>
 *
 * <h3>核心功能：</h3>
 * <ul>
 *     <li>自动解析泛型，获取消息类型</li>
 *     <li>自动反序列化 JSON 消息</li>
 *     <li>自动执行拦截器链</li>
 *     <li>提供模板方法，简化业务实现</li>
 * </ul>
 *
 * <h3>工作流程：</h3>
 * <ol>
 *     <li>接收 Redis Pub/Sub 的原始消息</li>
 *     <li>将 JSON 消息体反序列化为具体的消息对象</li>
 *     <li>执行消费前拦截器（consumeMessageBefore）</li>
 *     <li>调用子类实现的 onMessage 方法处理业务逻辑</li>
 *     <li>执行消费后拦截器（consumeMessageAfter）</li>
 * </ol>
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractRedisChannelMessageListener<T extends AbstractRedisChannelMessage> implements MessageListener {

    /**
     * 消息类型的 Class 对象
     */
    private final Class<T> messageType;

    /**
     * Redis Channel 名称
     */
    private final String channel;

    /**
     * Redis MQ 模板，用于访问拦截器
     */
    @Setter
    private RedisMQTemplate redisMQTemplate;

    /**
     * 构造函数
     *
     * <p>自动解析泛型类型，获取消息类型和 Channel 名称。</p>
     *
     * @throws IllegalStateException 如果无法解析泛型类型
     */
    @SneakyThrows
    protected AbstractRedisChannelMessageListener() {
        this.messageType = getMessageClass();
        this.channel = messageType.getDeclaredConstructor().newInstance().getChannel();
        log.debug("[Channel监听器] 初始化完成 - Channel: {}, 监听器: {}", channel, getClass().getSimpleName());
    }

    /**
     * 获取订阅的 Redis Channel 名称
     *
     * @return Channel 名称
     */
    public final String getChannel() {
        return channel;
    }

    /**
     * 处理接收到的 Redis 消息
     *
     * <p>该方法由 Spring Data Redis 框架调用，负责消息的反序列化和拦截器的执行。</p>
     *
     * @param message Redis 原始消息对象
     * @param pattern 订阅模式（本框架未使用）
     */
    @Override
    public final void onMessage(Message message, byte[] pattern) {
        T messageObj = null;
        try {
            // 反序列化消息
            messageObj = JsonUtils.convert(message.getBody(), messageType);
            if (messageObj == null) {
                log.warn("[Channel监听器] 消息反序列化结果为 null - Channel: {}", channel);
                return;
            }

            log.debug("[Channel监听器] 接收到消息 - Channel: {}, 消息类型: {}", channel, messageType.getSimpleName());

            // 执行消费前拦截器
            consumeMessageBefore(messageObj);

            // 处理业务逻辑
            this.onMessage(messageObj);

        } catch (Exception e) {
            log.error("[Channel监听器] 处理消息异常 - Channel: {}, 消息类型: {}", channel, messageType.getSimpleName(), e);
            // Pub/Sub 模式没有重试机制，异常消息会丢失
        } finally {
            // 执行消费后拦截器（即使发生异常也要执行）
            if (messageObj != null) {
                consumeMessageAfter(messageObj);
            }
        }
    }

    /**
     * 处理具体的业务消息
     *
     * <p>子类必须实现该方法，处理具体的业务逻辑。</p>
     *
     * <p><strong>注意事项：</strong></p>
     * <ul>
     *     <li>该方法应该快速返回，避免阻塞其他消息的处理</li>
     *     <li>如果处理耗时较长，建议异步处理或使用线程池</li>
     *     <li>建议捕获异常并记录日志，因为 Pub/Sub 不支持重试</li>
     *     <li>注意幂等性，因为所有订阅者都会收到消息</li>
     * </ul>
     *
     * @param message 已反序列化的消息对象
     */
    public abstract void onMessage(T message);

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
                    "监听器 %s 必须指定泛型类型，例如: extends AbstractRedisChannelMessageListener<YourMessage>",
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

        // 获取所有注册的拦截器
        List<RedisMessageInterceptor> interceptors = redisMQTemplate.getInterceptors();

        // 正序执行
        for (RedisMessageInterceptor interceptor : interceptors) {
            try {
                interceptor.consumeMessageBefore(message);
            } catch (Exception e) {
                log.error("[Channel监听器] 执行拦截器 consumeMessageBefore 异常 - 拦截器: {}", interceptor.getClass().getSimpleName(), e);
            }
        }
    }

    /**
     * 执行消费后拦截器
     *
     * <p>在消息处理后调用，用于执行一些后处理逻辑，如资源清理、日志记录等。</p>
     *
     * @param message 待处理的消息对象，必须是 {@link AbstractRedisMessage} 类型的实例
     */
    private void consumeMessageAfter(AbstractRedisMessage message) {
        if (redisMQTemplate == null) {
            return;
        }

        // 获取所有注册的拦截器
        List<RedisMessageInterceptor> interceptors = redisMQTemplate.getInterceptors();

        // 倒序执行
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            try {
                interceptors.get(i).consumeMessageAfter(message);
            } catch (Exception e) {
                log.error("[Channel监听器] 执行拦截器 consumeMessageAfter 异常 - 拦截器: {}", interceptors.get(i).getClass().getSimpleName(), e);
            }
        }
    }
}
