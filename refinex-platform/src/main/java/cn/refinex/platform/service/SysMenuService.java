package cn.refinex.platform.service;

import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.platform.controller.auth.dto.request.SysMenuCreateRequestDTO;
import cn.refinex.platform.controller.auth.dto.request.SysMenuQueryRequestDTO;
import cn.refinex.platform.controller.auth.dto.request.SysMenuUpdateRequestDTO;
import cn.refinex.platform.entity.sys.SysMenu;

import java.util.List;

/**
 * 系统菜单服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface SysMenuService {

    /**
     * 创建系统菜单
     *
     * @param request    创建请求
     * @param operatorId 操作人ID
     * @return 菜单ID
     */
    Long create(SysMenuCreateRequestDTO request, Long operatorId);

    /**
     * 更新系统菜单
     *
     * @param id         菜单ID
     * @param request    更新请求
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    boolean update(Long id, SysMenuUpdateRequestDTO request, Long operatorId);

    /**
     * 删除系统菜单
     *
     * @param id         菜单ID
     * @param operatorId 操作人ID
     * @return 是否删除成功
     */
    boolean delete(Long id, Long operatorId);

    /**
     * 更新系统菜单状态
     *
     * @param id         菜单ID
     * @param status     状态（0：禁用，1：启用）
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    boolean updateStatus(Long id, Integer status, Long operatorId);

    /**
     * 根据ID查询系统菜单
     *
     * @param id 菜单ID
     * @return 菜单
     */
    SysMenu getById(Long id);

    /**
     * 查询所有系统菜单
     *
     * @return 菜单列表
     */
    List<SysMenu> listAll();

    /**
     * 分页查询系统菜单
     *
     * @param query       查询请求
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<SysMenu> page(SysMenuQueryRequestDTO query, PageRequest pageRequest);
}


