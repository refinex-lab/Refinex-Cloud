package cn.refinex.platform.repository.sys;

import cn.hutool.core.util.StrUtil;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.controller.user.dto.request.UserQueryRequestDTO;
import cn.refinex.platform.controller.user.dto.response.UserListResponseDTO;
import cn.refinex.platform.entity.sys.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * 根据用户 ID 查询用户名
     *
     * @param userId 用户 ID
     * @return 用户名
     */
    public String selectUsernameById(Long userId) {
        String sql = """
                SELECT username
                FROM sys_user
                WHERE id = :userId AND deleted = 0
                """;

        Map<String, Object> params = Map.of("userId", userId);

        try {
            return jdbcManager.queryString(sql, params, false);
        } catch (Exception e) {
            log.error("根据用户 ID 查询用户名失败，userId: {}", userId, e);
            return null;
        }
    }

    /**
     * 根据用户 ID 列表查询用户名映射
     *
     * @param userIds 用户 ID 列表
     * @return 用户名映射，键为用户 ID，值为用户名
     */
    public Map<String, Object> selectUsernameMap(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }

        String sql = """
                SELECT id, username
                FROM sys_user
                WHERE id IN (:userIds) AND deleted = 0
                """;

        Map<String, Object> params = Map.of("userIds", userIds);

        try {
            return jdbcManager.queryMap(sql, params);
        } catch (Exception e) {
            log.error("根据用户 ID 列表查询用户名映射失败，userIds: {}", userIds, e);
            return Map.of();
        }
    }

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

    /**
     * 分页查询用户列表
     *
     * @param request 查询条件
     * @return 分页结果
     */
    public PageResult<UserListResponseDTO> listUsers(UserQueryRequestDTO request) {
        StringBuilder sqlBuilder = new StringBuilder("""
                SELECT
                    id,
                    username,
                    mobile,
                    email,
                    nickname,
                    sex,
                    avatar,
                    user_status,
                    user_type,
                    register_source,
                    last_login_time,
                    last_login_ip,
                    create_time,
                    status,
                    remark
                FROM sys_user
                WHERE deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();
        List<String> conditions = new ArrayList<>();

        // 动态添加查询条件
        if (StrUtil.isNotBlank(request.getUsername())) {
            conditions.add("username LIKE :username");
            params.put("username", "%" + request.getUsername() + "%");
        }

        if (StrUtil.isNotBlank(request.getMobile())) {
            conditions.add("mobile LIKE :mobile");
            params.put("mobile", "%" + request.getMobile() + "%");
        }

        if (StrUtil.isNotBlank(request.getEmail())) {
            conditions.add("email LIKE :email");
            params.put("email", "%" + request.getEmail() + "%");
        }

        if (StrUtil.isNotBlank(request.getNickname())) {
            conditions.add("nickname LIKE :nickname");
            params.put("nickname", "%" + request.getNickname() + "%");
        }

        if (request.getUserStatus() != null) {
            conditions.add("user_status = :userStatus");
            params.put("userStatus", request.getUserStatus());
        }

        if (StrUtil.isNotBlank(request.getUserType())) {
            conditions.add("user_type = :userType");
            params.put("userType", request.getUserType());
        }

        if (StrUtil.isNotBlank(request.getRegisterSource())) {
            conditions.add("register_source = :registerSource");
            params.put("registerSource", request.getRegisterSource());
        }

        if (request.getStatus() != null) {
            conditions.add("status = :status");
            params.put("status", request.getStatus());
        }

        if (!conditions.isEmpty()) {
            sqlBuilder.append(" AND ").append(String.join(" AND ", conditions));
        }

        // 排序
        sqlBuilder.append(" ORDER BY create_time DESC");

        // 分页
        int pageNum = request.getPageNum() != null && request.getPageNum() > 0 ? request.getPageNum() : 1;
        int pageSize = request.getPageSize() != null && request.getPageSize() > 0 ? request.getPageSize() : 10;
        PageRequest pageRequest = new PageRequest(pageNum, pageSize);

        return jdbcManager.queryPage(sqlBuilder.toString(), params, pageRequest, true, UserListResponseDTO.class);
    }

    /**
     * 更新用户信息
     *
     * @param sysUser 用户实体
     * @return 影响行数
     */
    public int update(SysUser sysUser) {
        String sql = """
                UPDATE sys_user
                SET username = :username,
                    mobile = :mobile,
                    email = :email,
                    password = :password,
                    nickname = :nickname,
                    sex = :sex,
                    avatar = :avatar,
                    user_status = :userStatus,
                    user_type = :userType,
                    register_source = :registerSource,
                    last_login_time = :lastLoginTime,
                    last_login_ip = :lastLoginIp,
                    update_by = :updateBy,
                    update_time = :updateTime,
                    remark = :remark,
                    sort = :sort,
                    status = :status,
                    extra_data = :extraData
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", sysUser.getId());
        params.put("username", sysUser.getUsername());
        params.put("mobile", sysUser.getMobile());
        params.put("email", sysUser.getEmail());
        params.put("password", sysUser.getPassword());
        params.put("nickname", sysUser.getNickname());
        params.put("sex", sysUser.getSex());
        params.put("avatar", sysUser.getAvatar());
        params.put("userStatus", sysUser.getUserStatus());
        params.put("userType", sysUser.getUserType());
        params.put("registerSource", sysUser.getRegisterSource());
        params.put("lastLoginTime", sysUser.getLastLoginTime());
        params.put("lastLoginIp", sysUser.getLastLoginIp());
        params.put("updateBy", sysUser.getUpdateBy());
        params.put("updateTime", LocalDateTime.now());
        params.put("remark", sysUser.getRemark());
        params.put("sort", sysUser.getSort());
        params.put("status", sysUser.getStatus());
        params.put("extraData", sysUser.getExtraData());

        return jdbcManager.update(sql, params);
    }

    /**
     * 逻辑删除用户
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    public int delete(Long userId) {
        String sql = """
                UPDATE sys_user
                SET deleted = 1,
                    update_time = :updateTime
                WHERE id = :userId
                """;

        Map<String, Object> params = Map.of(
                "userId", userId,
                "updateTime", LocalDateTime.now()
        );

        return jdbcManager.update(sql, params);
    }
}

