package cn.refinex.common.protection.ratelimiter.core.aop;

import cn.hutool.core.util.StrUtil;
import cn.refinex.common.constants.ModuleConstants;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.exception.code.ResultCode;
import cn.refinex.common.protection.ratelimiter.core.annotation.RateLimiter;
import cn.refinex.common.protection.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import cn.refinex.common.protection.ratelimiter.core.redis.RateLimiterRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 限流切面, 拦截声明 {@link RateLimiter} 注解的方法实现限流操作
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Aspect
public class RateLimiterAspect {

    private final RateLimiterRedisService rateLimiterRedisService;

    /**
     * 限流键解析器映射, 用于根据注解中指定的键解析器类型获取对应的解析器实例
     */
    private final Map<Class<? extends RateLimiterKeyResolver>, RateLimiterKeyResolver> keyResolvers;

    /**
     * 构造函数, 初始化限流键解析器映射和 Redis 服务
     *
     * @param keyResolvers            限流键解析器列表
     * @param rateLimiterRedisService Redis 服务实例
     */
    public RateLimiterAspect(List<RateLimiterKeyResolver> keyResolvers, RateLimiterRedisService rateLimiterRedisService) {
        this.keyResolvers = CollectionUtils.isEmpty(keyResolvers)
                ? new HashMap<>()
                : keyResolvers.stream().collect(Collectors.toMap(RateLimiterKeyResolver::getClass, Function.identity()));
        this.rateLimiterRedisService = rateLimiterRedisService;
    }

    @Before("@annotation(rateLimiter)")
    public void beforePointCut(JoinPoint joinPoint, RateLimiter rateLimiter) {
        // 查找对应的限流键解析器
        RateLimiterKeyResolver keyResolver = keyResolvers.get(rateLimiter.keyResolver());
        Assert.notNull(keyResolver, "找不到对应的 RateLimiterKeyResolver");

        // 解析限流键
        String key = keyResolver.resolver(joinPoint, rateLimiter);

        // 尝试获取限流令牌
        boolean success = rateLimiterRedisService.tryAcquire(key, rateLimiter.count(), rateLimiter.time(), rateLimiter.timeUnit());
        if (!success) {
            log.info("[beforePointCut][方法({}) 参数({}) 请求过于频繁]", joinPoint.getSignature().toString(), joinPoint.getArgs());
            String message = StrUtil.blankToDefault(rateLimiter.message(), ResultCode.TOO_MANY_REQUESTS.getMessage());
            throw new BusinessException(ModuleConstants.MODULE_COMMON, ResultCode.TOO_MANY_REQUESTS.getCode(), message);
        }
    }
}
