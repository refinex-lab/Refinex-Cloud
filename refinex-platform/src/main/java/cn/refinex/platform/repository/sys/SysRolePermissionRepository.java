package cn.refinex.platform.repository.sys;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统角色权限型数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SysRolePermissionRepository {

    private final JdbcTemplateManager jdbcManager;

    // ================ Read ================

    /**
     * 查询角色权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    public List<Long> listPermissionIdsByRoleId(Long roleId) {
        String sql = """
                SELECT permission_id
                FROM sys_role_permission
                WHERE role_id = :roleId
                """;

        return jdbcManager.queryColumn(sql, Map.of("roleId", roleId), true, Long.class);
    }

    /**
     * 查询权限关联的角色ID列表
     *
     * @param permissionId 权限ID
     * @return 角色ID列表
     */
    public List<Long> listRoleIdsByPermissionId(Long permissionId) {
        String sql = """
                SELECT role_id
                FROM sys_role_permission
                WHERE permission_id = :pid
                """;

        return jdbcManager.queryColumn(sql, Map.of("pid", permissionId), true, Long.class);
    }

    // ================ Write (tx-managed) ================

    /**
     * 删除角色权限关联关系
     *
     * @param tx     数据库事务
     * @param roleId 角色ID
     */
    public void deleteByRoleId(JdbcTemplateManager tx, Long roleId) {
        String sql = """
                DELETE FROM sys_role_permission
                WHERE role_id = :roleId
                """;

        tx.delete(sql, Map.of("roleId", roleId));
    }

    /**
     * 批量插入角色权限关联关系
     *
     * @param tx            数据库事务
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     */
    public void batchInsert(JdbcTemplateManager tx, Long roleId, List<Long> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return;
        }

        String sql = """
                INSERT INTO sys_role_permission (role_id, permission_id, create_time)
                VALUES (:roleId, :permissionId, NOW())
                """;

        List<Map<String, Object>> paramList = new ArrayList<>(permissionIds.size());
        for (Long pid : permissionIds) {
            Map<String, Object> m = new HashMap<>();
            m.put("roleId", roleId);
            m.put("permissionId", pid);
            paramList.add(m);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object>[] batch = paramList.toArray(new Map[0]);
        tx.batchUpdate(sql, batch);
    }
}


