package cn.refinex.common.security.repository;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.redis.RedisService;
import cn.refinex.common.security.constants.SecurityCacheConstants;
import cn.refinex.common.security.properties.RefinexSecurityProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 角色数据访问对象
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RoleRepository {

    private final JdbcTemplateManager jdbcManager;
    private final RedisService redisService;
    private final RefinexSecurityProperties securityProperties;

    /**
     * 查询用户的所有角色编码列表
     * <p>
     * SQL 说明：
     * 1. 通过用户角色关联表查询用户的所有角色
     * 2. 过滤已删除和已停用的角色
     * 3. 使用 DISTINCT 去重
     *
     * @param userId 用户 ID
     * @return 角色编码列表（如 ["ROLE_USER", "ROLE_ADMIN"]）
     */
    public List<String> selectRoleCodesByUserId(Long userId) {
        if (userId == null) {
            log.warn("查询用户角色时，用户 ID 为空");
            return Collections.emptyList();
        }

        // 如果启用缓存，先查缓存
        if (Boolean.TRUE.equals(securityProperties.getCache().getEnabled())) {
            String cacheKey = SecurityCacheConstants.buildRoleCacheKey(userId);
            Object cached = redisService.getStringService().get(cacheKey);
            if (cached != null) {
                log.debug("从缓存获取用户角色，userId: {}", userId);
                @SuppressWarnings("unchecked")
                List<String> result = (List<String>) cached;
                return result;
            }
        }

        // 查询数据库
        String sql = """
                SELECT DISTINCT r.role_code
                FROM sys_role r
                INNER JOIN sys_user_role ur ON r.id = ur.role_id
                WHERE ur.user_id = :userId
                  AND r.status = 0
                  AND r.deleted = 0
                ORDER BY r.role_code
                """;

        Map<String, Object> params = Map.of("userId", userId);

        try {
            List<String> roles = jdbcManager.queryList(sql, params, true, String.class);

            // 写入缓存
            if (Boolean.TRUE.equals(securityProperties.getCache().getEnabled()) && !roles.isEmpty()) {
                String cacheKey = SecurityCacheConstants.buildRoleCacheKey(userId);
                Long ttl = securityProperties.getCache().getTtl();
                redisService.getStringService().set(cacheKey, roles, Duration.ofSeconds(ttl));
                log.debug("用户角色已缓存，userId: {}, count: {}", userId, roles.size());
            }

            return roles;
        } catch (Exception e) {
            log.error("查询用户角色失败，userId: {}", userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 查询用户的所有有效角色编码列表（考虑时间有效期）
     * <p>
     * SQL 说明：
     * 1. 通过用户角色关联表查询用户的所有角色
     * 2. 过滤已删除和已停用的角色
     * 3. 考虑角色的时间有效期（valid_from 和 valid_until）
     * 4. 使用 DISTINCT 去重
     *
     * @param userId 用户 ID
     * @return 角色编码列表
     */
    public List<String> selectValidRoleCodesByUserId(Long userId) {
        if (userId == null) {
            log.warn("查询用户有效角色时，用户 ID 为空");
            return Collections.emptyList();
        }

        String sql = """
                SELECT DISTINCT r.role_code
                FROM sys_role r
                INNER JOIN sys_user_role ur ON r.id = ur.role_id
                WHERE ur.user_id = :userId
                  AND r.status = 0
                  AND r.deleted = 0
                  AND (ur.valid_from IS NULL OR ur.valid_from <= NOW())
                  AND (ur.valid_until IS NULL OR ur.valid_until > NOW())
                ORDER BY r.role_code
                """;

        Map<String, Object> params = Map.of("userId", userId);

        try {
            return jdbcManager.queryList(sql, params, true, String.class);
        } catch (Exception e) {
            log.error("查询用户有效角色失败，userId: {}", userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 清除用户角色缓存
     *
     * @param userId 用户 ID
     */
    public void clearRoleCache(Long userId) {
        if (userId == null) {
            return;
        }
        String cacheKey = SecurityCacheConstants.buildRoleCacheKey(userId);
        redisService.delete(cacheKey);
        log.info("清除用户角色缓存，userId: {}", userId);
    }

    /**
     * 批量清除用户角色缓存
     *
     * @param userIds 用户 ID 列表
     */
    public void clearRoleCacheBatch(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        userIds.forEach(this::clearRoleCache);
        log.info("批量清除用户角色缓存，count: {}", userIds.size());
    }
}

