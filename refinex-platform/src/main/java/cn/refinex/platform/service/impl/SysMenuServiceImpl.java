package cn.refinex.platform.service.impl;

import cn.hutool.core.convert.Convert;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.json.utils.JsonUtils;
import cn.refinex.common.redis.RedisService;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.domain.dto.request.SysMenuCreateRequest;
import cn.refinex.platform.domain.dto.request.SysMenuQueryRequest;
import cn.refinex.platform.domain.dto.request.SysMenuUpdateRequest;
import cn.refinex.platform.domain.entity.sys.SysMenu;
import cn.refinex.platform.repository.sys.SysMenuRepository;
import cn.refinex.platform.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static cn.refinex.common.constants.SystemRedisKeyConstants.Permission.menuTree;

/**
 * 菜单服务实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {

    private final JdbcTemplateManager jdbcManager;
    private final SysMenuRepository sysMenuRepository;
    private final RedisService redisService;

    /**
     * 创建系统菜单
     *
     * @param request    创建请求
     * @param operatorId 操作人ID
     * @return 菜单ID
     */
    @Override
    public Long create(SysMenuCreateRequest request, Long operatorId) {
        // 同一个父级下菜单名称不能重复
        if (sysMenuRepository.existsNameUnderParent(request.getMenuName(), request.getParentId(), null)) {
            throw new BusinessException("同一父级下菜单名称已存在");
        }
        // 路由路径不能重复
        if (StringUtils.isNotBlank(request.getRoutePath()) && sysMenuRepository.existsRoutePath(request.getRoutePath(), null)) {
            throw new BusinessException("路由路径已存在");
        }

        SysMenu m = BeanConverter.toBean(request, SysMenu.class);
        m.setIsExternal(Convert.toInt(request.getIsExternal(), 0));
        m.setIsCached(Convert.toInt(request.getIsCached(), 0));
        m.setIsVisible(Convert.toInt(request.getIsVisible(), 1));
        m.setSort(Convert.toInt(request.getSort(), 0));
        m.setStatus(Convert.toInt(request.getStatus(), 0));
        m.setCreateBy(operatorId);
        m.setCreateTime(LocalDateTime.now());
        m.setUpdateBy(operatorId);
        m.setUpdateTime(LocalDateTime.now());

        long menuId = jdbcManager.executeInTransaction(tx -> sysMenuRepository.insert(tx, m));

        // 菜单树缓存无效化，下次查询时重新构建
        invalidateMenuTreeCache();

        return menuId;
    }

    /**
     * 更新系统菜单
     *
     * @param request    更新请求
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    @Override
    public boolean update(SysMenuUpdateRequest request, Long operatorId) {
        // 检查菜单是否存在
        SysMenu exist = sysMenuRepository.selectById(request.getId());
        if (exist == null) {
            throw new BusinessException("菜单不存在");
        }
        // 检查菜单名称是否重复
        if (sysMenuRepository.existsNameUnderParent(request.getMenuName(), request.getParentId(), request.getId())) {
            throw new BusinessException("同一父级下菜单名称已存在");
        }
        // 检查路由路径是否重复
        if (StringUtils.isNotBlank(request.getRoutePath()) && sysMenuRepository.existsRoutePath(request.getRoutePath(), request.getId())) {
            throw new BusinessException("路由路径已存在");
        }

        exist.setMenuName(request.getMenuName());
        exist.setParentId(request.getParentId());
        exist.setMenuType(request.getMenuType());
        exist.setRoutePath(request.getRoutePath());
        exist.setComponentPath(request.getComponentPath());
        exist.setMenuIcon(request.getMenuIcon());
        exist.setIsExternal(Convert.toInt(request.getIsExternal(), 0));
        exist.setIsCached(Convert.toInt(request.getIsCached(), 0));
        exist.setIsVisible(Convert.toInt(request.getIsVisible(), 1));
        exist.setSort(Convert.toInt(request.getSort(), 0));
        exist.setStatus(Convert.toInt(request.getStatus(), 0));
        exist.setRemark(request.getRemark());
        exist.setExtraData(request.getExtraData());
        exist.setUpdateBy(operatorId);
        exist.setUpdateTime(LocalDateTime.now());
        exist.setVersion(exist.getVersion() == null ? 0 : exist.getVersion() + 1);

        int rows = jdbcManager.executeInTransaction(tx -> sysMenuRepository.updateById(tx, exist));
        if (rows > 0) {
            // 菜单树缓存无效化，下次查询时重新构建
            invalidateMenuTreeCache();
        }
        return rows > 0;
    }

    /**
     * 删除系统菜单
     *
     * @param id         菜单ID
     * @param operatorId 操作人ID
     * @return 是否删除成功
     */
    @Override
    public boolean delete(Long id, Long operatorId) {
        int rows = jdbcManager.executeInTransaction(tx -> sysMenuRepository.softDeleteById(tx, id, operatorId));
        if (rows > 0) {
            // 菜单树缓存无效化，下次查询时重新构建
            invalidateMenuTreeCache();
        }
        return rows > 0;
    }

    /**
     * 更新系统菜单状态
     *
     * @param id         菜单ID
     * @param status     状态（0：禁用，1：启用）
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    @Override
    public boolean updateStatus(Long id, Integer status, Long operatorId) {
        int rows = jdbcManager.executeInTransaction(tx -> sysMenuRepository.updateStatus(tx, id, status, operatorId));
        if (rows > 0) {
            // 菜单树缓存无效化，下次查询时重新构建
            invalidateMenuTreeCache();
        }
        return rows > 0;
    }

    /**
     * 根据ID查询系统菜单
     *
     * @param id 菜单ID
     * @return 菜单
     */
    @Override
    public SysMenu getById(Long id) {
        return sysMenuRepository.selectById(id);
    }

    /**
     * 查询所有系统菜单
     *
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> listAll() {
        // 尝试读取菜单树缓存，后续考虑维护树结构，可在此返回树；当前返回列表
        String key = menuTree();
        try {
            String json = redisService.string().get(key, String.class);
            if (StringUtils.isNotBlank(json)) {
                return JsonUtils.toList(json, SysMenu.class);
            }
        } catch (Exception e) {
            log.warn("读取菜单缓存失败", e);
        }

        // 缓存未命中，查询数据库并写入缓存
        List<SysMenu> list = sysMenuRepository.selectAll();
        writeMenuTreeCache(list);
        return list;
    }

    /**
     * 分页查询系统菜单
     *
     * @param query      查询请求
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    @Override
    public PageResult<SysMenu> page(SysMenuQueryRequest query, PageRequest pageRequest) {
        return sysMenuRepository.pageQuery(query, pageRequest);
    }

    /**
     * 写入菜单树缓存
     *
     * @param list 菜单列表
     */
    private void writeMenuTreeCache(List<SysMenu> list) {
        try {
            String key = menuTree();
            String json = JsonUtils.toJson(list == null ? List.of() : list);
            redisService.string().set(key, json, Duration.ofHours(4));
        } catch (Exception e) {
            log.warn("写入菜单缓存失败", e);
        }
    }

    /**
     * 无效化菜单树缓存
     */
    private void invalidateMenuTreeCache() {
        try {
            redisService.delete(menuTree());
        } catch (Exception e) {
            log.warn("删除菜单缓存失败", e);
        }
    }
}


