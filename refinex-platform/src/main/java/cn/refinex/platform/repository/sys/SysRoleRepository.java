package cn.refinex.platform.repository.sys;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.platform.entity.sys.SysRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统角色仓库接口
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SysRoleRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 根据角色ID检查角色是否存在
     *
     * @param roleId 角色ID
     * @return 如果角色存在则返回true，否则返回false
     */
    public boolean checkRoleExistsById(Long roleId) {
        String sql = "SELECT COUNT(*) FROM sys_role WHERE id = :roleId AND deleted = 0";
        Map<String, Object> params = Map.of("roleId", roleId);

        try {
            return jdbcManager.queryInt(sql, params) > 0;
        } catch (Exception e) {
            log.error("检查角色是否存在失败", e);
            return false;
        }
    }

    /**
     * 根据角色编码检查角色是否存在
     *
     * @param roleCode 角色编码
     * @return 如果角色存在则返回true，否则返回false
     */
    public boolean checkRoleExistsByCode(String roleCode) {
        String sql = "SELECT COUNT(*) FROM sys_role WHERE role_code = :roleCode AND deleted = 0";
        Map<String, Object> params = Map.of("roleCode", roleCode);

        try {
            return jdbcManager.queryInt(sql, params) > 0;
        } catch (Exception e) {
            log.error("根据角色编码检查角色是否存在失败", e);
            return false;
        }
    }

    /**
     * 根据角色编码检查角色是否存在（排除指定ID）
     *
     * @param roleCode  角色编码
     * @param excludeId 要排除的角色ID
     * @return 如果角色存在则返回true，否则返回false
     */
    public boolean checkRoleExistsByCodeExcludeId(String roleCode, Long excludeId) {
        String sql = "SELECT COUNT(*) FROM sys_role WHERE role_code = :roleCode AND id != :excludeId AND deleted = 0";
        Map<String, Object> params = Map.of("roleCode", roleCode, "excludeId", excludeId);

        try {
            return jdbcManager.queryInt(sql, params) > 0;
        } catch (Exception e) {
            log.error("根据角色编码检查角色是否存在失败", e);
            return false;
        }
    }

    /**
     * 检查角色是否为内置角色
     *
     * @param roleId 角色ID
     * @return 如果是内置角色返回true，否则返回false
     */
    public boolean checkIsBuiltinRole(Long roleId) {
        String sql = "SELECT is_builtin FROM sys_role WHERE id = :roleId AND deleted = 0";
        Map<String, Object> params = Map.of("roleId", roleId);

        try {
            Integer isBuiltin = jdbcManager.queryInt(sql, params);
            return isBuiltin != null && isBuiltin == 1;
        } catch (Exception e) {
            log.error("检查是否为内置角色失败，roleId: {}", roleId, e);
            return false;
        }
    }

    /**
     * 检查角色下是否有绑定用户
     *
     * @param roleId 角色ID
     * @return 如果有绑定用户返回true，否则返回false
     */
    public boolean checkRoleHasUsers(Long roleId) {
        String sql = "SELECT COUNT(*) FROM sys_user_role WHERE role_id = :roleId";
        Map<String, Object> params = Map.of("roleId", roleId);

        try {
            return jdbcManager.queryInt(sql, params) > 0;
        } catch (Exception e) {
            log.error("检查角色是否有绑定用户失败，roleId: {}", roleId, e);
            return false;
        }
    }

    /**
     * 创建角色
     *
     * @param role 角色对象
     * @return 受影响的行数
     */
    public int insertRole(SysRole role) {
        String sql = """
                INSERT INTO sys_role (
                    role_code, role_name, role_type, data_scope, is_builtin,
                    priority, create_by, create_time, deleted, version, remark, sort, status
                ) VALUES (
                    :roleCode, :roleName, :roleType, :dataScope, :isBuiltin,
                    :priority, :createBy, :createTime, :deleted, :version, :remark, :sort, :status
                )
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("roleCode", role.getRoleCode());
        params.put("roleName", role.getRoleName());
        params.put("roleType", role.getRoleType() != null ? role.getRoleType() : 1);
        params.put("dataScope", role.getDataScope() != null ? role.getDataScope() : 3);
        params.put("isBuiltin", role.getIsBuiltin() != null ? role.getIsBuiltin() : 0);
        params.put("priority", role.getSort() != null ? role.getSort() : 0);
        params.put("createBy", role.getCreateBy());
        params.put("createTime", LocalDateTime.now());
        params.put("deleted", 0);
        params.put("version", 0);
        params.put("remark", role.getRemark() != null ? role.getRemark() : "");
        params.put("sort", role.getSort() != null ? role.getSort() : 0);
        params.put("status", role.getStatus() != null ? role.getStatus() : 0);

        return jdbcManager.update(sql, params);
    }

    /**
     * 更新角色
     *
     * @param role 角色对象
     * @return 受影响的行数
     */
    public int updateRole(SysRole role) {
        String sql = """
                UPDATE sys_role SET
                    role_name   = :roleName,
                    role_type   = :roleType,
                    data_scope  = :dataScope,
                    priority    = :priority,
                    remark      = :remark,
                    sort        = :sort,
                    status      = :status,
                    update_by   = :updateBy,
                    update_time = :updateTime
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", role.getId());
        params.put("roleName", role.getRoleName());
        params.put("roleType", role.getRoleType());
        params.put("dataScope", role.getDataScope());
        params.put("priority", role.getSort() != null ? role.getSort() : 0);
        params.put("remark", role.getRemark() != null ? role.getRemark() : "");
        params.put("sort", role.getSort() != null ? role.getSort() : 0);
        params.put("status", role.getStatus());
        params.put("updateBy", role.getUpdateBy());
        params.put("updateTime", LocalDateTime.now());

        return jdbcManager.update(sql, params);
    }

    /**
     * 更新角色状态
     *
     * @param roleId   角色ID
     * @param status   状态
     * @param updateBy 更新人ID
     * @return 受影响的行数
     */
    public int updateRoleStatus(Long roleId, Integer status, Long updateBy) {
        String sql = """
                UPDATE sys_role SET
                    status      = :status,
                    update_by   = :updateBy,
                    update_time = :updateTime
                WHERE id = :roleId AND deleted = 0
                """;

        Map<String, Object> params = Map.of(
                "roleId", roleId,
                "status", status,
                "updateBy", updateBy,
                "updateTime", LocalDateTime.now()
        );

        return jdbcManager.update(sql, params);
    }

    /**
     * 逻辑删除角色
     *
     * @param roleId   角色ID
     * @param updateBy 更新人ID
     * @return 受影响的行数
     */
    public int deleteRole(Long roleId, Long updateBy) {
        String sql = """
                UPDATE sys_role SET
                    deleted     = 1,
                    update_by   = :updateBy,
                    update_time = :updateTime
                WHERE id = :roleId AND deleted = 0
                """;

        Map<String, Object> params = Map.of(
                "roleId", roleId,
                "updateBy", updateBy,
                "updateTime", LocalDateTime.now()
        );

        return jdbcManager.update(sql, params);
    }

    /**
     * 根据ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象
     */
    public SysRole selectRoleById(Long roleId) {
        String sql = """
                SELECT id, role_code, role_name, role_type, data_scope, is_builtin, priority,
                       create_by, create_time, update_by, update_time, deleted, version, remark, sort, status
                FROM sys_role
                WHERE id = :roleId AND deleted = 0
                """;

        Map<String, Object> params = Map.of("roleId", roleId);

        try {
            return jdbcManager.queryObject(sql, params, SysRole.class);
        } catch (Exception e) {
            log.error("根据ID查询角色失败，roleId: {}", roleId, e);
            return null;
        }
    }

    /**
     * 根据角色编码查询角色
     *
     * @param roleCode 角色编码
     * @return 角色对象
     */
    public SysRole selectRoleByCode(String roleCode) {
        String sql = """
                SELECT id, role_code, role_name, role_type, data_scope, is_builtin, priority,
                       create_by, create_time, update_by, update_time, deleted, version, remark, sort, status
                FROM sys_role
                WHERE role_code = :roleCode AND deleted = 0
                """;

        Map<String, Object> params = Map.of("roleCode", roleCode);

        try {
            return jdbcManager.queryObject(sql, params, SysRole.class);
        } catch (Exception e) {
            log.error("根据角色编码查询角色失败，roleCode: {}", roleCode, e);
            return null;
        }
    }

    /**
     * 查询所有启用的角色
     *
     * @return 角色列表
     */
    public List<SysRole> selectEnabledRoles() {
        String sql = """
                SELECT id, role_code, role_name, role_type, data_scope, is_builtin, priority,
                       create_by, create_time, update_by, update_time, deleted, version, remark, sort, status
                FROM sys_role
                WHERE deleted = 0 AND status = 0
                ORDER BY sort ASC, create_time DESC
                """;

        try {
            return jdbcManager.queryList(sql, Collections.emptyMap(), SysRole.class);
        } catch (Exception e) {
            log.error("查询所有启用的角色失败", e);
            return Collections.emptyList();
        }
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
    public PageResult<SysRole> pageQueryRoles(String roleCode, String roleName, Integer roleType, Integer status, PageRequest pageReq) {
        StringBuilder sqlBuilder = new StringBuilder("""
                SELECT id, role_code, role_name, role_type, data_scope, is_builtin, priority,
                       create_by, create_time, update_by, update_time, deleted, version, remark, sort, status
                FROM sys_role
                WHERE deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();

        if (roleCode != null && !roleCode.isEmpty()) {
            sqlBuilder.append(" AND role_code LIKE :roleCode");
            params.put("roleCode", "%" + roleCode + "%");
        }

        if (roleName != null && !roleName.isEmpty()) {
            sqlBuilder.append(" AND role_name LIKE :roleName");
            params.put("roleName", "%" + roleName + "%");
        }

        if (roleType != null) {
            sqlBuilder.append(" AND role_type = :roleType");
            params.put("roleType", roleType);
        }

        if (status != null) {
            sqlBuilder.append(" AND status = :status");
            params.put("status", status);
        }

        // 只有在 PageRequest 没有指定排序时才使用默认排序
        if (pageReq.getOrderBy() == null || pageReq.getOrderBy().isEmpty()) {
            sqlBuilder.append(" ORDER BY sort ASC, create_time DESC");
        }

        return jdbcManager.queryPage(sqlBuilder.toString(), params, pageReq, SysRole.class);
    }

    /**
     * 获取角色最大排序值
     *
     * @return 最大排序值
     */
    public Integer selectMaxSort() {
        String sql = "SELECT COALESCE(MAX(sort), 0) FROM sys_role WHERE deleted = 0";

        try {
            return jdbcManager.queryInt(sql, Collections.emptyMap());
        } catch (Exception e) {
            log.error("获取角色最大排序值失败", e);
            return 0;
        }
    }
}
