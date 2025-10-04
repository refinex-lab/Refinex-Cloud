package cn.refinex.common.security.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 安全模块缓存常量
 *
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityCacheConstants {

    /**
     * 权限缓存 Key 前缀
     */
    public static final String PERMISSION_CACHE_PREFIX = "security:permission:";

    /**
     * 角色缓存 Key 前缀
     */
    public static final String ROLE_CACHE_PREFIX = "security:role:";

    /**
     * 默认缓存过期时间（秒）- 30 分钟
     */
    public static final long DEFAULT_CACHE_TTL = 1800L;

    /**
     * 构建用户权限缓存 Key
     *
     * @param userId 用户 ID
     * @return 缓存 Key
     */
    public static String buildPermissionCacheKey(Long userId) {
        return PERMISSION_CACHE_PREFIX + userId;
    }

    /**
     * 构建用户角色缓存 Key
     *
     * @param userId 用户 ID
     * @return 缓存 Key
     */
    public static String buildRoleCacheKey(Long userId) {
        return ROLE_CACHE_PREFIX + userId;
    }
}

