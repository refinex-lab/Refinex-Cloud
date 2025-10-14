package cn.refinex.mq.redis.core.message;

import lombok.Data;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis 消息抽象基类
 *
 * <p>所有 Redis 消息类型的公共父类，提供消息头（Header）的统一管理机制。
 * 消息头用于传递元数据信息，不影响业务数据的结构。</p>
 *
 * <h3>消息头的典型用途：</h3>
 * <ul>
 *     <li><strong>上下文传递：</strong>租户ID、用户ID、请求ID等上下文信息</li>
 *     <li><strong>消息追踪：</strong>TraceID、SpanID等分布式追踪标识</li>
 *     <li><strong>消息元信息：</strong>消息类型、版本号、优先级等</li>
 *     <li><strong>业务标识：</strong>业务流水号、订单号等业务唯一标识</li>
 *     <li><strong>扩展属性：</strong>其他需要传递但不属于业务数据的信息</li>
 * </ul>
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@Data
public abstract class AbstractRedisMessage {

    /**
     * 消息头，用于存储元数据信息
     *
     * <p>消息头不参与业务逻辑，仅用于传递上下文、追踪、标识等辅助信息。建议只存储必要的元数据，避免消息体积过大。</p>
     */
    private Map<String, String> headers = new HashMap<>();


    /**
     * 获取消息头的值
     *
     * <p>如果指定的 key 不存在，返回 null。</p>
     *
     * @param key 消息头的键
     * @return 消息头的值，不存在则返回 null
     */
    public String getHeader(String key) {
        return headers.get(key);
    }

    /**
     * 添加消息头
     *
     * <p>如果 key 已存在，则覆盖原有值。建议 value 不要为 null，使用空字符串代替。</p>
     *
     * @param key   消息头的键，不能为 null
     * @param value 消息头的值，建议不为 null
     */
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * 批量添加消息头
     *
     * <p>将指定 Map 中的所有键值对添加到消息头中，如果存在相同的 key，则覆盖原有值。</p>
     *
     * @param headers 要添加的消息头 Map，不能为 null
     */
    public void addHeaders(Map<String, String> headers) {
        if (MapUtils.isNotEmpty(headers)) {
            this.headers.putAll(headers);
        }
    }

    /**
     * 移除指定的消息头
     *
     * @param key 要移除的消息头键
     * @return 被移除的消息头值，如果不存在则返回 null
     */
    public String removeHeader(String key) {
        return headers.remove(key);
    }

    /**
     * 清空所有消息头
     */
    public void clearHeaders() {
        headers.clear();
    }

    /**
     * 判断是否包含指定的消息头
     *
     * @param key 消息头的键
     * @return 如果包含则返回 true，否则返回 false
     */
    public boolean containsHeader(String key) {
        return headers.containsKey(key);
    }
}
