package cn.refinex.mq.redis.core.stream;

import cn.refinex.mq.redis.core.message.AbstractRedisMessage;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Redis Stream 消息抽象类
 *
 * <p>基于 Redis Stream 机制实现的消息队列基类。Stream 模式适用于需要可靠消息传递和集群消费的场景。</p>
 *
 * <h3>特性说明：</h3>
 * <ul>
 *     <li><strong>持久化：</strong>消息会被 Redis 持久化存储，不会丢失</li>
 *     <li><strong>消费者组：</strong>支持消费者组模式，实现负载均衡</li>
 *     <li><strong>消息确认：</strong>支持 ACK 机制，确保消息被正确处理</li>
 *     <li><strong>消息重投：</strong>未确认的消息可以被重新投递</li>
 *     <li><strong>消息历史：</strong>可以回溯历史消息</li>
 * </ul>
 *
 * <h3>适用场景：</h3>
 * <ul>
 *     <li>订单处理（需要可靠传递，不能丢失）</li>
 *     <li>异步任务（如邮件发送、短信通知）</li>
 *     <li>事件溯源（需要保留历史事件）</li>
 *     <li>日志收集（需要持久化和批量处理）</li>
 *     <li>数据同步（需要确保数据一致性）</li>
 * </ul>
 *
 * @author 芋道源码
 * @author Refinex
 * @see AbstractRedisStreamMessageListener
 * @since 1.0.0
 */
public abstract class AbstractRedisStreamMessage extends AbstractRedisMessage {

    /**
     * 获取 Redis Stream 的 Key 名称
     *
     * <p>默认使用类的简单名称（不含包名）作为 Stream Key。这样可以确保相同类型的消息存储在同一个 Stream 中。</p>
     *
     * <p>如需自定义 Stream Key，可以重写此方法：</p>
     * <pre>{@code
     * @Override
     * public String getStreamKey() {
     *     return "custom-stream-key";
     * }
     * }</pre>
     *
     * @return Stream Key，用于 Redis Stream 的标识
     */
    @JsonIgnore  // 避免序列化到消息体中，Stream Key 信息由框架管理
    public String getStreamKey() {
        return getClass().getSimpleName();
    }
}
