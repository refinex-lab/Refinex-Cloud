package cn.refinex.platform.service;

import java.util.List;

/**
 * 角色权限关系服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface SysRolePermissionService {

    /**
     * 分配角色权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     * @param operatorId    操作人ID
     * @return 是否分配成功
     */
    boolean assignPermissions(Long roleId, List<Long> permissionIds, Long operatorId);

    /**
     * 查询角色权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> listPermissionIds(Long roleId);
}


