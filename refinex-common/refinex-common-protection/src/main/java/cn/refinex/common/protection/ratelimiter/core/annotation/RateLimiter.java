package cn.refinex.common.protection.ratelimiter.core.annotation;

import cn.refinex.common.protection.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import cn.refinex.common.protection.ratelimiter.core.keyresolver.impl.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解
 *
 * @author 芋道源码
 * @since 1.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {

    /**
     * 限流时间，单位：秒
     */
    int time() default 1;

    /**
     * 限流时间单位, 默认秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 限流次数, 默认 100 次
     */
    int count() default 100;

    /**
     * 限流提示信息, 默认 "请求过于频繁，请稍后重试"
     */
    String message() default "请求过于频繁，请稍后重试";

    /**
     * 限流键解析器, 默认 {@link DefaultRateLimiterKeyResolver}
     *
     * @see DefaultRateLimiterKeyResolver    全局级别
     * @see UserRateLimiterKeyResolver       用户 ID 级别
     * @see ClientIpRateLimiterKeyResolver   用户 IP 级别
     * @see ServerNodeRateLimiterKeyResolver 服务器 Node 级别
     * @see ExpressionRateLimiterKeyResolver 自定义表达式，通过 {@link #keyArg()} 计算
     */
    Class<? extends RateLimiterKeyResolver> keyResolver() default DefaultRateLimiterKeyResolver.class;

    /**
     * 限流键参数, 默认 ""
     */
    String keyArg() default "";
}
