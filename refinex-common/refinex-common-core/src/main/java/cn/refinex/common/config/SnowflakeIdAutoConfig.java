package cn.refinex.common.config;

import cn.refinex.common.utils.algorithm.SnowflakeIdGenerator;
import lombok.Data;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Snowflake ID 自动配置
 * <p>
 * 配置示例：
 *
 * <pre>{@code
 * snowflake:
 *   worker-id: 1
 *   datacenter-id: 1
 *   max-backward-ms: 2
 * }</pre>
 * <p>
 * 使用示例：
 *
 * <pre>{@code
 * private final SnowflakeIdGenerator idGenerator;
 *
 * public TestController(SnowflakeIdGenerator idGenerator) {
 *     this.idGenerator = idGenerator;
 * }
 *
 * // 使用示例
 * long id = idGenerator.nextId();
 * }</pre>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@AutoConfiguration
@ConfigurationProperties(prefix = "snowflake")
@ConditionalOnMissingBean(SnowflakeIdGenerator.class)
public class SnowflakeIdAutoConfig {

    /**
     * 工作节点ID (0-31)
     */
    private long workerId = 0;

    /**
     * 数据中心ID (0-31)
     */
    private long datacenterId = 0;

    /**
     * 容忍的最大时钟回拨毫秒数，默认2ms
     */
    private long maxBackwardMs = 2;

    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator() {
        return new SnowflakeIdGenerator(workerId, datacenterId, maxBackwardMs);
    }
}
