package cn.refinex.common.security.config;

import cn.refinex.common.redis.config.RefinexRedisAutoConfiguration;
import cn.refinex.common.security.exception.SaTokenExceptionHandler;
import cn.refinex.common.security.properties.RefinexSecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * Sa-Token 自动配置类
 * <p>
 * 说明：
 * 1. 在 RefinexRedisAutoConfiguration 之后加载，确保 Redis 已配置
 * 2. 通过 @Import 导入其他配置类
 * 3. 通过 refinex.security.enabled 控制是否启用（默认启用）
 * 4. Redis 持久化由 sa-token-redis-template 依赖自动配置，无需手动配置
 * 5. 导入 Sa-Token 异常处理器，统一处理认证授权异常
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration(after = RefinexRedisAutoConfiguration.class)
@EnableConfigurationProperties(RefinexSecurityProperties.class)
@ConditionalOnProperty(prefix = "refinex.security", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({
        SaTokenJwtConfiguration.class,
        SaTokenExceptionHandler.class
})
public class SaTokenAutoConfiguration {

    public SaTokenAutoConfiguration() {
        log.info("初始化 Sa-Token 自动配置");
    }
}

