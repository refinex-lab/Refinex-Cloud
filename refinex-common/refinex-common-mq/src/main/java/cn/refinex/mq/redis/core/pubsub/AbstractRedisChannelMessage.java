package cn.refinex.mq.redis.core.pubsub;

import cn.refinex.mq.redis.core.message.AbstractRedisMessage;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Redis Pub/Sub Channel 消息抽象类
 *
 * <p>基于 Redis Pub/Sub 机制实现的广播消息基类。Pub/Sub 模式适用于需要将消息广播给所有订阅者的场景。</p>
 *
 * <h3>特性说明：</h3>
 * <ul>
 *     <li><strong>广播模式：</strong>消息会发送给所有订阅该 Channel 的消费者</li>
 *     <li><strong>无持久化：</strong>消息不会被 Redis 持久化存储</li>
 *     <li><strong>无法保证送达：</strong>如果没有消费者在线，消息会丢失</li>
 *     <li><strong>实时性强：</strong>消息延迟极低，适合实时通知场景</li>
 * </ul>
 *
 * <h3>适用场景：</h3>
 * <ul>
 *     <li>缓存失效通知（所有节点需要刷新缓存）</li>
 *     <li>配置变更通知（所有节点需要重新加载配置）</li>
 *     <li>实时状态同步（多节点状态同步）</li>
 *     <li>系统广播通知（系统级的通知消息）</li>
 * </ul>
 *
 * @author 芋道源码
 * @author Refinex
 * @see AbstractRedisChannelMessageListener
 * @since 1.0.0
 */
public abstract class AbstractRedisChannelMessage extends AbstractRedisMessage {

    /**
     * 获取 Redis Pub/Sub 的 Channel 名称
     *
     * <p>默认使用类的简单名称（不含包名）作为 Channel 名称。这样可以确保相同类型的消息使用相同的 Channel。</p>
     *
     * <p>如需自定义 Channel 名称，可以重写此方法：</p>
     * <pre>{@code
     * @Override
     * public String getChannel() {
     *     return "custom-channel-name";
     * }
     * }</pre>
     *
     * @return Channel 名称，用于 Redis PUBLISH 和 SUBSCRIBE 命令
     */
    @JsonIgnore  // 避免序列化到消息体中，Channel 信息由框架管理
    public String getChannel() {
        return getClass().getSimpleName();
    }
}
