package cn.refinex.common.protection.ratelimiter.core.keyresolver;

import cn.refinex.common.protection.ratelimiter.core.annotation.RateLimiter;
import org.aspectj.lang.JoinPoint;

/**
 * 限流键解析器
 *
 * @author 芋道源码
 * @since 1.0.0
 */
public interface RateLimiterKeyResolver {

    /**
     * 解析限流键
     *
     * @param joinPoint   连接点
     * @param rateLimiter 限流注解
     * @return 限流键
     */
    String resolver(JoinPoint joinPoint, RateLimiter rateLimiter);
}
