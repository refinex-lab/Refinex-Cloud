package cn.refinex.platform.repository.sys;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public List<Long> listPermissionIdsByRoleId(Long roleId) {
        String sql = """
                SELECT permission_id FROM sys_role_permission WHERE role_id = :roleId
                """;
        return jdbcManager.queryColumn(sql, Map.of("roleId", roleId), true, Long.class);
    }

    public List<Long> listRoleIdsByPermissionId(Long permissionId) {
        String sql = """
                SELECT role_id FROM sys_role_permission WHERE permission_id = :pid
                """;
        return jdbcManager.queryColumn(sql, Map.of("pid", permissionId), true, Long.class);
    }

    // ================ Write (tx-managed) ================

    public int deleteByRoleId(JdbcTemplateManager tx, Long roleId) {
        String sql = """
                DELETE FROM sys_role_permission WHERE role_id = :roleId
                """;
        return tx.delete(sql, Map.of("roleId", roleId));
    }

    public int[] batchInsert(JdbcTemplateManager tx, Long roleId, List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new int[0];
        }
        String sql = """
                INSERT INTO sys_role_permission (id, role_id, permission_id, create_time)
                VALUES (:id, :roleId, :permissionId, NOW())
                """;
        List<Map<String, Object>> paramList = new ArrayList<>(permissionIds.size());
        for (Long pid : permissionIds) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", System.nanoTime()); // 这里建议由上层传入ID或使用统一ID生成器
            m.put("roleId", roleId);
            m.put("permissionId", pid);
            paramList.add(m);
        }
        @SuppressWarnings("unchecked")
        Map<String, Object>[] batch = paramList.toArray(new Map[0]);
        return tx.batchUpdate(sql, batch);
    }
}


