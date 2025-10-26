package cn.refinex.platform.repository.sys;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.entity.sys.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SysUserRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 根据用户 ID 查询用户信息
     *
     * @param userId 用户 ID
     * @return 用户实体，不存在返回 null
     */
    public SysUser selectById(Long userId) {
        String sql = """
                SELECT *
                FROM sys_user
                WHERE id = :userId AND deleted = 0
                """;

        Map<String, Object> params = Map.of("userId", userId);

        try {
            return jdbcManager.queryObject(sql, params, true, SysUser.class);
        } catch (Exception e) {
            log.error("根据用户 ID 查询用户失败，userId: {}", userId, e);
            return null;
        }
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户实体，不存在返回 null
     */
    public SysUser selectByUserName(String username) {
        String sql = """
                SELECT *
                FROM sys_user
                WHERE username = :username AND deleted = 0
                """;

        Map<String, Object> params = Map.of("username", username);

        try {
            return jdbcManager.queryObject(sql, params, true, SysUser.class);
        } catch (Exception e) {
            log.error("根据用户名查询用户失败，username: {}", username, e);
            return null;
        }
    }

    /**
     * 根据手机号查询用户信息
     *
     * @param phoneEncrypted 加密手机号
     * @return 用户实体，不存在返回 null
     */
    public SysUser selectByMobile(String phoneEncrypted) {
        String sql = """
                SELECT *
                FROM sys_user AS u
                INNER JOIN sys_sensitive AS s ON u.user_id = s.row_guid AND s.table_name = 'sys_user' AND s.field_code = 'mobile'
                WHERE s.encrypted_value = :phoneEncrypted
                """;

        Map<String, Object> params = Map.of("phoneEncrypted", phoneEncrypted);

        try {
            return jdbcManager.queryObject(sql, params, true, SysUser.class);
        } catch (Exception e) {
            log.error("根据手机号查询用户失败，phoneEncrypted: {}", phoneEncrypted, e);
            return null;
        }
    }

    /**
     * 根据邮箱查询用户信息
     *
     * @param emailEncrypted 加密邮箱
     * @return 用户实体，不存在返回 null
     */
    public SysUser selectByEmail(String emailEncrypted) {
        String sql = """
                SELECT *
                FROM sys_user AS u
                INNER JOIN sys_sensitive AS s ON u.user_id = s.row_guid AND s.table_name = 'sys_user' AND s.field_code = 'email'
                WHERE s.encrypted_value = :emailEncrypted
                """;

        Map<String, Object> params = Map.of("emailEncrypted", emailEncrypted);

        try {
            return jdbcManager.queryObject(sql, params, true, SysUser.class);
        } catch (Exception e) {
            log.error("根据邮箱查询用户失败，emailEncrypted: {}", emailEncrypted, e);
            return null;
        }
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 存在返回 1，不存在返回 0
     */
    public int checkUsernameExist(String username) {
        String sql = """
                SELECT COUNT(*)
                FROM sys_user
                WHERE username = :username
                """;

        Map<String, Object> params = Map.of("username", username);

        try {
            return jdbcManager.queryInt(sql, params);
        } catch (Exception e) {
            log.error("检查用户名是否存在失败，username: {}", username, e);
            return 0;
        }
    }

    /**
     * 检查手机号是否存在
     *
     * @param phoneEncrypted 加密手机号
     * @return 存在返回 1，不存在返回 0
     */
    public int checkPhoneExist(String phoneEncrypted) {
        String sql = """
                SELECT COUNT(*)
                FROM sys_user AS u
                INNER JOIN sys_sensitive AS s ON u.user_id = s.row_guid AND s.table_name = 'sys_user' AND s.field_code = 'mobile'
                WHERE s.encrypted_value = :phoneEncrypted
                """;

        Map<String, Object> params = Map.of("phoneEncrypted", phoneEncrypted);

        try {
            return jdbcManager.queryInt(sql, params);
        } catch (Exception e) {
            log.error("检查手机号是否存在失败", e);
            return 0;
        }
    }

    /**
     * 检查邮箱是否存在
     *
     * @param emailEncrypted 加密邮箱
     * @return 存在返回 1，不存在返回 0
     */
    public int checkEmailExist(String emailEncrypted) {
        String sql = """
                SELECT COUNT(*)
                FROM sys_user AS u
                INNER JOIN sys_sensitive AS s ON u.user_id = s.row_guid AND s.table_name = 'sys_user' AND s.field_code = 'email'
                WHERE s.encrypted_value = :emailEncrypted
                """;

        Map<String, Object> params = Map.of("emailEncrypted", emailEncrypted);

        try {
            return jdbcManager.queryInt(sql, params);
        } catch (Exception e) {
            log.error("检查手机号是否存在失败", e);
            return 0;
        }
    }

    /**
     * 注册用户
     *
     * @param sysUser 用户实体
     */
    public void register(SysUser sysUser) {
        String sql = """
                INSERT INTO sys_user (
                    id, username, mobile, email, password, nickname, user_status,
                    register_source, create_by, create_time, update_time
                )
                VALUES (
                    :id, :username, :mobile, :email, :password, :nickname, :userStatus,
                    :registerSource, :createBy, :createTime, :updateTime
                )
                """;

        Map<String, Object> params = BeanConverter.beanToMap(sysUser, false, false);
        jdbcManager.insert(sql, params, true);
    }

    /**
     * 根据用户名模糊查询用户名列表
     *
     * @param username 用户名关键词
     * @param limit    查询数量限制
     * @return 用户名列表
     */
    public java.util.List<String> searchUsernamesByKeyword(String username, int limit) {
        String sql = """
                SELECT DISTINCT username
                FROM sys_user
                WHERE username LIKE CONCAT('%', :username, '%') AND deleted = 0
                ORDER BY username
                LIMIT :limit
                """;

        Map<String, Object> params = Map.of(
                "username", username,
                "limit", limit
        );

        try {
            return jdbcManager.queryColumn(sql, params, String.class);
        } catch (Exception e) {
            log.error("模糊查询用户名失败，username: {}", username, e);
            return java.util.Collections.emptyList();
        }
    }

    /**
     * 初始化超级管理员
     *
     * @param superAdmin 超级管理员实体
     */
    public void initSuperAdmin(SysUser superAdmin) {
        String sql = """
                INSERT INTO sys_user (
                    id, username, mobile, email, password, nickname, user_status, user_type,
                    register_source, create_by, create_time, update_by, update_time, sort, status
                )
                VALUES (
                    :id, :username, :mobile, :email, :password, :nickname, :userStatus, :userType,
                    :registerSource, :createBy, :createTime, :updateBy, :updateTime, :sort, :status
                )
                """;

        Map<String, Object> params = BeanConverter.beanToMap(superAdmin, false, false);
        jdbcManager.insert(sql, params, true);
    }

    /**
     * 更新用户密码
     *
     * @param emailEncrypted 加密邮箱
     * @param password       加密后的密码
     * @return 更新影响的行数
     */
    public int updatePassword(String emailEncrypted, String password) {
        String sql = """
                UPDATE sys_user AS u
                INNER JOIN sys_sensitive AS s ON u.user_id = s.row_guid AND s.table_name = 'sys_user' AND s.field_code = 'email'
                SET u.password = :password, u.update_time = :updateTime
                WHERE s.encrypted_value = :emailEncrypted
                """;

        Map<String, Object> params = Map.of(
                "password", password,
                "updateTime", LocalDateTime.now(),
                "emailEncrypted", emailEncrypted
        );

        return jdbcManager.update(sql, params, true);
    }
}

