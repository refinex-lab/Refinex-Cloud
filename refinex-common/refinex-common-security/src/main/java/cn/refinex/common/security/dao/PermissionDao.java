package cn.refinex.common.security.dao;

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
 * 权限数据访问对象
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionDao {

    private final JdbcTemplateManager jdbcManager;
    private final RedisService redisService;
    private final RefinexSecurityProperties securityProperties;

    /**
     * 查询用户的所有权限编码列表
     * <p>
     * SQL 说明：
     * 1. 通过用户角色关联表和角色权限关联表查询用户的所有权限
     * 2. 过滤已删除和已停用的权限
     * 3. 考虑用户角色的时间有效期
     * 4. 使用 DISTINCT 去重
     *
     * @param userId 用户 ID
     * @return 权限编码列表（如 ["user:add", "content:delete"]）
     */
    public List<String> selectPermissionCodesByUserId(Long userId) {
        if (userId == null) {
            log.warn("查询用户权限时，用户 ID 为空");
            return Collections.emptyList();
        }

        // 如果启用缓存，先查缓存
        if (Boolean.TRUE.equals(securityProperties.getCache().getEnabled())) {
            String cacheKey = SecurityCacheConstants.buildPermissionCacheKey(userId);
            Object cached = redisService.getStringService().get(cacheKey);
            if (cached != null) {
                log.debug("从缓存获取用户权限，userId: {}", userId);
                @SuppressWarnings("unchecked")
                List<String> result = (List<String>) cached;
                return result;
            }
        }

        // 查询数据库
        String sql = """
                SELECT DISTINCT p.permission_code
                FROM sys_permission p
                INNER JOIN sys_role_permission rp ON p.id = rp.permission_id
                INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id
                WHERE ur.user_id = :userId
                  AND p.status = 0
                  AND p.deleted = 0
                  AND (ur.valid_until IS NULL OR ur.valid_until > NOW())
                ORDER BY p.permission_code
                """;

        Map<String, Object> params = Map.of("userId", userId);

        try {
            List<String> permissions = jdbcManager.queryList(sql, params, true, String.class);

            // 写入缓存
            if (Boolean.TRUE.equals(securityProperties.getCache().getEnabled()) && !permissions.isEmpty()) {
                String cacheKey = SecurityCacheConstants.buildPermissionCacheKey(userId);
                Long ttl = securityProperties.getCache().getTtl();
                redisService.getStringService().set(cacheKey, permissions, Duration.ofSeconds(ttl));
                log.debug("用户权限已缓存，userId: {}, count: {}", userId, permissions.size());
            }

            return permissions;
        } catch (Exception e) {
            log.error("查询用户权限失败，userId: {}", userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 查询角色的所有权限编码列表
     * <p>
     * SQL 说明：
     * 1. 通过角色权限关联表查询角色的所有权限
     * 2. 过滤已删除和已停用的权限
     * 3. 使用 DISTINCT 去重
     *
     * @param roleId 角色 ID
     * @return 权限编码列表
     */
    public List<String> selectPermissionCodesByRoleId(Long roleId) {
        if (roleId == null) {
            log.warn("查询角色权限时，角色 ID 为空");
            return Collections.emptyList();
        }

        String sql = """
                SELECT DISTINCT p.permission_code
                FROM sys_permission p
                INNER JOIN sys_role_permission rp ON p.id = rp.permission_id
                WHERE rp.role_id = :roleId
                  AND p.status = 0
                  AND p.deleted = 0
                ORDER BY p.permission_code
                """;

        Map<String, Object> params = Map.of("roleId", roleId);

        try {
            return jdbcManager.queryList(sql, params, true, String.class);
        } catch (Exception e) {
            log.error("查询角色权限失败，roleId: {}", roleId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 批量查询多个角色的权限编码列表
     * <p>
     * SQL 说明：
     * 1. 通过角色权限关联表批量查询多个角色的所有权限
     * 2. 过滤已删除和已停用的权限
     * 3. 使用 DISTINCT 去重
     *
     * @param roleIds 角色 ID 列表
     * @return 权限编码列表（去重）
     */
    public List<String> selectPermissionCodesByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            log.warn("查询角色权限时，角色 ID 列表为空");
            return Collections.emptyList();
        }

        String sql = """
                SELECT DISTINCT p.permission_code
                FROM sys_permission p
                INNER JOIN sys_role_permission rp ON p.id = rp.permission_id
                WHERE rp.role_id IN (:roleIds)
                  AND p.status = 0
                  AND p.deleted = 0
                ORDER BY p.permission_code
                """;

        Map<String, Object> params = Map.of("roleIds", roleIds);

        try {
            return jdbcManager.queryList(sql, params, true, String.class);
        } catch (Exception e) {
            log.error("批量查询角色权限失败，roleIds: {}", roleIds, e);
            return Collections.emptyList();
        }
    }

    /**
     * 清除用户权限缓存
     *
     * @param userId 用户 ID
     */
    public void clearPermissionCache(Long userId) {
        if (userId == null) {
            return;
        }
        String cacheKey = SecurityCacheConstants.buildPermissionCacheKey(userId);
        redisService.delete(cacheKey);
        log.info("清除用户权限缓存，userId: {}", userId);
    }

    /**
     * 批量清除用户权限缓存
     *
     * @param userIds 用户 ID 列表
     */
    public void clearPermissionCacheBatch(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        userIds.forEach(this::clearPermissionCache);
        log.info("批量清除用户权限缓存，count: {}", userIds.size());
    }
}

