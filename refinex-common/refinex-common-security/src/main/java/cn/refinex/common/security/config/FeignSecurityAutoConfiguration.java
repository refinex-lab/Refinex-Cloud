package cn.refinex.common.security.config;

import cn.refinex.common.security.interceptor.FeignLogInterceptor;
import cn.refinex.common.security.interceptor.SaTokenFeignInterceptor;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Feign 安全自动配置
 * <p>
 * 自动配置 Feign 调用的 Token 传递和日志记录
 * </p>
 *
 * @author Refinex
 * @since 2025-10-04
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(RequestInterceptor.class)
@ConditionalOnProperty(prefix = "refinex.security", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FeignSecurityAutoConfiguration {

    public FeignSecurityAutoConfiguration() {
        log.info("初始化 Feign 安全配置");
    }

    /**
     * 注册 Sa-Token Feign 拦截器
     *
     * @return SaTokenFeignInterceptor
     */
    @Bean
    public SaTokenFeignInterceptor saTokenFeignInterceptor() {
        log.info("注册 Sa-Token Feign 拦截器");
        return new SaTokenFeignInterceptor();
    }

    /**
     * 注册 Feign 日志拦截器
     *
     * @return FeignLogInterceptor
     */
    @Bean
    @ConditionalOnProperty(prefix = "refinex.security", name = "feign-log-enabled", havingValue = "true", matchIfMissing = false)
    public FeignLogInterceptor feignLogInterceptor() {
        log.info("注册 Feign 日志拦截器");
        return new FeignLogInterceptor();
    }
}

