package cn.refinex.common.security.core;

import cn.dev33.satoken.stp.StpInterface;
import cn.refinex.common.security.repository.PermissionRepository;
import cn.refinex.common.security.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 权限接口实现
 * <p>
 * 实现 StpInterface 接口，为 Sa-Token 提供权限和角色数据。
 * Sa-Token 在进行权限校验时会调用此类的方法获取用户的权限和角色信息。
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
public class StpInterfaceImpl implements StpInterface {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public StpInterfaceImpl(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        log.info("初始化 Sa-Token 权限接口实现");
    }

    /**
     * 返回指定账号 id 所拥有的权限码集合
     * <p>
     * 说明：
     * 1. Sa-Token 在调用 StpUtil.checkPermission("user:add") 时会调用此方法
     * 2. loginId 即为用户登录时传入的用户 ID
     * 3. loginType 为账号类型，默认为 "login"，可用于区分不同的登录体系（如用户端、管理端）
     * 4. 返回的权限码列表会被 Sa-Token 缓存，直到 Session 过期或手动清除
     * </p>
     *
     * @param loginId   账号 id（即用户 ID）
     * @param loginType 账号类型（预留，暂不使用）
     * @return 权限码集合（如 ["user:add", "content:delete"]）
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if (loginId == null) {
            log.warn("查询用户权限时，loginId 为空");
            return Collections.emptyList();
        }

        try {
            Long userId = Long.valueOf(loginId.toString());
            List<String> permissions = permissionRepository.selectPermissionCodesByUserId(userId);
            log.debug("查询用户权限成功，userId: {}, loginType: {}, permissions: {}", userId, loginType, permissions);
            return permissions;
        } catch (Exception e) {
            log.error("查询用户权限失败，loginId: {}, loginType: {}", loginId, loginType, e);
            return Collections.emptyList();
        }
    }

    /**
     * 返回指定账号 id 所拥有的角色标识集合
     * <p>
     * 说明：
     * 1. Sa-Token 在调用 StpUtil.checkRole("ROLE_ADMIN") 时会调用此方法
     * 2. loginId 即为用户登录时传入的用户 ID
     * 3. loginType 为账号类型，默认为 "login"
     * 4. 返回的角色列表会被 Sa-Token 缓存，直到 Session 过期或手动清除
     * 5. 使用 selectValidRoleCodesByUserId 方法，会考虑角色的时间有效期
     * </p>
     *
     * @param loginId   账号 id（即用户 ID）
     * @param loginType 账号类型（预留，暂不使用）
     * @return 角色标识集合（如 ["ROLE_USER", "ROLE_ADMIN"]）
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if (loginId == null) {
            log.warn("查询用户角色时，loginId 为空");
            return Collections.emptyList();
        }

        try {
            Long userId = Long.valueOf(loginId.toString());
            List<String> roles = roleRepository.selectValidRoleCodesByUserId(userId);
            log.debug("查询用户角色成功，userId: {}, loginType: {}, roles: {}", userId, loginType, roles);
            return roles;
        } catch (Exception e) {
            log.error("查询用户角色失败，loginId: {}, loginType: {}", loginId, loginType, e);
            return Collections.emptyList();
        }
    }
}

