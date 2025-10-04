package cn.refinex.platform.util;

import cn.dev33.satoken.exception.NotLoginException;
import cn.refinex.common.security.util.SecurityUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 用户上下文持有者
 * <p>
 * 提供获取当前登录用户信息的便捷方法
 * </p>
 *
 * @author Refinex
 * @since 2025-10-04
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserContextHolder {

    /**
     * 获取当前登录用户 ID
     *
     * @return 用户 ID，未登录返回 null
     */
    public static Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    /**
     * 获取当前登录用户 ID（必须已登录）
     *
     * @return 用户 ID
     * @throws NotLoginException 未登录时抛出
     */
    public static Long getRequiredUserId() {
        return SecurityUtils.getRequiredUserId();
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名，未登录返回 null
     */
    public static String getCurrentUsername() {
        return SecurityUtils.getSessionValue("username");
    }

    /**
     * 获取当前登录用户昵称
     *
     * @return 昵称，未登录返回 null
     */
    public static String getCurrentNickname() {
        return SecurityUtils.getSessionValue("nickname");
    }

    /**
     * 获取当前登录用户头像
     *
     * @return 头像 URL，未登录返回 null
     */
    public static String getCurrentAvatar() {
        return SecurityUtils.getSessionValue("avatar");
    }

    /**
     * 判断当前用户是否已登录
     *
     * @return true-已登录，false-未登录
     */
    public static boolean isLogin() {
        return SecurityUtils.isLogin();
    }

    /**
     * 检查当前用户是否拥有指定权限
     *
     * @param permission 权限码
     * @return true-拥有，false-不拥有
     */
    public static boolean hasPermission(String permission) {
        return SecurityUtils.hasPermission(permission);
    }

    /**
     * 检查当前用户是否拥有指定角色
     *
     * @param role 角色码
     * @return true-拥有，false-不拥有
     */
    public static boolean hasRole(String role) {
        return SecurityUtils.hasRole(role);
    }
}

