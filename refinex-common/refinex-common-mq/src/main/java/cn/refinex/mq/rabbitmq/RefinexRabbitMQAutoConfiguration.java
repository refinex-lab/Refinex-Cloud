package cn.refinex.mq.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * RabbitMQ 消息队列自动配置类
 * <p>
 * 该配置类负责初始化 RabbitMQ 相关的核心组件，包括消息转换器等。仅在 Spring AMQP 的 RabbitTemplate 类存在时生效。
 *
 * <h3>功能特性：</h3>
 * <ul>
 *     <li>自动配置 Jackson2 JSON 消息转换器，实现消息的序列化和反序列化</li>
 *     <li>支持复杂对象的自动转换，无需手动序列化</li>
 *     <li>条件化装配，避免不必要的 Bean 创建</li>
 * </ul>
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.amqp.rabbit.core.RabbitTemplate")
public class RefinexRabbitMQAutoConfiguration {

    /**
     * 创建 Jackson2 JSON 消息转换器
     * <p>
     * 该转换器负责将 Java 对象转换为 JSON 格式的消息体，以及将 JSON 消息体反序列化为 Java 对象。
     *
     * <h4>转换器特性：</h4>
     * <ul>
     *     <li>支持复杂对象的序列化和反序列化</li>
     *     <li>自动处理日期、枚举等常见类型</li>
     *     <li>支持泛型和嵌套对象</li>
     *     <li>保留对象类型信息，便于反序列化</li>
     * </ul>
     *
     * @return Jackson2JsonMessageConverter 实例
     */
    @Bean
    public MessageConverter rabbitMessageConverter() {
        log.info("[RabbitMQ配置] 初始化 Jackson2JsonMessageConverter");
        return new Jackson2JsonMessageConverter();
    }
}
