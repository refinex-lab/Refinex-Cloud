package cn.refinex.platform.repository.sys;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.controller.auth.dto.request.SysMenuQueryRequestDTO;
import cn.refinex.platform.entity.sys.SysMenu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统菜单型数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SysMenuRepository {

    private final JdbcTemplateManager jdbcManager;

    // ================= Read =================

    /**
     * 根据ID查询菜单
     *
     * @param id 菜单ID
     * @return 菜单
     */
    public SysMenu selectById(Long id) {
        String sql = """
                SELECT *
                FROM sys_menu
                WHERE id = :id AND deleted = 0
                """;

        try {
            return jdbcManager.queryObject(sql, Map.of("id", id), true, SysMenu.class);
        } catch (EmptyResultDataAccessException e) {
            log.error("查询菜单失败，id: {}", id, e);
            return null;
        }
    }

    /**
     * 查询所有菜单
     *
     * @return 菜单列表
     */
    public List<SysMenu> selectAll() {
        String sql = """
                SELECT *
                FROM sys_menu
                WHERE deleted = 0
                ORDER BY sort ASC, id ASC
                """;

        return jdbcManager.queryList(sql, Map.of(), true, SysMenu.class);
    }

    /**
     * 根据父ID查询子菜单
     *
     * @param parentId 父菜单ID
     * @return 子菜单列表
     */
    public List<SysMenu> selectByParentId(Long parentId) {
        String sql = """
                SELECT *
                FROM sys_menu
                WHERE parent_id = :parentId AND deleted = 0
                ORDER BY sort ASC, id ASC
                """;

        return jdbcManager.queryList(sql, Map.of("parentId", parentId), true, SysMenu.class);
    }

    /**
     * 查询所有启用的菜单
     *
     * @return 启用的菜单列表
     */
    public List<SysMenu> selectEnabledList() {
        String sql = """
                SELECT *
                FROM sys_menu
                WHERE deleted = 0 AND status = 0
                ORDER BY sort ASC, id ASC
                """;

        return jdbcManager.queryList(sql, Map.of(), true, SysMenu.class);
    }

    /**
     * 检查菜单名称是否存在于指定父菜单下（不包括指定ID）
     *
     * @param menuName 菜单名称
     * @param parentId 父菜单ID
     * @param excludeId 排除的菜单ID
     * @return 是否存在
     */
    public boolean existsNameUnderParent(String menuName, Long parentId, Long excludeId) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(1)
                FROM sys_menu
                WHERE menu_name = :menuName AND parent_id = :parentId AND deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();
        params.put("menuName", menuName);
        params.put("parentId", parentId);

        if (excludeId != null) {
            sql.append(" AND id <> :excludeId");
            params.put("excludeId", excludeId);
        }

        Integer c = jdbcManager.queryInt(sql.toString(), params);
        return c != null && c > 0;
    }

    /**
     * 检查路由路径是否存在（不包括指定ID）
     *
     * @param routePath 路由路径
     * @param excludeId 排除的菜单ID
     * @return 是否存在
     */
    public boolean existsRoutePath(String routePath, Long excludeId) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(1)
                FROM sys_menu
                WHERE route_path = :routePath AND deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();
        params.put("routePath", routePath);

        if (excludeId != null) {
            sql.append(" AND id <> :excludeId");
            params.put("excludeId", excludeId);
        }

        Integer c = jdbcManager.queryInt(sql.toString(), params);
        return c != null && c > 0;
    }

    /**
     * 分页查询系统菜单
     *
     * @param query 查询条件
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    public PageResult<SysMenu> pageQuery(SysMenuQueryRequestDTO query, PageRequest pageRequest) {
        StringBuilder sql = new StringBuilder("""
                SELECT *
                FROM sys_menu
                WHERE deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();

        if (query != null) {
            if (query.getMenuName() != null && !query.getMenuName().isEmpty()) {
                sql.append(" AND menu_name LIKE :menuName");
                params.put("menuName", "%" + query.getMenuName() + "%");
            }
            if (query.getMenuType() != null && !query.getMenuType().isEmpty()) {
                sql.append(" AND menu_type = :menuType");
                params.put("menuType", query.getMenuType());
            }
            if (query.getParentId() != null) {
                sql.append(" AND parent_id = :parentId");
                params.put("parentId", query.getParentId());
            }
            if (query.getIsVisible() != null) {
                sql.append(" AND is_visible = :isVisible");
                params.put("isVisible", query.getIsVisible());
            }
            if (query.getStatus() != null) {
                sql.append(" AND status = :status");
                params.put("status", query.getStatus());
            }
        }

        sql.append(" ORDER BY sort ASC, id ASC");
        return jdbcManager.queryPage(sql.toString(), params, pageRequest, SysMenu.class);
    }

    // ================= Write (tx-managed) =================

    /**
     * 插入系统菜单
     *
     * @param tx 数据库事务管理器
     * @param menu 系统菜单
     * @return 新插入的菜单ID
     */
    public long insert(JdbcTemplateManager tx, SysMenu menu) {
        String sql = """
                INSERT INTO sys_menu (
                    menu_name, parent_id, menu_type, route_path, component_path, menu_icon,
                    is_external, is_cached, is_visible, create_by, create_time, update_by, update_time,
                    remark, sort, status, extra_data
                ) VALUES (
                    :menuName, :parentId, :menuType, :routePath, :componentPath, :menuIcon,
                    :isExternal, :isCached, :isVisible, :createBy, :createTime, :updateBy, :updateTime,
                    :remark, :sort, :status, :extraData
                )
                """;

        BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(menu);
        return tx.insertAndGetKey(sql, paramSource);
    }

    /**
     * 更新系统菜单（根据ID）
     *
     * @param tx 数据库事务管理器
     * @param menu 系统菜单
     * @return 影响行数
     */
    public int updateById(JdbcTemplateManager tx, SysMenu menu) {
        String sql = """
                UPDATE sys_menu SET
                    menu_name      = :menuName,
                    parent_id      = :parentId,
                    menu_type      = :menuType,
                    route_path     = :routePath,
                    component_path = :componentPath,
                    menu_icon      = :menuIcon,
                    is_external    = :isExternal,
                    is_cached      = :isCached,
                    is_visible     = :isVisible,
                    update_by      = :updateBy,
                    update_time    = :updateTime,
                    version        = :version,
                    remark         = :remark,
                    sort           = :sort,
                    status         = :status,
                    extra_data     = :extraData
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = BeanConverter.beanToMap(menu, false, false);
        return tx.update(sql, params);
    }

    /**
     * 逻辑删除系统菜单（根据ID）
     *
     * @param tx 数据库事务管理器
     * @param id 系统菜单ID
     * @param operatorId 操作人ID
     * @return 影响行数
     */
    public int softDeleteById(JdbcTemplateManager tx, Long id, Long operatorId) {
        String sql = """
                UPDATE sys_menu SET
                    deleted      = 1,
                    update_by    = :updateBy,
                    update_time  = :updateTime
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("updateBy", operatorId);
        params.put("updateTime", LocalDateTime.now());

        return tx.update(sql, params);
    }

    /**
     * 更新系统菜单状态（根据ID）
     *
     * @param tx 数据库事务管理器
     * @param id 系统菜单ID
     * @param status 状态：0正常,1停用
     * @param operatorId 操作人ID
     * @return 影响行数
     */
    public int updateStatus(JdbcTemplateManager tx, Long id, Integer status, Long operatorId) {
        String sql = """
                UPDATE sys_menu SET
                    status      = :status,
                    update_by   = :updateBy,
                    update_time = :updateTime
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("status", status);
        params.put("updateBy", operatorId);
        params.put("updateTime", LocalDateTime.now());

        return tx.update(sql, params);
    }
}


