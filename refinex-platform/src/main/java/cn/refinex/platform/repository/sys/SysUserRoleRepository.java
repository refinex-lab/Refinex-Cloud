package cn.refinex.platform.repository.sys;

import cn.refinex.common.constants.SystemRoleConstants;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.utils.algorithm.SnowflakeIdGenerator;
import cn.refinex.platform.domain.entity.sys.SysRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户角色数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SysUserRoleRepository {

    private final JdbcTemplateManager jdbcManager;
    private final SnowflakeIdGenerator idGenerator;

    /**
     * 根据用户ID查询用户角色列表
     *
     * @param userId 用户ID
     * @return 用户角色列表
     */
    public List<SysRole> selectRolesByUserId(Long userId) {
        String sql = """
                SELECT r.*
                FROM sys_role r
                INNER JOIN sys_user_role ur ON r.id = ur.role_id
                WHERE ur.user_id = :userId
                AND r.deleted = 0
                AND r.status = 0
                """;

        Map<String, Object> params = Map.of("userId", userId);

        try {
            return jdbcManager.queryList(sql, params, SysRole.class);
        } catch (Exception e) {
            log.error("获取用户角色失败，userId: {}", userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取用户拥有的所有角色编码
     *
     * @param userId 用户ID
     * @return 角色编码集合
     */
    public Set<String> selectRolePermissionsByUserId(Long userId) {
        String sql = """
                SELECT r.role_code
                FROM sys_role r
                INNER JOIN sys_user_role ur ON r.id = ur.role_id
                WHERE ur.user_id = :userId
                AND r.deleted = 0
                AND r.status = 0
                """;

        Map<String, Object> params = Map.of("userId", userId);

        try {
            List<String> roleCodes = jdbcManager.queryList(sql, params, String.class);
            return Set.copyOf(roleCodes);
        } catch (Exception e) {
            log.error("获取用户角色失败，userId: {}", userId, e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取用户拥有的所有权限编码
     *
     * @param userId 用户ID
     * @return 权限编码集合
     */
    public Set<String> selectMenuPermissionsByUserId(Long userId) {
        String sql = """
                SELECT DISTINCT p.permission_code
                FROM sys_permission p
                INNER JOIN sys_role_permission rp ON p.id = rp.permission_id
                INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id
                WHERE ur.user_id = :userId
                AND p.deleted = 0
                AND p.status = 0
                """;

        Map<String, Object> params = Map.of("userId", userId);

        try {
            List<String> permissionCodes = jdbcManager.queryList(sql, params, String.class);
            return Set.copyOf(permissionCodes);
        } catch (Exception e) {
            log.error("获取用户权限失败，userId: {}", userId, e);
            return Collections.emptySet();
        }
    }

    /**
     * 绑定用户角色
     *
     * @param userId   用户ID
     * @param roleIds  角色ID列表
     * @param actionBy 操作人ID
     */
    public void bindUserRole(Long userId, List<Long> roleIds, Long actionBy) {
        String sql = """
                INSERT INTO sys_user_role (id, user_id, role_id, create_by, create_time)
                VALUES (:id, :userId, :roleId, :actionBy, :now)
                """;

        @SuppressWarnings("unchecked")
        Map<String, Object>[] params = roleIds.stream()
                .map(roleId -> Map.of(
                        "id", idGenerator.nextId(),
                        "userId", userId,
                        "roleId", roleId,
                        "actionBy", Objects.isNull(actionBy) ? SystemRoleConstants.SUPER_ADMIN_ID : actionBy,
                        "now", LocalDateTime.now()))
                .toArray(Map[]::new);

        jdbcManager.batchUpdate(sql, params, true);
    }
}