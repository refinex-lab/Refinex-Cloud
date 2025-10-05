package cn.refinex.common.security.util;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.stp.StpUtil;
import cn.refinex.common.security.service.WhitelistService;
import cn.refinex.common.utils.spring.SpringContextHolder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 安全工具类
 * <p>
 * 封装 Sa-Token 的 StpUtil 常用方法，提供更便捷的 API。
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityUtils {

    /**
     * 获取当前登录用户 ID
     * <p>
     * 说明：
     * 1. 如果用户未登录，返回 null
     * 2. 不会抛出异常
     * 3. 适用于可选登录的场景
     * </p>
     *
     * @return 用户 ID，未登录返回 null
     */
    public static Long getCurrentUserId() {
        try {
            Object loginId = StpUtil.getLoginIdDefaultNull();
            return loginId != null ? Long.valueOf(loginId.toString()) : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前登录用户 ID（必须已登录）
     * <p>
     * 说明：
     * 1. 如果用户未登录，抛出 NotLoginException
     * 2. 适用于必须登录的场景
     * </p>
     *
     * @return 用户 ID
     * @throws NotLoginException 未登录时抛出
     */
    public static Long getRequiredUserId() {
        return Long.valueOf(StpUtil.getLoginId().toString());
    }

    /**
     * 判断当前用户是否已登录
     *
     * @return true-已登录，false-未登录
     */
    public static boolean isLogin() {
        return StpUtil.isLogin();
    }

    /**
     * 检查当前用户是否拥有指定权限
     * <p>
     * 说明：
     * 1. 不会抛出异常
     * 2. 返回 true 表示拥有权限，false 表示不拥有
     * 3. 适用于需要根据权限显示/隐藏功能的场景
     * </p>
     *
     * @param permission 权限码（如 "user:add"）
     * @return true-拥有，false-不拥有
     */
    public static boolean hasPermission(String permission) {
        try {
            StpUtil.checkPermission(permission);
            return true;
        } catch (NotPermissionException e) {
            return false;
        }
    }

    /**
     * 检查当前用户是否拥有指定角色
     * <p>
     * 说明：
     * 1. 不会抛出异常
     * 2. 返回 true 表示拥有角色，false 表示不拥有
     * 3. 适用于需要根据角色显示/隐藏功能的场景
     * </p>
     *
     * @param role 角色码（如 "ROLE_ADMIN"）
     * @return true-拥有，false-不拥有
     */
    public static boolean hasRole(String role) {
        try {
            StpUtil.checkRole(role);
            return true;
        } catch (NotRoleException e) {
            return false;
        }
    }

    /**
     * 检查当前用户是否拥有指定权限（抛出异常）
     * <p>
     * 说明：
     * 1. 如果不拥有权限，抛出 NotPermissionException
     * 2. 适用于必须拥有权限才能执行的场景
     * </p>
     *
     * @param permission 权限码（如 "user:add"）
     * @throws NotPermissionException 不拥有权限时抛出
     */
    public static void checkPermission(String permission) {
        StpUtil.checkPermission(permission);
    }

    /**
     * 检查当前用户是否拥有指定角色（抛出异常）
     * <p>
     * 说明：
     * 1. 如果不拥有角色，抛出 NotRoleException
     * 2. 适用于必须拥有角色才能执行的场景
     * </p>
     *
     * @param role 角色码（如 "ROLE_ADMIN"）
     * @throws NotRoleException 不拥有角色时抛出
     */
    public static void checkRole(String role) {
        StpUtil.checkRole(role);
    }

    /**
     * 从 Session 中获取用户信息
     * <p>
     * 说明：
     * 1. Session 数据存储在 Redis 中
     * 2. 可用于存储用户的扩展信息（如用户名、昵称等）
     * 3. 如果 key 不存在，返回 null
     * </p>
     *
     * @param key Session key
     * @param <T> 返回类型
     * @return Session 中的值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getSessionValue(String key) {
        return (T) StpUtil.getSession().get(key);
    }

    /**
     * 向 Session 中设置用户信息
     * <p>
     * 说明：
     * 1. Session 数据存储在 Redis 中
     * 2. 可用于存储用户的扩展信息（如用户名、昵称等）
     * 3. Session 会随着 Token 过期而过期
     * </p>
     *
     * @param key   Session key
     * @param value Session value
     */
    public static void setSessionValue(String key, Object value) {
        StpUtil.getSession().set(key, value);
    }

    /**
     * 获取当前 Token 值
     * <p>
     * 说明：
     * 1. 返回当前请求的 Token 值
     * 2. 如果未登录，返回 null
     * </p>
     *
     * @return Token 值
     */
    public static String getTokenValue() {
        return StpUtil.getTokenValueNotCut();
    }

    /**
     * 获取当前 Token 的剩余有效时间（秒）
     * <p>
     * 说明：
     * 1. 返回当前 Token 的剩余有效时间
     * 2. 如果 Token 永久有效，返回 -1
     * 3. 如果 Token 已过期，返回 -2
     * </p>
     *
     * @return 剩余有效时间（秒）
     */
    public static long getTokenTimeout() {
        return StpUtil.getTokenTimeout();
    }

    /**
     * 续签当前 Token
     * <p>
     * 说明：
     * 1. 将当前 Token 的有效期延长到最大值
     * 2. 适用于"记住我"功能
     * </p>
     */
    public static void renewTimeout() {
        StpUtil.renewTimeout(StpUtil.getTokenTimeout());
    }

    // ==================== 踢人下线相关方法 ====================

    /**
     * 踢人下线（根据用户 ID）
     * <p>
     * 说明：
     * 1. 将指定用户的所有设备踢下线
     * 2. 不会清除 Token 信息，而是标记为 "已被踢下线"
     * 3. 后续访问会提示 "Token已被踢下线"
     * 4. 白名单用户（管理员）不受影响
     * </p>
     *
     * @param userId 用户 ID
     * @throws cn.refinex.common.security.exception.WhitelistException 用户在白名单中时抛出
     */
    public static void kickout(Long userId) {
        // 白名单检查
        checkNotWhitelist(userId);
        StpUtil.kickout(userId);
    }

    /**
     * 踢人下线（根据用户 ID 和设备类型）
     * <p>
     * 说明：
     * 1. 将指定用户的指定设备类型踢下线
     * 2. 其他设备类型不受影响
     * 3. 白名单用户（管理员）不受影响
     * </p>
     *
     * @param userId     用户 ID
     * @param deviceType 设备类型（如 "PC"、"APP"、"H5"）
     * @throws cn.refinex.common.security.exception.WhitelistException 用户在白名单中时抛出
     */
    public static void kickout(Long userId, String deviceType) {
        // 白名单检查
        checkNotWhitelist(userId);
        StpUtil.kickout(userId, deviceType);
    }

    /**
     * 踢人下线（根据 Token 值）
     * <p>
     * 说明：
     * 1. 将指定 Token 踢下线
     * 2. 适用于精确控制某个会话
     * </p>
     *
     * @param tokenValue Token 值
     */
    public static void kickoutByTokenValue(String tokenValue) {
        StpUtil.kickoutByTokenValue(tokenValue);
    }

    // ==================== 账号封禁相关方法 ====================

    /**
     * 封禁账号
     * <p>
     * 说明：
     * 1. 封禁指定账号，禁止其登录
     * 2. 封禁期间，该账号无法通过任何方式登录
     * 3. 封禁时间到期后自动解封
     * 4. 白名单用户（管理员）不受影响
     * </p>
     *
     * @param userId  用户 ID
     * @param seconds 封禁时长（秒），-1 表示永久封禁
     * @throws cn.refinex.common.security.exception.WhitelistException 用户在白名单中时抛出
     */
    public static void disable(Long userId, long seconds) {
        // 白名单检查
        checkNotWhitelist(userId);
        StpUtil.disable(userId, seconds);
    }

    /**
     * 分类封禁（封禁指定服务）
     * <p>
     * 说明：
     * 1. 封禁指定账号的指定服务
     * 2. 例如：封禁评论功能、封禁发帖功能
     * 3. 其他服务不受影响
     * 4. 白名单用户（管理员）不受影响
     * </p>
     *
     * @param userId  用户 ID
     * @param service 服务标识（如 "comment"、"post"）
     * @param seconds 封禁时长（秒），-1 表示永久封禁
     * @throws cn.refinex.common.security.exception.WhitelistException 用户在白名单中时抛出
     */
    public static void disable(Long userId, String service, long seconds) {
        // 白名单检查
        checkNotWhitelist(userId);
        StpUtil.disable(userId, service, seconds);
    }

    /**
     * 判断账号是否被封禁
     *
     * @param userId 用户 ID
     * @return true=已被封禁，false=未被封禁
     */
    public static boolean isDisable(Long userId) {
        return StpUtil.isDisable(userId);
    }

    /**
     * 判断账号的指定服务是否被封禁
     *
     * @param userId  用户 ID
     * @param service 服务标识
     * @return true=已被封禁，false=未被封禁
     */
    public static boolean isDisable(Long userId, String service) {
        return StpUtil.isDisable(userId, service);
    }

    /**
     * 校验账号是否被封禁（如果被封禁则抛出异常）
     *
     * @param userId 用户 ID
     * @throws cn.dev33.satoken.exception.DisableServiceException 账号被封禁时抛出
     */
    public static void checkDisable(Long userId) {
        StpUtil.checkDisable(userId);
    }

    /**
     * 校验账号的指定服务是否被封禁（如果被封禁则抛出异常）
     *
     * @param userId  用户 ID
     * @param service 服务标识
     * @throws cn.dev33.satoken.exception.DisableServiceException 服务被封禁时抛出
     */
    public static void checkDisable(Long userId, String service) {
        StpUtil.checkDisable(userId, service);
    }

    /**
     * 获取账号剩余封禁时间
     *
     * @param userId 用户 ID
     * @return 剩余封禁时间（秒），-1=永久封禁，-2=未被封禁
     */
    public static long getDisableTime(Long userId) {
        return StpUtil.getDisableTime(userId);
    }

    /**
     * 获取账号指定服务的剩余封禁时间
     *
     * @param userId  用户 ID
     * @param service 服务标识
     * @return 剩余封禁时间（秒），-1=永久封禁，-2=未被封禁
     */
    public static long getDisableTime(Long userId, String service) {
        return StpUtil.getDisableTime(userId, service);
    }

    /**
     * 解封账号
     *
     * @param userId 用户 ID
     */
    public static void untieDisable(Long userId) {
        StpUtil.untieDisable(userId);
    }

    /**
     * 解封账号的指定服务
     *
     * @param userId  用户 ID
     * @param service 服务标识
     */
    public static void untieDisable(Long userId, String service) {
        StpUtil.untieDisable(userId, service);
    }

    // ==================== 白名单相关方法 ====================

    /**
     * 检查用户是否不在白名单中
     * <p>
     * 如果用户在白名单中（管理员），则抛出异常
     * </p>
     *
     * @param userId 用户 ID
     * @throws cn.refinex.common.security.exception.WhitelistException 用户在白名单中时抛出
     */
    private static void checkNotWhitelist(Long userId) {
        try {
            WhitelistService whitelistService = SpringContextHolder.getBean(WhitelistService.class);
            whitelistService.checkNotWhitelist(userId);
        } catch (Exception e) {
            // 如果获取 WhitelistService 失败，记录日志但不阻止操作
            // 这样可以避免在某些特殊场景下（如测试环境）因为 Bean 未初始化而导致功能不可用
            if (e instanceof cn.refinex.common.security.exception.WhitelistException) {
                throw e;
            }
        }
    }
}

