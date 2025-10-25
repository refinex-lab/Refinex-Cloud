package cn.refinex.common.satoken.core.util;

import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.refinex.common.constants.SystemRoleConstants;
import cn.refinex.common.domain.model.LoginUser;
import cn.refinex.common.enums.UserType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 登录助手类
 * <p>
 * 相关说明:
 * 1. user_type: 标识用户类型，同一个用户可以有多种用户类型，简单理解就是登录设备类型，例如 PC、APP 等
 * 2. device_type: 标识登录设备类型，例如 WEB、IOS、ANDROID 等
 * <p>
 * - 可以组成 用户类型与设备类型多对多 的权限灵活控制
 * - 多用户体系(暂不考虑) 针对多种用户类型但权限控制不一致 可以组成 多用户类型表与多设备类型 分别控制权限
 *
 * @author Lion Li
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginHelper {

    public static final String LOGIN_TYPE = "loginType";
    public static final String LOGIN_USER_KEY = "loginUser";
    public static final String USER_KEY = "userId";
    public static final String USER_NAME_KEY = "userName";

    /**
     * 客户端标识符，用于识别不同的客户端应用程序或设备
     * <p>
     * 设计目的:
     * 1. 区分不同的前端应用(如 Web 管理后台、移动端 APP、第三方应用等)
     * 2. 防止 Token 在非预期客户端中被滥用
     * <p>
     * 安全防护:
     * 1. 可以参考 SaTokenAuthFilter 中的实现
     * 2. 会检查Header 和 Param 里的 clientid 与 Token 里的是否一致，如果三者任意一个 clientid 不匹配则 Token 无效
     * <p>
     * 传递方式:
     * - HTTP Header: clientid: xxx
     * - URL 参数: ?clientid=xxx
     * - Token 中存储: 登录时存储在 Sa-Token 的 extra 数据中
     * <p>
     * 支持的值:
     * - web_admin: Web 管理后台
     * - mobile_app: 移动端 APP
     */
    public static final String CLIENT_KEY = "clientid";

    /**
     * 基于设备类型(SaLoginParameter 参数)登录系统，针对相同用户体系不同设备类型
     *
     * @param loginUser      登录用户对象
     * @param loginParameter 登录参数
     */
    public static void login(LoginUser loginUser, Integer loginType, SaLoginParameter loginParameter) {
        // 如果没有传入自定义 Sa-Token 登录参数就使用默认参数
        loginParameter = ObjectUtil.defaultIfNull(loginParameter, new SaLoginParameter());

        // 登录系统
        StpUtil.login(
                loginUser.getLoginId(),
                loginParameter
                        // 额外参数：用户ID、用户名 (按需补充即可)
                        .setExtra(USER_KEY, loginUser.getUserId())
                        .setExtra(USER_NAME_KEY, loginUser.getUsername())
                        .setExtra(LOGIN_TYPE, loginType)
        );

        // 将登录用户对象写入 Sa-Token 会话
        StpUtil.getTokenSession().set(LOGIN_USER_KEY, loginUser);
    }

    /**
     * 获取登录用户对象(多级缓存)
     *
     * @param <T> 登录用户对象类型
     * @return 登录用户对象
     */
    @SuppressWarnings("unchecked cast")
    public static <T extends LoginUser> T getLoginUser() {
        SaSession session = StpUtil.getTokenSession();
        if (Objects.isNull(session)) {
            return null;
        }
        return (T) session.get(LOGIN_USER_KEY);
    }

    /**
     * 根据 token 获取登录用户对象(多级缓存)
     *
     * @param token 登录 token
     * @param <T>   登录用户对象类型
     * @return 登录用户对象
     */
    @SuppressWarnings("unchecked cast")
    public static <T extends LoginUser> T getLoginUser(String token) {
        SaSession session = StpUtil.getTokenSessionByToken(token);
        if (Objects.isNull(session)) {
            return null;
        }
        return (T) session.get(LOGIN_USER_KEY);
    }

    /**
     * 获取登录用户ID
     *
     * @return 登录用户ID
     */
    public static Long getUserId() {
        return Convert.toLong(getExtra(USER_KEY));
    }

    /**
     * 获取登录用户ID字符串
     *
     * @return 登录用户ID字符串
     */
    public static String getUserIdStr() {
        return Convert.toStr(getExtra(USER_KEY));
    }

    /**
     * 获取用户账户
     *
     * @return 用户账户
     */
    public static String getUsername() {
        return Convert.toStr(getExtra(USER_NAME_KEY));
    }

    /**
     * 获取用户类型
     *
     * @return 用户类型
     */
    public static UserType getUserType() {
        String loginType = StpUtil.getLoginIdAsString();
        return UserType.fromCode(loginType);
    }

    /**
     * 获取当前 Token 值
     *
     * @return 当前 Token 值
     */
    public static String getTokenValue() {
        return StpUtil.getTokenValue();
    }

    /**
     * 获取当前 Token 名称
     *
     * @return 当前 Token 名称
     */
    public static String getTokenName() {
        return StpUtil.getTokenName();
    }

    /**
     * 获取当前 Same-Token, 如果不存在, 则立即创建并返回
     *
     * @return 相同 Token 值
     */
    public static String getSameTokenValue() {
        return SaSameUtil.getToken();
    }

    /**
     * 判断用户是否为超级管理员
     *
     * @param userId 用户ID
     * @return 是否为超级管理员
     */
    public static boolean isSuperAdmin(Long userId) {
        return SystemRoleConstants.SUPER_ADMIN_ID.equals(userId);
    }

    /**
     * 判断当前登录用户是否为超级管理员
     *
     * @return 是否为超级管理员
     */
    public static boolean isSuperAdmin() {
        return isSuperAdmin(getUserId());
    }

    /**
     * 获取登录用户对象额外参数
     *
     * @param key 参数键, 即当时通过 SaLoginParameter.setExtra 设置的键
     * @return 参数值
     */
    private static Object getExtra(String key) {
        try {
            return StpUtil.getExtra(key);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断当前用户是否已登录
     *
     * @return 是否已登录
     */
    public static boolean isLogin() {
        try {
            StpUtil.checkLogin();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
