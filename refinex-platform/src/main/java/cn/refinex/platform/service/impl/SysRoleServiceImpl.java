package cn.refinex.platform.service.impl;

import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.platform.controller.role.dto.request.RoleCreateRequestDTO;
import cn.refinex.platform.controller.role.dto.request.RoleUpdateRequestDTO;
import cn.refinex.platform.controller.role.dto.response.RoleUserResponseDTO;
import cn.refinex.platform.entity.sys.SysRole;
import cn.refinex.platform.repository.sys.SysRoleRepository;
import cn.refinex.platform.repository.sys.SysUserRoleRepository;
import cn.refinex.platform.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
     * 创建角色
     *
     * @param requestDTO 角色创建请求DTO
     * @param createBy   创建人ID
     * @return 角色ID
     */
    @Override
    public Long createRole(RoleCreateRequestDTO requestDTO, Long createBy) {
        // 检查角色编码是否已存在
        if (sysRoleRepository.checkRoleExistsByCode(requestDTO.getRoleCode())) {
            throw new BusinessException("角色编码已存在：" + requestDTO.getRoleCode());
        }

        // 创建角色对象
        SysRole role = SysRole.builder()
                .roleCode(requestDTO.getRoleCode())
                .roleName(requestDTO.getRoleName())
                .roleType(requestDTO.getRoleType())
                .dataScope(requestDTO.getDataScope())
                .isBuiltin(0) // 新创建的角色默认非内置
                .sort(requestDTO.getSort())
                .remark(requestDTO.getRemark())
                .status(requestDTO.getStatus())
                .createBy(createBy)
                .build();

        // 插入数据库
        int rows = sysRoleRepository.insertRole(role);
        if (rows <= 0) {
            throw new BusinessException("创建角色失败");
        }

        return role.getId();
    }

    /**
     * 更新角色
     *
     * @param roleId     角色ID
     * @param requestDTO 角色更新请求DTO
     * @param updateBy   更新人ID
     * @return 是否成功
     */
    @Override
    public boolean updateRole(Long roleId, RoleUpdateRequestDTO requestDTO, Long updateBy) {
        // 检查角色是否存在
        SysRole existingRole = sysRoleRepository.selectRoleById(roleId);
        if (existingRole == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查是否为内置角色
        if (existingRole.getIsBuiltin() == 1) {
            throw new BusinessException("内置角色禁止修改");
        }

        // 更新角色对象
        SysRole role = SysRole.builder()
                .id(roleId)
                .roleName(requestDTO.getRoleName())
                .roleType(requestDTO.getRoleType())
                .dataScope(requestDTO.getDataScope())
                .sort(requestDTO.getSort())
                .remark(requestDTO.getRemark())
                .status(requestDTO.getStatus())
                .updateBy(updateBy)
                .build();

        // 更新数据库
        int rows = sysRoleRepository.updateRole(role);
        if (rows <= 0) {
            throw new BusinessException("更新角色失败");
        }

        return true;
    }

    /**
     * 更新角色状态
     *
     * @param roleId   角色ID
     * @param status   状态
     * @param updateBy 更新人ID
     * @return 是否成功
     */
    @Override
    public boolean updateRoleStatus(Long roleId, Integer status, Long updateBy) {
        // 检查角色是否存在
        if (!sysRoleRepository.checkRoleExistsById(roleId)) {
            throw new BusinessException("角色不存在");
        }

        // 检查是否为内置角色
        if (sysRoleRepository.checkIsBuiltinRole(roleId)) {
            throw new BusinessException("内置角色禁止修改状态");
        }

        // 更新状态
        int rows = sysRoleRepository.updateRoleStatus(roleId, status, updateBy);
        if (rows <= 0) {
            throw new BusinessException("更新角色状态失败");
        }

        return true;
    }

    /**
     * 删除角色
     *
     * @param roleId   角色ID
     * @param updateBy 操作人ID
     */
    @Override
    public void deleteRole(Long roleId, Long updateBy) {
        // 检查角色是否存在
        if (!sysRoleRepository.checkRoleExistsById(roleId)) {
            throw new BusinessException("角色不存在");
        }

        // 检查是否为内置角色
        if (sysRoleRepository.checkIsBuiltinRole(roleId)) {
            throw new BusinessException("内置角色禁止删除");
        }

        // 检查角色下是否有绑定用户
        if (sysRoleRepository.checkRoleHasUsers(roleId)) {
            throw new BusinessException("该角色下存在关联用户，禁止删除");
        }

        // 逻辑删除
        int rows = sysRoleRepository.deleteRole(roleId, updateBy);
        if (rows <= 0) {
            throw new BusinessException("删除角色失败");
        }
    }

    /**
     * 根据ID获取角色
     *
     * @param roleId 角色ID
     * @return 角色对象
     */
    @Override
    public SysRole getRoleById(Long roleId) {
        SysRole role = sysRoleRepository.selectRoleById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }

    /**
     * 根据角色编码获取角色
     *
     * @param roleCode 角色编码
     * @return 角色对象
     */
    @Override
    public SysRole getRoleByCode(String roleCode) {
        SysRole role = sysRoleRepository.selectRoleByCode(roleCode);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }

    /**
     * 查询所有启用的角色
     *
     * @return 角色列表
     */
    @Override
    public List<SysRole> listEnabledRoles() {
        return sysRoleRepository.selectEnabledRoles();
    }

    /**
     * 分页查询角色列表
     *
     * @param roleCode 角色编码（模糊查询）
     * @param roleName 角色名称（模糊查询）
     * @param roleType 角色类型
     * @param status   状态
     * @param pageReq  分页请求
     * @return 分页结果
     */
    @Override
    public PageResult<SysRole> pageQueryRoles(String roleCode, String roleName, Integer roleType, Integer status, PageRequest pageReq) {
        return sysRoleRepository.pageQueryRoles(roleCode, roleName, roleType, status, pageReq);
    }

    /**
     * 获取角色最大排序值
     *
     * @return 最大排序值
     */
    @Override
    public Integer getMaxRoleSort() {
        return sysRoleRepository.selectMaxSort();
    }

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
            throw new BusinessException("角色不存在");
        }

        // 绑定用户角色
        sysUserRoleRepository.bindUserRole(roleId, userIds, actionBy);
    }

    /**
     * 为用户绑定角色（支持临时授权）
     *
     * @param roleId     角色ID
     * @param userIds    用户ID列表
     * @param validFrom  有效开始时间
     * @param validUntil 有效结束时间
     * @param actionBy   操作人ID
     */
    @Override
    public void bindUserRoleWithValidity(Long roleId, List<Long> userIds, LocalDateTime validFrom, LocalDateTime validUntil, Long actionBy) {
        // 检查角色是否存在
        if (!sysRoleRepository.checkRoleExistsById(roleId)) {
            throw new BusinessException("角色不存在");
        }

        // 验证时间范围
        if (validFrom != null && validUntil != null && validUntil.isBefore(validFrom)) {
            throw new BusinessException("有效结束时间不能早于有效开始时间");
        }

        // 为每个用户绑定角色
        for (Long userId : userIds) {
            sysUserRoleRepository.bindUserRole(userId, roleId, validFrom, validUntil, actionBy);
        }
    }

    /**
     * 解绑用户角色
     *
     * @param roleId 角色ID
     * @param userId 用户ID
     */
    @Override
    public void unbindUserRole(Long roleId, Long userId) {
        // 禁止解绑超级管理员
        if (LoginHelper.isSuperAdmin(userId)) {
            throw new BusinessException("禁止解绑超级管理员");
        }

        int rows = sysUserRoleRepository.unbindUserRole(userId, roleId);
        if (rows <= 0) {
            throw new BusinessException("解绑失败");
        }
    }

    /**
     * 分页查询角色下的用户列表
     *
     * @param roleId   角色ID
     * @param username 用户名（模糊查询）
     * @param nickname 昵称（模糊查询）
     * @param mobile   手机号（精确查询）
     * @param email    邮箱（精确查询）
     * @param pageReq  分页请求
     * @return 分页结果
     */
    @Override
    public PageResult<RoleUserResponseDTO> pageQueryRoleUsers(Long roleId, String username, String nickname, String mobile, String email, PageRequest pageReq) {
        return sysUserRoleRepository.pageQueryRoleUsers(roleId, username, nickname, mobile, email, pageReq);
    }
}
