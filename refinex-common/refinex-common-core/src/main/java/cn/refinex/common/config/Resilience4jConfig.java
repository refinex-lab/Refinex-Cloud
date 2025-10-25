package cn.refinex.common.config;

import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.exception.SystemException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerErrorException;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * 基于 Resilience4j 的重试和断路器配置类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Configuration
public class Resilience4jConfig {

    /**
     * 重试配置
     */
    @Bean
    public RetryConfig retryConfig() {
        return RetryConfig.custom()
                // 最大重试次数
                .maxAttempts(3)
                // 重试间隔
                .waitDuration(Duration.ofSeconds(2))
                // 哪些异常触发重试
                .retryExceptions(ServerErrorException.class, TimeoutException.class)
                // 哪些异常不重试
                .ignoreExceptions(SystemException.class, BusinessException.class)
                .build();
    }

    /**
     * 重试注册中心
     */
    @Bean
    public RetryRegistry retryRegistry(RetryConfig retryConfig) {
        // 注册重试配置
        return RetryRegistry.of(retryConfig);
    }

    /**
     * 断路器配置
     */
    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                // 失败率阈值 50%
                .failureRateThreshold(50)
                // 慢调用率阈值 50%
                .slowCallRateThreshold(50)
                // 慢调用时长阈值
                .slowCallDurationThreshold(Duration.ofSeconds(5))
                // 基于计数的滑动窗口
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                // 滑动窗口大小
                .slidingWindowSize(10)
                // 最小调用次数
                .minimumNumberOfCalls(5)
                // 断路器打开状态持续时间
                .waitDurationInOpenState(Duration.ofSeconds(60))
                // 半开状态允许的调用次数
                .permittedNumberOfCallsInHalfOpenState(3)
                // 自动从打开到半开
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                // 记录为失败的异常
                .recordExceptions(ServerErrorException.class, TimeoutException.class)
                // 忽略的异常
                .ignoreExceptions(SystemException.class, BusinessException.class)
                .build();
    }

    /**
     * 断路器注册中心
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry(CircuitBreakerConfig config) {
        // 注册断路器配置
        return CircuitBreakerRegistry.of(config);
    }
}
