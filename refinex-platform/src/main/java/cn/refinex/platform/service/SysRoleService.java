package cn.refinex.platform.service;

import java.util.List;

/**
 * 系统角色服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface SysRoleService {

    /**
     * 根据角色ID和用户IDS为用户绑定角色
     *
     * @param roleId   角色ID
     * @param userIds  用户ID列表
     * @param actionBy 操作人ID
     */
    void bindUserRole(Long roleId, List<Long> userIds, Long actionBy);
}
