package cn.refinex.common.security.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 白名单角色常量
 * <p>
 * 定义不受封禁和踢人下线影响的角色
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WhitelistRoles {

    /**
     * 超级管理员角色
     */
    public static final String SUPER_ADMIN = "ROLE_SUPER_ADMIN";

    /**
     * 管理员角色
     */
    public static final String ADMIN = "ROLE_ADMIN";

    /**
     * 所有白名单角色数组
     */
    public static final String[] ALL = {SUPER_ADMIN, ADMIN};
}

