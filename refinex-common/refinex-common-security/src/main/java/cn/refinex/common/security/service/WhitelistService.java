package cn.refinex.common.security.service;

import cn.refinex.common.security.constants.WhitelistRoles;
import cn.refinex.common.security.repository.RoleRepository;
import cn.refinex.common.security.exception.WhitelistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 白名单服务
 * <p>
 * 提供白名单用户检查功能，防止对管理员执行封禁或踢人下线操作
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WhitelistService {

    private final RoleRepository roleRepository;

    /**
     * 判断用户是否在白名单中
     * <p>
     * 白名单用户包括：
     * 1. ROLE_SUPER_ADMIN（超级管理员）
     * 2. ROLE_ADMIN（管理员）
     * </p>
     *
     * @param userId 用户 ID
     * @return true=在白名单中，false=不在白名单中
     */
    public boolean isWhitelistUser(Long userId) {
        if (userId == null) {
            return false;
        }

        try {
            // 查询用户的所有角色
            List<String> userRoles = roleRepository.selectRoleCodesByUserId(userId);

            // 检查是否包含白名单角色
            boolean isWhitelist = userRoles.stream()
                    .anyMatch(role -> Arrays.asList(WhitelistRoles.ALL).contains(role));

            if (isWhitelist) {
                log.debug("用户 [{}] 为白名单用户，角色：{}", userId, userRoles);
            }

            return isWhitelist;
        } catch (Exception e) {
            log.error("检查用户白名单状态失败，userId: {}", userId, e);
            // 出错时默认不在白名单中
            return false;
        }
    }

    /**
     * 校验用户不在白名单中
     * <p>
     * 如果用户在白名单中，则抛出 WhitelistException 异常
     * </p>
     *
     * @param userId 用户 ID
     * @throws WhitelistException 用户在白名单中时抛出
     */
    public void checkNotWhitelist(Long userId) {
        if (isWhitelistUser(userId)) {
            log.warn("尝试对白名单用户执行操作，userId: {}", userId);
            throw WhitelistException.create(userId);
        }
    }

    /**
     * 批量检查用户是否在白名单中
     *
     * @param userIds 用户 ID 列表
     * @return 在白名单中的用户 ID 列表
     */
    public List<Long> filterWhitelistUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }

        return userIds.stream()
                .filter(this::isWhitelistUser)
                .toList();
    }
}

