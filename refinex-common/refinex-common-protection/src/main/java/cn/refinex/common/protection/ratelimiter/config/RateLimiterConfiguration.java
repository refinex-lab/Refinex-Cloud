package cn.refinex.common.protection.ratelimiter.config;

import cn.refinex.common.protection.ratelimiter.core.aop.RateLimiterAspect;
import cn.refinex.common.protection.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import cn.refinex.common.protection.ratelimiter.core.keyresolver.impl.*;
import cn.refinex.common.protection.ratelimiter.core.redis.RateLimiterRedisService;
import cn.refinex.common.redis.config.RefinexRedisAutoConfiguration;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 限流配置类
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@AutoConfiguration(after = RefinexRedisAutoConfiguration.class)
public class RateLimiterConfiguration {

    /**
     * 创建限流切面 Bean
     *
     * @param keyResolvers            限流键解析器列表
     * @param rateLimiterRedisService 限流 Redis 服务
     * @return 限流切面实例
     */
    @Bean
    public RateLimiterAspect rateLimiterAspect(List<RateLimiterKeyResolver> keyResolvers, RateLimiterRedisService rateLimiterRedisService) {
        return new RateLimiterAspect(keyResolvers, rateLimiterRedisService);
    }

    /**
     * 创建限流 Redis 服务 Bean
     *
     * @param redissonClient Redisson 客户端
     * @return 限流 Redis 服务实例
     */
    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RateLimiterRedisService rateLimiterRedisService(RedissonClient redissonClient) {
        return new RateLimiterRedisService(redissonClient);
    }

    /**
     * 创建默认限流键解析器 Bean
     *
     * @return 默认限流键解析器实例
     */
    @Bean
    public DefaultRateLimiterKeyResolver defaultRateLimiterKeyResolver() {
        return new DefaultRateLimiterKeyResolver();
    }

    /**
     * 创建用户限流键解析器 Bean
     *
     * @return 用户限流键解析器实例
     */
    @Bean
    public UserRateLimiterKeyResolver userRateLimiterKeyResolver() {
        return new UserRateLimiterKeyResolver();
    }

    /**
     * 创建客户端 IP 限流键解析器 Bean
     *
     * @return 客户端 IP 限流键解析器实例
     */
    @Bean
    public ClientIpRateLimiterKeyResolver clientIpRateLimiterKeyResolver() {
        return new ClientIpRateLimiterKeyResolver();
    }

    /**
     * 创建服务器节点限流键解析器 Bean
     *
     * @return 服务器节点限流键解析器实例
     */
    @Bean
    public ServerNodeRateLimiterKeyResolver serverNodeRateLimiterKeyResolver() {
        return new ServerNodeRateLimiterKeyResolver();
    }

    /**
     * 创建表达式限流键解析器 Bean
     *
     * @return 表达式限流键解析器实例
     */
    @Bean
    public ExpressionRateLimiterKeyResolver expressionRateLimiterKeyResolver() {
        return new ExpressionRateLimiterKeyResolver();
    }
}
