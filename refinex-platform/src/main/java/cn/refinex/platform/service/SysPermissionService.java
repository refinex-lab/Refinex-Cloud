package cn.refinex.platform.service;

import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.platform.domain.dto.request.SysPermissionCreateRequest;
import cn.refinex.platform.domain.dto.request.SysPermissionQueryRequest;
import cn.refinex.platform.domain.dto.request.SysPermissionUpdateRequest;
import cn.refinex.platform.domain.entity.sys.SysPermission;

import java.util.List;

/**
 * 权限服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface SysPermissionService {

    /**
     * 创建系统权限
     *
     * @param request    创建请求
     * @param operatorId 操作人ID
     * @return 权限ID
     */
    Long create(SysPermissionCreateRequest request, Long operatorId);

    /**
     * 更新系统权限
     *
     * @param id         权限ID
     * @param request    更新请求
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    boolean update(Long id, SysPermissionUpdateRequest request, Long operatorId);

    /**
     * 删除系统权限
     *
     * @param id         权限ID
     * @param operatorId 操作人ID
     * @return 是否删除成功
     */
    boolean delete(Long id, Long operatorId);

    /**
     * 更新系统权限状态
     *
     * @param id         权限ID
     * @param status     状态
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    boolean updateStatus(Long id, Integer status, Long operatorId);

    /**
     * 根据ID查询系统权限
     *
     * @param id 权限ID
     * @return 权限
     */
    SysPermission getById(Long id);

    /**
     * 根据编码查询系统权限
     *
     * @param code 权限编码
     * @return 权限
     */
    SysPermission getByCode(String code);

    /**
     * 根据角色ID查询系统权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<SysPermission> listByRoleId(Long roleId);

    /**
     * 分页查询系统权限
     *
     * @param query       查询请求
     * @param pageRequest 分页请求
     * @return 权限分页结果
     */
    PageResult<SysPermission> page(SysPermissionQueryRequest query, PageRequest pageRequest);
}


