package cn.refinex.platform.service.impl;

import cn.refinex.platform.repository.sys.SysRoleRepository;
import cn.refinex.platform.repository.sys.SysUserRoleRepository;
import cn.refinex.platform.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统角色服务实现类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysUserRoleRepository sysUserRoleRepository;
    private final SysRoleRepository sysRoleRepository;

    /**
     * 根据角色ID和用户IDS为用户绑定角色
     *
     * @param roleId  角色ID
     * @param userIds 用户ID列表
     */
    @Override
    public void bindUserRole(Long roleId, List<Long> userIds, Long actionBy) {
        // 检查角色是否存在
        if (!sysRoleRepository.checkRoleExistsById(roleId)) {
            throw new IllegalArgumentException("角色不存在");
        }

        // 绑定用户角色
        sysUserRoleRepository.bindUserRole(roleId, userIds, actionBy);
    }
}
