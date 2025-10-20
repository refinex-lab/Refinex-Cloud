package cn.refinex.platform.service.impl;

import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.redis.RedisService;
import cn.refinex.platform.repository.sys.SysRolePermissionRepository;
import cn.refinex.platform.repository.sys.SysRoleRepository;
import cn.refinex.platform.service.SysRolePermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.refinex.common.constants.SystemRedisKeyConstants.Permission.rolePermissions;

/**
 * 角色权限关系服务实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRolePermissionServiceImpl implements SysRolePermissionService {

    private final JdbcTemplateManager jdbcManager;
    private final SysRoleRepository sysRoleRepository;
    private final SysRolePermissionRepository sysRolePermissionRepository;
    private final RedisService redisService;

    /**
     * 分配角色权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     * @param operatorId    操作人ID
     * @return 是否分配成功
     */
    @Override
    public boolean assignPermissions(Long roleId, List<Long> permissionIds, Long operatorId) {
        // 校验角色是否存在
        if (!sysRoleRepository.checkRoleExistsById(roleId)) {
            throw new BusinessException("角色不存在");
        }

        Boolean result = jdbcManager.executeInTransaction(tx -> {
            // 删除角色已有的权限关联关系
            sysRolePermissionRepository.deleteByRoleId(tx, roleId);

            if (CollectionUtils.isNotEmpty(permissionIds)) {
                // 批量插入新的权限关联关系
                sysRolePermissionRepository.batchInsert(tx, roleId, permissionIds);
            }
            return true;
        });

        try {
            // 删除角色权限缓存, 后续查询时会重新加载
            redisService.delete(rolePermissions(roleId));
        } catch (Exception e) {
            log.warn("删除角色权限缓存失败 roleId={}", roleId, e);
        }

        return Boolean.TRUE.equals(result);
    }

    /**
     * 查询角色权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    @Override
    public List<Long> listPermissionIds(Long roleId) {
        return sysRolePermissionRepository.listPermissionIdsByRoleId(roleId);
    }
}


