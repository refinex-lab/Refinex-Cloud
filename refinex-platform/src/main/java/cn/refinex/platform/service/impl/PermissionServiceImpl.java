package cn.refinex.platform.service.impl;

import cn.refinex.common.constants.SystemRedisKeyConstants;
import cn.refinex.common.constants.SystemRoleConstants;
import cn.refinex.common.json.utils.JsonUtils;
import cn.refinex.common.redis.RedisService;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.platform.domain.entity.sys.SysRole;
import cn.refinex.platform.domain.entity.sys.SysUserRole;
import cn.refinex.platform.repository.sys.SysUserRoleRepository;
import cn.refinex.platform.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * 权限服务实现类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final SysUserRoleRepository sysUserRoleRepository;
    private final RedisService redisService;

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 用户角色列表
     */
    @Override
    public Set<String> getUserRolePermissions(Long userId) {
        try {
            // 如果是超级管理员，则返回超级管理员角色
            if (LoginHelper.isSuperAdmin(userId)) {
                return Set.of(SystemRoleConstants.SUPER_ADMIN);
            }

            // 先从缓存中获取角色列表
            String userRolesKey = SystemRedisKeyConstants.Permission.userRoles(userId);
            String userRoles = redisService.string().get(userRolesKey, String.class);
            if (StringUtils.isNotBlank(userRoles)) {
                return JsonUtils.toSet(userRoles, String.class);
            }

            // 缓存中没有角色列表，从数据库中查询
            Set<String> userRolesFromDb = sysUserRoleRepository.selectRolePermissionsByUserId(userId);
            if (CollectionUtils.isEmpty(userRolesFromDb)) {
                return Set.of();
            }

            // 缓存用户角色列表
            redisService.string().set(userRolesKey, JsonUtils.toJson(userRolesFromDb), Duration.ofMinutes(30));

            // 返回角色列表
            return userRolesFromDb;
        } catch (Exception e) {
            log.error("获取用户角色失败，userId: {}", userId, e);
            return Set.of();
        }
    }

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 用户角色列表
     */
    @Override
    public List<SysRole> getUserRoles(Long userId) {
        return sysUserRoleRepository.selectRolesByUserId(userId);
    }

    /**
     * 获取用户权限列表
     *
     * @param userId 用户ID
     * @return 用户权限列表
     */
    @Override
    public Set<String> getUserMenuPermissions(Long userId) {
        try {
            // 如果是管理员，则返回所有权限
            if (LoginHelper.isSuperAdmin(userId)) {
                return Set.of("*:*:*");
            }

            // 先从缓存中获取权限列表
            String userPermissionsKey = SystemRedisKeyConstants.Permission.userPermissions(userId);
            String userPermissions = redisService.string().get(userPermissionsKey, String.class);
            if (StringUtils.isNotBlank(userPermissions)) {
                return JsonUtils.toSet(userPermissions, String.class);
            }

            // 缓存中没有权限列表，从数据库中查询
            Set<String> userPermissionsFromDb = sysUserRoleRepository.selectMenuPermissionsByUserId(userId);
            if (CollectionUtils.isEmpty(userPermissionsFromDb)) {
                return Set.of();
            }

            // 缓存用户权限列表
            redisService.string().set(userPermissionsKey, JsonUtils.toJson(userPermissionsFromDb), Duration.ofMinutes(30));

            // 返回权限列表
            return userPermissionsFromDb;
        } catch (Exception e) {
            log.error("获取用户权限失败，userId: {}", userId, e);
            return Set.of();
        }
    }
}
