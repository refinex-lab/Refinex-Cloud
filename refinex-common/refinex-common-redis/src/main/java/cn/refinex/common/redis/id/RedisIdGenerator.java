package cn.refinex.common.redis.id;

import cn.refinex.common.exception.SystemException;
import cn.refinex.common.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 基于 Redis 的分布式 ID 生成器
 * <p>
 * 特点：
 * - 使用 Redis 原子自增保证唯一性
 * - ID 值连续且可控（从指定起始值开始）
 * - 支持分布式环境
 * - 性能高，无需锁表
 * - ID 值较小，适合对 ID 长度敏感的场景
 * <p>
 * 与雪花算法对比：
 * - 雪花算法：ID 值很大（19位），无需外部依赖，性能最高
 * - Redis ID：ID 值连续可控，依赖 Redis，性能略低但足够高
 * <p>
 * 使用场景：
 * - 用户 ID、订单号等需要连续 ID 的场景
 * - 对 ID 长度敏感的场景
 * - 需要按 ID 排序的场景
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisIdGenerator {

    private final RedisService redisService;

    /**
     * Redis Key 前缀
     */
    private static final String REDIS_KEY_PREFIX = "id_generator:";

    /**
     * 默认起始 ID
     */
    private static final long DEFAULT_START_ID = 1000L;

    /**
     * 获取下一个 ID（使用默认业务类型和默认起始值）
     *
     * @return 下一个唯一 ID
     */
    public Long nextId() {
        return nextId("default");
    }

    /**
     * 获取下一个 ID（指定业务类型，使用默认起始值）
     *
     * @param businessType 业务类型（如：user、order、product）
     * @return 下一个唯一 ID
     */
    public Long nextId(String businessType) {
        return nextId(businessType, DEFAULT_START_ID);
    }

    /**
     * 获取下一个 ID（指定业务类型和起始值）
     * <p>
     * 示例：
     * <pre>{@code
     * // 用户 ID 从 1000 开始
     * Long userId = nextId("user", 1000L);
     *
     * // 订单 ID 从 10000 开始
     * Long orderId = nextId("order", 10000L);
     * }</pre>
     *
     * @param businessType 业务类型（如：user、order、product）
     * @param startId      起始 ID 值（仅第一次生效）
     * @return 下一个唯一 ID
     */
    public Long nextId(String businessType, Long startId) {
        try {
            String redisKey = REDIS_KEY_PREFIX + businessType;

            // 检查 key 是否存在
            Boolean exists = redisService.hasKey(redisKey);

            if (Boolean.FALSE.equals(exists)) {
                // 首次使用，初始化起始值
                // 使用 setIfAbsent 保证并发安全（只有第一个线程能设置成功）
                Boolean success = redisService.string().setIfAbsent(redisKey, startId - 1);
                if (Boolean.TRUE.equals(success)) {
                    log.info("初始化 ID 生成器成功，businessType: {}, startId: {}", businessType, startId);
                }
                // 如果设置失败，说明其他线程已经初始化了，继续执行自增即可
            }

            // 原子自增并返回
            Long nextId = redisService.string().increment(redisKey);
            log.debug("生成 ID 成功，businessType: {}, nextId: {}", businessType, nextId);

            return nextId;
        } catch (Exception e) {
            log.error("生成 ID 失败，businessType: {}, startId: {}", businessType, startId, e);
            throw new SystemException("生成 ID 失败", e);
        }
    }

    /**
     * 批量获取 ID
     * <p>
     * 一次性获取多个连续的 ID，适合批量插入场景
     *
     * @param businessType 业务类型
     * @param count        获取数量
     * @param startId      起始 ID 值（仅第一次生效）
     * @return ID 起始值（可以使用 startValue ~ startValue+count-1 的所有 ID）
     */
    public Long nextIdBatch(String businessType, int count, Long startId) {
        if (count <= 0) {
            throw new IllegalArgumentException("批量获取 ID 数量必须大于 0");
        }

        try {
            String redisKey = REDIS_KEY_PREFIX + businessType;

            // 检查 key 是否存在
            Boolean exists = redisService.hasKey(redisKey);

            if (Boolean.FALSE.equals(exists)) {
                // 首次使用，初始化起始值
                Boolean success = redisService.string().setIfAbsent(redisKey, startId - 1);
                if (Boolean.TRUE.equals(success)) {
                    log.info("初始化 ID 生成器成功，businessType: {}, startId: {}", businessType, startId);
                }
            }

            // 原子自增指定数量
            Long startValue = redisService.string().increment(redisKey, count);
            // increment 返回的是增加后的值，所以起始值需要减去 count - 1
            Long batchStartId = startValue - count + 1;

            log.debug("批量生成 ID 成功，businessType: {}, startId: {}, endId: {}, count: {}",
                    businessType, batchStartId, startValue, count);

            return batchStartId;
        } catch (Exception e) {
            log.error("批量生成 ID 失败，businessType: {}, count: {}, startId: {}", businessType, count, startId, e);
            throw new SystemException("批量生成 ID 失败", e);
        }
    }

    /**
     * 获取当前 ID 值（不自增）
     *
     * @param businessType 业务类型
     * @return 当前 ID 值，如果不存在返回 null
     */
    public Long getCurrentId(String businessType) {
        try {
            String redisKey = REDIS_KEY_PREFIX + businessType;
            Object value = redisService.string().get(redisKey);
            return value != null ? Long.parseLong(value.toString()) : null;
        } catch (Exception e) {
            log.error("获取当前 ID 失败，businessType: {}", businessType, e);
            return null;
        }
    }

    /**
     * 重置 ID 生成器（谨慎使用）
     * <p>
     * 注意：只在测试环境或特殊场景使用，生产环境慎用！
     *
     * @param businessType 业务类型
     * @param newValue     新的起始值
     */
    public void resetId(String businessType, Long newValue) {
        try {
            String redisKey = REDIS_KEY_PREFIX + businessType;
            redisService.string().set(redisKey, newValue);
            log.warn("重置 ID 生成器，businessType: {}, newValue: {}", businessType, newValue);
        } catch (Exception e) {
            log.error("重置 ID 生成器失败，businessType: {}, newValue: {}", businessType, newValue, e);
            throw new SystemException("重置 ID 生成器失败", e);
        }
    }

    /**
     * 设置 ID 生成器的过期时间
     * <p>
     * 用于临时性的 ID 生成场景，过期后自动清理
     *
     * @param businessType 业务类型
     * @param duration     过期时间
     */
    public void setExpire(String businessType, Duration duration) {
        try {
            String redisKey = REDIS_KEY_PREFIX + businessType;
            redisService.expire(redisKey, duration);
            log.info("设置 ID 生成器过期时间，businessType: {}, duration: {}", businessType, duration);
        } catch (Exception e) {
            log.error("设置 ID 生成器过期时间失败，businessType: {}, duration: {}", businessType, duration, e);
            throw new SystemException("设置过期时间失败", e);
        }
    }
}

