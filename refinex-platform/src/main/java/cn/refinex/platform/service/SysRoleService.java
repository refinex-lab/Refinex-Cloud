package cn.refinex.platform.service;

import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.platform.controller.role.dto.request.RoleCreateRequestDTO;
import cn.refinex.platform.controller.role.dto.request.RoleUpdateRequestDTO;
import cn.refinex.platform.controller.role.dto.response.RoleUserResponseDTO;
import cn.refinex.platform.entity.sys.SysRole;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统角色服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface SysRoleService {

    /**
     * 创建角色
     *
     * @param requestDTO 角色创建请求DTO
     * @param createBy   创建人ID
     * @return 角色ID
     */
    Long createRole(RoleCreateRequestDTO requestDTO, Long createBy);

    /**
     * 更新角色
     *
     * @param roleId     角色ID
     * @param requestDTO 角色更新请求DTO
     * @param updateBy   更新人ID
     * @return 是否成功
     */
    boolean updateRole(Long roleId, RoleUpdateRequestDTO requestDTO, Long updateBy);

    /**
     * 更新角色状态
     *
     * @param roleId   角色ID
     * @param status   状态
     * @param updateBy 更新人ID
     * @return 是否成功
     */
    boolean updateRoleStatus(Long roleId, Integer status, Long updateBy);

    /**
     * 删除角色
     *
     * @param roleId   角色ID
     * @param updateBy 操作人ID
     */
    void deleteRole(Long roleId, Long updateBy);

    /**
     * 根据ID获取角色
     *
     * @param roleId 角色ID
     * @return 角色对象
     */
    SysRole getRoleById(Long roleId);

    /**
     * 根据角色编码获取角色
     *
     * @param roleCode 角色编码
     * @return 角色对象
     */
    SysRole getRoleByCode(String roleCode);

    /**
     * 查询所有启用的角色
     *
     * @return 角色列表
     */
    List<SysRole> listEnabledRoles();

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
    PageResult<SysRole> pageQueryRoles(String roleCode, String roleName, Integer roleType, Integer status, PageRequest pageReq);

    /**
     * 获取角色最大排序值
     *
     * @return 最大排序值
     */
    Integer getMaxRoleSort();

    /**
     * 根据角色ID和用户IDS为用户绑定角色
     *
     * @param roleId   角色ID
     * @param userIds  用户ID列表
     * @param actionBy 操作人ID
     */
    void bindUserRole(Long roleId, List<Long> userIds, Long actionBy);

    /**
     * 为用户绑定角色（支持临时授权）
     *
     * @param roleId     角色ID
     * @param userIds    用户ID列表
     * @param validFrom  有效开始时间
     * @param validUntil 有效结束时间
     * @param actionBy   操作人ID
     */
    void bindUserRoleWithValidity(Long roleId, List<Long> userIds, LocalDateTime validFrom, LocalDateTime validUntil, Long actionBy);

    /**
     * 解绑用户角色
     *
     * @param roleId 角色ID
     * @param userId 用户ID
     */
    void unbindUserRole(Long roleId, Long userId);

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
    PageResult<RoleUserResponseDTO> pageQueryRoleUsers(Long roleId, String username, String nickname, String mobile, String email, PageRequest pageReq);
}
