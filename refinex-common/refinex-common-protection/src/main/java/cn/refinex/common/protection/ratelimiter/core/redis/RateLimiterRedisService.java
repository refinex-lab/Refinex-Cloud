package cn.refinex.common.protection.ratelimiter.core.redis;

import lombok.AllArgsConstructor;
import org.redisson.api.*;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 限流 Redis 服务
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@AllArgsConstructor
public class RateLimiterRedisService {

    private final RedissonClient redissonClient;

    /**
     * 限流键前缀
     * <p>
     * KEY   格式：rate_limiter:%s
     * VALUE 格式：String
     * Expire：不固定
     */
    private static final String RATE_LIMITER = "rate_limiter:%s";

    /**
     * 尝试获取限流令牌
     *
     * @param key      限流键
     * @param count    令牌数量
     * @param time     过期时间
     * @param timeUnit 过期时间单位
     * @return 是否获取成功
     */
    public Boolean tryAcquire(String key, int count, int time, TimeUnit timeUnit) {
        // 获取限流令牌器并设置 rate 速率
        RRateLimiter rateLimiter = getRrateLimiter(key, count, time, timeUnit);
        // 尝试获取令牌
        return rateLimiter.tryAcquire(count);
    }

    /**
     * 获取限流令牌器
     *
     * @param key      限流键
     * @param count    令牌数量
     * @param time     过期时间
     * @param timeUnit 过期时间单位
     * @return 限流令牌器
     */
    private RRateLimiter getRrateLimiter(String key, long count, int time, TimeUnit timeUnit) {
        String redisKey = String.format(RATE_LIMITER, key);
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(redisKey);
        Duration rateInterval = Duration.ofMillis(timeUnit.toMillis(time));

        // 如果限流配置不存在，则设置 rate 速率
        RateLimiterConfig config = rateLimiter.getConfig();
        if (Objects.isNull(config)) {
            rateLimiter.trySetRate(RateType.OVERALL, count, rateInterval);
            rateLimiter.expire(rateInterval);
            return rateLimiter;
        }

        // 如果限流配置存在且配置相同，直接返回, 否则更新 rate 速率
        boolean sameConfig = config.getRateType() == RateType.OVERALL
                        && config.getRate() == count
                        && config.getRateInterval() == rateInterval.toMillis();

        if (!sameConfig) {
            rateLimiter.setRate(RateType.OVERALL, count, rateInterval);
            rateLimiter.expire(rateInterval);
        }

        return rateLimiter;
    }
}
