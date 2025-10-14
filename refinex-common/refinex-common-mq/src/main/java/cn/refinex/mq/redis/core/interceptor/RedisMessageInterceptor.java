package cn.refinex.mq.redis.core.interceptor;

import cn.refinex.mq.redis.core.RedisMQTemplate;
import cn.refinex.mq.redis.core.message.AbstractRedisMessage;

/**
 * Redis 消息拦截器接口
 *
 * <p>消息拦截器提供了一个插件化的机制，允许在消息发送和消费的关键节点注入自定义逻辑。通过实现该接口，可以在不修改核心代码的情况下，扩展消息队列的功能。</p>
 *
 * <h3>拦截器执行时机：</h3>
 * <ul>
 *     <li><strong>发送前（sendMessageBefore）：</strong>消息即将发送到 Redis 之前执行</li>
 *     <li><strong>发送后（sendMessageAfter）：</strong>消息成功发送到 Redis 之后执行</li>
 *     <li><strong>消费前（consumeMessageBefore）：</strong>消费者接收到消息，准备处理之前执行</li>
 *     <li><strong>消费后（consumeMessageAfter）：</strong>消费者处理完消息之后执行</li>
 * </ul>
 *
 * <h3>拦截器执行顺序：</h3>
 * <ul>
 *     <li><strong>发送场景：</strong>before 方法按注册顺序正向执行，after 方法按注册顺序反向执行</li>
 *     <li><strong>消费场景：</strong>before 方法按注册顺序正向执行，after 方法按注册顺序反向执行</li>
 * </ul>
 *
 * <h3>典型应用场景：</h3>
 * <ol>
 *     <li><strong>多租户支持：</strong>在消息中传递租户标识，消费时恢复租户上下文</li>
 *     <li><strong>消息追踪：</strong>记录消息的发送和消费日志，实现全链路追踪</li>
 *     <li><strong>性能监控：</strong>统计消息处理耗时，监控队列性能指标</li>
 *     <li><strong>安全增强：</strong>对敏感消息进行加密和签名验证</li>
 *     <li><strong>幂等性保障：</strong>记录消息处理状态，防止重复消费</li>
 *     <li><strong>分布式事务：</strong>与本地事务结合，实现最终一致性</li>
 *     <li><strong>消息转换：</strong>统一处理消息格式转换和协议适配</li>
 * </ol>
 *
 * @author 芋道源码
 * @author Refinex
 * @see AbstractRedisMessage
 * @see RedisMQTemplate
 * @since 1.0.0
 */
public interface RedisMessageInterceptor {

    /**
     * 消息发送前的拦截处理
     *
     * <p>在消息即将发送到 Redis 之前执行。可以在此方法中：</p>
     * <ul>
     *     <li>向消息头添加额外的上下文信息（如租户ID、追踪ID等）</li>
     *     <li>对消息内容进行加密或签名</li>
     *     <li>记录消息发送日志</li>
     *     <li>进行消息格式校验</li>
     * </ul>
     *
     * <p><strong>注意：</strong>该方法的执行时间会直接影响消息发送性能，应避免耗时操作。</p>
     *
     * @param message 即将发送的消息对象
     * @throws RuntimeException 如果抛出异常，消息将不会被发送
     */
    default void sendMessageBefore(AbstractRedisMessage message) {
        // 默认空实现，子类可选择性覆盖
    }

    /**
     * 消息发送后的拦截处理
     *
     * <p>在消息成功发送到 Redis 之后执行。可以在此方法中：</p>
     * <ul>
     *     <li>记录消息发送成功的日志</li>
     *     <li>更新消息发送统计指标</li>
     *     <li>触发后续业务逻辑</li>
     *     <li>清理临时资源</li>
     * </ul>
     *
     * <p><strong>执行保证：</strong>即使发送过程中出现异常，该方法仍会在 finally 块中执行，因此可用于资源清理等必须执行的操作。</p>
     *
     * @param message 已发送的消息对象
     */
    default void sendMessageAfter(AbstractRedisMessage message) {
        // 默认空实现，子类可选择性覆盖
    }

    /**
     * 消息消费前的拦截处理
     *
     * <p>在消费者接收到消息后、业务处理逻辑执行前调用。可以在此方法中：</p>
     * <ul>
     *     <li>从消息头恢复上下文信息（如租户ID、用户ID等）</li>
     *     <li>对消息内容进行解密或验签</li>
     *     <li>记录消息消费开始日志</li>
     *     <li>实现幂等性检查</li>
     *     <li>进行消息格式转换</li>
     * </ul>
     *
     * <p><strong>上下文管理：</strong>该方法常用于建立线程上下文（如 ThreadLocal），务必在对应的 consumeMessageAfter 方法中清理，避免内存泄漏。</p>
     *
     * @param message 即将被消费的消息对象
     * @throws RuntimeException 如果抛出异常，消息处理会失败，根据消息模式可能会重试
     */
    default void consumeMessageBefore(AbstractRedisMessage message) {
        // 默认空实现，子类可选择性覆盖
    }

    /**
     * 消息消费后的拦截处理
     *
     * <p>在消费者完成消息处理后执行。可以在此方法中：</p>
     * <ul>
     *     <li>清理线程上下文信息（如 ThreadLocal）</li>
     *     <li>记录消息消费完成日志</li>
     *     <li>更新消息处理统计指标</li>
     *     <li>触发后续的业务流程</li>
     *     <li>释放临时资源</li>
     * </ul>
     *
     * <p><strong>执行保证：</strong>无论消息处理成功或失败，该方法都会在 finally 块中执行，因此必须在此方法中清理所有临时资源和上下文，防止内存泄漏。</p>
     *
     * <p><strong>异常安全：</strong>该方法内部不应抛出异常，否则可能导致资源无法释放。建议在方法内部捕获所有可能的异常并记录日志。</p>
     *
     * @param message 已被消费的消息对象
     */
    default void consumeMessageAfter(AbstractRedisMessage message) {
        // 默认空实现，子类可选择性覆盖
    }
}
