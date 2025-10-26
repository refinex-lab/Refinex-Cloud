package cn.refinex.platform.repository.sys;

import cn.refinex.common.constants.SystemRoleConstants;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.jdbc.service.SensitiveDataService;
import cn.refinex.common.utils.algorithm.SnowflakeIdGenerator;
import cn.refinex.platform.controller.role.dto.response.RoleUserResponseDTO;
import cn.refinex.platform.entity.sys.SysRole;
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
    private final SensitiveDataService sensitiveDataService;

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
     * 绑定用户角色（支持临时授权）
     *
     * @param userId     用户ID
     * @param roleId     角色ID
     * @param validFrom  有效开始时间（可选）
     * @param validUntil 有效结束时间（可选，用于临时授权）
     * @param actionBy   操作人ID
     */
    public void bindUserRole(Long userId, Long roleId, LocalDateTime validFrom, LocalDateTime validUntil, Long actionBy) {
        // 动态构建 SQL，根据参数决定是否包含 valid_from 和 valid_until
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO sys_user_role (user_id, role_id");
        StringBuilder valuesBuilder = new StringBuilder(" VALUES (:userId, :roleId");
        StringBuilder updateBuilder = new StringBuilder(" ON DUPLICATE KEY UPDATE ");
        
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("roleId", roleId);
        params.put("actionBy", Objects.isNull(actionBy) ? SystemRoleConstants.SUPER_ADMIN_ID : actionBy);
        params.put("now", LocalDateTime.now());
        
        boolean hasUpdate = false;
        
        // 如果 validFrom 不为 null，添加到 SQL 中
        if (validFrom != null) {
            sqlBuilder.append(", valid_from");
            valuesBuilder.append(", :validFrom");
            params.put("validFrom", validFrom);
            
            if (hasUpdate) {
                updateBuilder.append(", ");
            }
            updateBuilder.append("valid_from = VALUES(valid_from)");
            hasUpdate = true;
        }
        
        // 如果 validUntil 不为 null，添加到 SQL 中
        if (validUntil != null) {
            sqlBuilder.append(", valid_until");
            valuesBuilder.append(", :validUntil");
            params.put("validUntil", validUntil);
            
            if (hasUpdate) {
                updateBuilder.append(", ");
            }
            updateBuilder.append("valid_until = VALUES(valid_until)");
            hasUpdate = true;
        }
        
        // 添加固定字段
        sqlBuilder.append(", create_by, create_time)");
        valuesBuilder.append(", :actionBy, :now)");
        
        // 添加固定的更新字段
        if (hasUpdate) {
            updateBuilder.append(", ");
        }
        updateBuilder.append("create_by = VALUES(create_by), create_time = VALUES(create_time)");
        
        String sql = sqlBuilder + valuesBuilder.toString() + updateBuilder;
        
        jdbcManager.update(sql, params);
    }

    /**
     * 批量绑定用户角色
     *
     * @param roleId   角色ID
     * @param userIds  用户ID列表
     * @param actionBy 操作人ID
     */
    public void bindUserRole(Long roleId, List<Long> userIds, Long actionBy) {
        String sql = """
                INSERT IGNORE INTO sys_user_role (id, user_id, role_id, create_by, create_time)
                VALUES (:id, :userId, :roleId, :actionBy, :now)
                """;

        @SuppressWarnings("unchecked")
        Map<String, Object>[] params = userIds.stream()
                .map(userId -> Map.of(
                        "id", idGenerator.nextId(),
                        "userId", userId,
                        "roleId", roleId,
                        "actionBy", Objects.isNull(actionBy) ? SystemRoleConstants.SUPER_ADMIN_ID : actionBy,
                        "now", LocalDateTime.now()))
                .toArray(Map[]::new);

        jdbcManager.batchUpdate(sql, params, true);
    }

    /**
     * 解绑用户角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    public int unbindUserRole(Long userId, Long roleId) {
        String sql = "DELETE FROM sys_user_role WHERE user_id = :userId AND role_id = :roleId";
        Map<String, Object> params = Map.of("userId", userId, "roleId", roleId);
        return jdbcManager.update(sql, params);
    }

    /**
     * 根据角色ID查询关联的用户ID列表
     *
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    public List<Long> selectUserIdsByRoleId(Long roleId) {
        String sql = """
                SELECT DISTINCT user_id
                FROM sys_user_role
                WHERE role_id = :roleId
                """;

        Map<String, Object> params = Map.of("roleId", roleId);

        try {
            return jdbcManager.queryList(sql, params, Long.class);
        } catch (Exception e) {
            log.error("查询角色关联用户失败，roleId: {}", roleId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 检查用户是否已绑定该角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否已绑定
     */
    public boolean checkUserRoleExists(Long userId, Long roleId) {
        String sql = "SELECT COUNT(*) FROM sys_user_role WHERE user_id = :userId AND role_id = :roleId";
        Map<String, Object> params = Map.of("userId", userId, "roleId", roleId);

        try {
            return jdbcManager.queryInt(sql, params) > 0;
        } catch (Exception e) {
            log.error("检查用户角色关联失败", e);
            return false;
        }
    }

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
    public PageResult<RoleUserResponseDTO> pageQueryRoleUsers(Long roleId, String username, String nickname, String mobile, String email, PageRequest pageReq) {
        StringBuilder sqlBuilder = new StringBuilder("""
                SELECT
                    u.id AS user_id,
                    u.username,
                    u.nickname,
                    u.mobile,
                    u.email,
                    u.user_status,
                    ur.role_id,
                    ur.valid_from,
                    ur.valid_until,
                    ur.create_time AS bind_time
                FROM sys_user u
                INNER JOIN sys_user_role ur ON u.id = ur.user_id
                """);

        // 如果需要搜索手机号或邮箱，需要 JOIN sys_sensitive 表
        boolean needMobileJoin = mobile != null && !mobile.isEmpty();
        boolean needEmailJoin = email != null && !email.isEmpty();

        if (needMobileJoin) {
            sqlBuilder.append(" INNER JOIN sys_sensitive sm ON u.id = sm.row_guid AND sm.table_name = 'sys_user' AND sm.field_code = 'mobile'");
        }
        if (needEmailJoin) {
            sqlBuilder.append(" INNER JOIN sys_sensitive se ON u.id = se.row_guid AND se.table_name = 'sys_user' AND se.field_code = 'email'");
        }

        sqlBuilder.append(" WHERE ur.role_id = :roleId AND u.deleted = 0");

        Map<String, Object> params = new HashMap<>();
        params.put("roleId", roleId);

        if (username != null && !username.isEmpty()) {
            sqlBuilder.append(" AND u.username LIKE :username");
            params.put("username", "%" + username + "%");
        }

        if (nickname != null && !nickname.isEmpty()) {
            sqlBuilder.append(" AND u.nickname LIKE :nickname");
            params.put("nickname", "%" + nickname + "%");
        }

        if (needMobileJoin) {
            sqlBuilder.append(" AND sm.encrypted_value = :mobileEncrypted");
            try {
                params.put("mobileEncrypted", sensitiveDataService.encryptValue(mobile));
            } catch (Exception e) {
                log.error("加密手机号失败", e);
                // 如果加密失败，返回空结果
                return new PageResult<>(Collections.emptyList(), 0, pageReq.getPageNum(), pageReq.getPageSize());
            }
        }

        if (needEmailJoin) {
            sqlBuilder.append(" AND se.encrypted_value = :emailEncrypted");
            try {
                params.put("emailEncrypted", sensitiveDataService.encryptValue(email));
            } catch (Exception e) {
                log.error("加密邮箱失败", e);
                // 如果加密失败，返回空结果
                return new PageResult<>(Collections.emptyList(), 0, pageReq.getPageNum(), pageReq.getPageSize());
            }
        }

        if (pageReq.getOrderBy() == null || pageReq.getOrderBy().isEmpty()) {
            sqlBuilder.append(" ORDER BY ur.create_time DESC");
        }

        return jdbcManager.queryPage(sqlBuilder.toString(), params, pageReq, RoleUserResponseDTO.class);
    }

    /**
     * 清理过期的临时授权（物理删除）
     *
     * @return 删除的记录数
     */
    public int deleteExpiredTemporaryAuthorizations() {
        String sql = """
                DELETE FROM sys_user_role
                WHERE valid_until IS NOT NULL
                AND valid_until < :now
                """;

        Map<String, Object> params = Map.of("now", LocalDateTime.now());

        try {
            int deleted = jdbcManager.update(sql, params);
            if (deleted > 0) {
                log.info("清理过期临时授权成功，删除记录数: {}", deleted);
            }
            return deleted;
        } catch (Exception e) {
            log.error("清理过期临时授权失败", e);
            return 0;
        }
    }
}