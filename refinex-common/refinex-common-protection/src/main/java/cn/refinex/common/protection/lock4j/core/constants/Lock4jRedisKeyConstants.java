package cn.refinex.common.protection.lock4j.core.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Lock4j Redis 键常量类
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Lock4jRedisKeyConstants {

    /**
     * Lock4j Redis 键前缀
     * <p>
     * KEY 格式：lock4j:%s (参数来自 DefaultLockKeyBuilder 类)
     * VALUE 格式：HASH (RLock.class：Redisson 的 Lock 锁，使用 Hash 数据结构)
     * EXPIRE：不固定，根据业务场景设置
     */
    public static final String LOCK4J_REDIS_KEY_PREFIX = "lock4j:%s";
}
