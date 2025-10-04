package cn.refinex.common.security.util;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.stp.StpUtil;
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
}

