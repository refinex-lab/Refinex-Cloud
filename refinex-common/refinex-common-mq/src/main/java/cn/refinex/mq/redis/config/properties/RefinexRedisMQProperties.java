package cn.refinex.mq.redis.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Redis MQ 配置属性类
 *
 * <h3>配置示例：</h3>
 * <pre>{@code
 * refinex:
 *   mq:
 *     redis:
 *       stream:
 *         batch-size: 20
 *         pending-message:
 *           resend-enabled: true
 *           expire-time: 10m
 *           lock-timeout: 30s
 *         cleanup:
 *           enabled: true
 *           max-length: 20000
 *           lock-timeout: 60s
 * }</pre>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "refinex.mq.redis")
public class RefinexRedisMQProperties {

    /**
     * Redis Stream 相关配置
     */
    @NotNull(message = "Redis Stream 配置不能为空")
    private StreamProperties stream = new StreamProperties();

    /**
     * Redis Stream 配置属性
     */
    @Data
    public static class StreamProperties {

        /**
         * 每次拉取的最大消息数量
         *
         * <p>建议值：10-50，具体取决于消息处理速度和系统负载</p>
         */
        @Min(value = 1, message = "批次大小必须大于等于 1")
        private Integer batchSize = 10;

        /**
         * Pending 消息重发配置
         */
        @NotNull(message = "Pending 消息重发配置不能为空")
        private PendingMessageProperties pendingMessage = new PendingMessageProperties();

        /**
         * 消息清理配置
         */
        @NotNull(message = "消息清理配置不能为空")
        private CleanupProperties cleanup = new CleanupProperties();
    }

    /**
     * Pending 消息重发配置
     */
    @Data
    public static class PendingMessageProperties {

        /**
         * 是否启用 Pending 消息重发功能
         *
         * <p>推荐开启，用于处理消费者崩溃后未确认的消息</p>
         */
        private Boolean enabled = true;

        /**
         * 消息超时时间
         *
         * <p>超过该时间未确认的消息将被重新投递。
         * 建议值：5-15 分钟，根据业务处理时长调整</p>
         */
        @NotNull(message = "消息超时时间不能为空")
        private Duration expireTime = Duration.ofMinutes(5);

        /**
         * 定时任务 Cron 表达式
         *
         * <p>默认每分钟的第 35 秒执行，避开整点高峰</p>
         */
        @NotNull(message = "定时任务 Cron 表达式不能为空")
        private String cron = "35 * * * * ?";

        /**
         * 分布式锁超时时间
         *
         * <p>防止任务执行时间过长导致锁一直被占用</p>
         */
        @NotNull(message = "锁超时时间不能为空")
        private Duration lockTimeout = Duration.ofSeconds(30);
    }

    /**
     * 消息清理配置
     */
    @Data
    public static class CleanupProperties {

        /**
         * 是否启用消息清理功能
         *
         * <p>推荐开启，防止 Stream 占用过多内存</p>
         */
        private Boolean enabled = true;

        /**
         * Stream 保留的最大消息数量
         *
         * <p>超过该数量的旧消息将被清理。
         * 建议值：10000-100000，根据消息量和内存容量调整</p>
         */
        @Min(value = 1000, message = "保留消息数量不能少于 1000")
        private Long maxLength = 10000L;

        /**
         * 定时任务 Cron 表达式
         *
         * <p>默认每小时执行一次</p>
         */
        @NotNull(message = "定时任务 Cron 表达式不能为空")
        private String cron = "0 0 * * * ?";

        /**
         * 分布式锁超时时间
         */
        @NotNull(message = "锁超时时间不能为空")
        private Duration lockTimeout = Duration.ofSeconds(60);
    }
}
