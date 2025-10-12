package cn.refinex.platform.service;

import cn.refinex.platform.domain.entity.sys.SysRole;

import java.util.List;
import java.util.Set;

/**
 * 权限服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface PermissionService {

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 用户角色列表
     */
    Set<String> getUserRolePermissions(Long userId);

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 用户角色列表
     */
    List<SysRole> getUserRoles(Long userId);

    /**
     * 获取用户权限列表
     *
     * @param userId 用户ID
     * @return 用户权限列表
     */
    Set<String> getUserMenuPermissions(Long userId);
}
