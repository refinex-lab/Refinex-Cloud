package cn.refinex.mq.redis.config;

import cn.refinex.mq.redis.core.RedisMQTemplate;
import cn.refinex.mq.redis.core.interceptor.RedisMessageInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * Redis MQ 生产者自动配置类
 *
 * <p>该配置类负责初始化 Redis 消息队列的生产者相关组件，提供统一的消息发送模板和拦截器机制。</p>
 *
 * <h3>核心功能：</h3>
 * <ul>
 *     <li>创建 RedisMQTemplate 用于消息发送</li>
 *     <li>自动注册所有消息拦截器</li>
 *     <li>支持 Pub/Sub 和 Stream 两种消息模式</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * @Autowired
 * private RedisMQTemplate redisMQTemplate;
 *
 * public void sendMessage() {
 *     UserMessage message = new UserMessage();
 *     message.setUserId(1001L);
 *     message.setContent("Hello");
 *
 *     // 发送 Stream 消息（集群消费）
 *     RecordId recordId = redisMQTemplate.send(message);
 *
 *     // 或发送 Channel 消息（广播消费）
 *     redisMQTemplate.send(broadcastMessage);
 * }
 * }</pre>
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
public class RefinexRedisMQProducerAutoConfiguration {

    /**
     * 创建 Redis MQ 模板
     *
     * <p>
     * RedisMQTemplate 是消息发送的核心组件，封装了 Redis 的 Pub/Sub 和 Stream 两种消息模式。
     * 所有消息拦截器会自动注册到模板中，实现消息发送的增强处理。
     * </p>
     *
     * <h4>拦截器执行顺序：</h4>
     * <ul>
     *     <li>发送前：按注册顺序正向执行</li>
     *     <li>发送后：按注册顺序反向执行</li>
     * </ul>
     *
     * <h4>拦截器应用场景：</h4>
     * <ul>
     *     <li>多租户上下文传递</li>
     *     <li>消息追踪和日志记录</li>
     *     <li>消息加密和签名</li>
     *     <li>消息格式校验和转换</li>
     *     <li>性能监控和统计</li>
     * </ul>
     *
     * @param redisTemplate Spring Data Redis 模板，用于底层 Redis 操作
     * @param interceptors  所有注册的消息拦截器，按照 Spring 的 @Order 排序
     * @return RedisMQTemplate 实例
     */
    @Bean
    public RedisMQTemplate redisMQTemplate(StringRedisTemplate redisTemplate, List<RedisMessageInterceptor> interceptors) {
        log.info("[Redis MQ] 开始初始化 RedisMQTemplate，拦截器数量: {}", interceptors.size());

        RedisMQTemplate redisMQTemplate = new RedisMQTemplate(redisTemplate);

        // 注册所有拦截器
        interceptors.forEach(interceptor -> {
            redisMQTemplate.addInterceptor(interceptor);
            log.info("[Redis MQ] 注册消息拦截器: {}", interceptor.getClass().getSimpleName());
        });

        log.info("[Redis MQ] RedisMQTemplate 初始化完成");
        return redisMQTemplate;
    }
}
