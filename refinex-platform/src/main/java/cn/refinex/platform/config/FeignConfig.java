package cn.refinex.platform.config;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 配置
 * <p>
 * 配置 Feign 的日志级别、重试策略和超时时间
 * </p>
 *
 * @author Refinex
 * @since 2025-10-04
 */
@Configuration
public class FeignConfig {

    /**
     * 配置 Feign 日志级别
     *
     * @return 日志级别
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * 配置 Feign 重试策略
     *
     * @return 重试策略
     */
    @Bean
    public Retryer feignRetryer() {
        // 最大重试次数 3 次，初始间隔 100ms，最大间隔 1s
        return new Retryer.Default(100, 1000, 3);
    }

    /**
     * 配置 Feign 超时时间
     *
     * @return 超时配置
     */
    @Bean
    public Request.Options feignOptions() {
        // 连接超时 5s，读取超时 10s
        return new Request.Options(5000, 10000);
    }
}

