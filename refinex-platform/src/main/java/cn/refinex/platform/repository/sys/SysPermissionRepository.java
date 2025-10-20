package cn.refinex.platform.repository.sys;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.domain.dto.request.SysPermissionQueryRequest;
import cn.refinex.platform.domain.entity.sys.SysPermission;
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
 * 系统权限型数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SysPermissionRepository {

    private final JdbcTemplateManager jdbcManager;

    // ================= Read =================

    /**
     * 根据ID查询权限
     *
     * @param id 权限ID
     * @return 权限
     */
    public SysPermission selectById(Long id) {
        String sql = """
                SELECT *
                FROM sys_permission
                WHERE id = :id AND deleted = 0
                """;

        try {
            return jdbcManager.queryObject(sql, Map.of("id", id), true, SysPermission.class);
        } catch (EmptyResultDataAccessException e) {
            log.error("查询权限失败，id: {}", id, e);
            return null;
        }
    }

    /**
     * 根据编码查询权限
     *
     * @param code 权限编码
     * @return 权限
     */
    public SysPermission selectByCode(String code) {
        String sql = """
                SELECT *
                FROM sys_permission
                WHERE permission_code = :code AND deleted = 0
                """;

        try {
            return jdbcManager.queryObject(sql, Map.of("code", code), true, SysPermission.class);
        } catch (EmptyResultDataAccessException e) {
            log.error("根据编码查询权限失败，code: {}", code, e);
            return null;
        }
    }

    /**
     * 根据编码查询是否存在权限（排除指定ID）
     *
     * @param code     权限编码
     * @param excludeId 排除的权限ID
     * @return 是否存在
     */
    public boolean existsByCode(String code, Long excludeId) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(1)
                FROM sys_permission
                WHERE permission_code = :code AND deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();
        params.put("code", code);

        if (excludeId != null) {
            sql.append(" AND id <> :excludeId");
            params.put("excludeId", excludeId);
        }

        Integer c = jdbcManager.queryInt(sql.toString(), params);
        return c != null && c > 0;
    }

    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    public List<SysPermission> listByRoleId(Long roleId) {
        String sql = """
                SELECT p.* FROM sys_permission p
                INNER JOIN sys_role_permission rp ON p.id = rp.permission_id
                WHERE rp.role_id = :roleId AND p.deleted = 0
                ORDER BY p.sort ASC, p.id ASC
                """;

        return jdbcManager.queryList(sql, Map.of("roleId", roleId), true, SysPermission.class);
    }

    /**
     * 分页查询权限列表
     *
     * @param query      查询条件
     * @param pageRequest 分页请求
     * @return 权限分页结果
     */
    public PageResult<SysPermission> pageQuery(SysPermissionQueryRequest query, PageRequest pageRequest) {
        StringBuilder sql = new StringBuilder("""
                SELECT *
                FROM sys_permission
                WHERE deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();

        if (query != null) {
            if (query.getPermissionCode() != null && !query.getPermissionCode().isEmpty()) {
                sql.append(" AND permission_code LIKE :permissionCode");
                params.put("permissionCode", "%" + query.getPermissionCode() + "%");
            }
            if (query.getPermissionName() != null && !query.getPermissionName().isEmpty()) {
                sql.append(" AND permission_name LIKE :permissionName");
                params.put("permissionName", "%" + query.getPermissionName() + "%");
            }
            if (query.getPermissionType() != null && !query.getPermissionType().isEmpty()) {
                sql.append(" AND permission_type = :permissionType");
                params.put("permissionType", query.getPermissionType());
            }
            if (query.getModuleName() != null && !query.getModuleName().isEmpty()) {
                sql.append(" AND module_name LIKE :moduleName");
                params.put("moduleName", "%" + query.getModuleName() + "%");
            }
            if (query.getParentId() != null) {
                sql.append(" AND parent_id = :parentId");
                params.put("parentId", query.getParentId());
            }
            if (query.getStatus() != null) {
                sql.append(" AND status = :status");
                params.put("status", query.getStatus());
            }
        }

        sql.append(" ORDER BY sort ASC, id ASC");

        return jdbcManager.queryPage(sql.toString(), params, pageRequest, SysPermission.class);
    }

    // ================= Write (tx-managed) =================

    /**
     * 插入权限
     *
     * @param tx 事务管理器
     * @param p  权限
     * @return 新插入的权限ID
     */
    public long insert(JdbcTemplateManager tx, SysPermission p) {
        String sql = """
                INSERT INTO sys_permission (
                    permission_code, permission_name, permission_type, parent_id, module_name,
                    resource_path, http_method, create_by, create_time, update_by, update_time,
                    remark, sort, status, extra_data
                ) VALUES (
                    ::permissionCode, :permissionName, :permissionType, :parentId, :moduleName,
                    :resourcePath, :httpMethod, :createBy, :createTime, :updateBy, :updateTime,
                    :remark, :sort, :status, :extraData
                )
                """;

        BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(p);
        return tx.insertAndGetKey(sql, paramSource);
    }

    /**
     * 根据ID更新权限
     *
     * @param tx 事务管理器
     * @param p  权限
     * @return 影响行数
     */
    public int updateById(JdbcTemplateManager tx, SysPermission p) {
        String sql = """
                UPDATE sys_permission SET
                    permission_code   = :permissionCode,
                    permission_name   = :permissionName,
                    permission_type   = :permissionType,
                    parent_id         = :parentId,
                    module_name       = :moduleName,
                    resource_path     = :resourcePath,
                    http_method       = :httpMethod,
                    update_by         = :updateBy,
                    update_time       = :updateTime,
                    version           = :version,
                    remark            = :remark,
                    sort              = :sort,
                    status            = :status,
                    extra_data        = :extraData
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = BeanConverter.beanToMap(p, false, false);
        return tx.update(sql, params);
    }

    /**
     * 根据ID逻辑删除权限
     *
     * @param tx       事务管理器
     * @param id       权限ID
     * @param operatorId 操作人ID
     * @return 影响行数
     */
    public int softDeleteById(JdbcTemplateManager tx, Long id, Long operatorId) {
        String sql = """
                UPDATE sys_permission SET
                    deleted         = 1,
                    update_by       = :updateBy,
                    update_time     = :updateTime
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("updateBy", operatorId);
        params.put("updateTime", LocalDateTime.now());

        return tx.update(sql, params);
    }

    /**
     * 根据ID更新权限状态
     *
     * @param tx       事务管理器
     * @param id       权限ID
     * @param status   状态
     * @param operatorId 操作人ID
     * @return 影响行数
     */
    public int updateStatus(JdbcTemplateManager tx, Long id, Integer status, Long operatorId) {
        String sql = """
                UPDATE sys_permission SET
                    status         = :status,
                    update_by      = :updateBy,
                    update_time    = :updateTime
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


