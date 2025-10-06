package cn.refinex.common.protection.ratelimiter.core.keyresolver.impl;

import cn.hutool.core.util.StrUtil;
import cn.refinex.common.protection.ratelimiter.core.annotation.RateLimiter;
import cn.refinex.common.protection.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import cn.refinex.common.utils.security.CryptoUtils;
import org.aspectj.lang.JoinPoint;

/**
 * 默认(全局)限流键解析器
 * <p>
 * 键名：方法名 + 方法参数
 * 说明：为了避免 Key 过长，使用 MD5 对键名进行压缩
 *
 * @author 芋道源码
 * @since 1.0.0
 */
public class DefaultRateLimiterKeyResolver implements RateLimiterKeyResolver {

    /**
     * 解析限流键
     *
     * @param joinPoint   连接点
     * @param rateLimiter 限流注解
     * @return 限流键
     */
    @Override
    public String resolver(JoinPoint joinPoint, RateLimiter rateLimiter) {
        String methodName = joinPoint.getSignature().toString();
        String argsStr = StrUtil.join(",", joinPoint.getArgs());
        return CryptoUtils.md5Hex(methodName + argsStr);
    }
}
