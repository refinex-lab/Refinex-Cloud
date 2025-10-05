package cn.refinex.platform.service.user;

import cn.dev33.satoken.stp.StpUtil;
import cn.refinex.common.security.util.SecurityUtils;
import cn.refinex.platform.controller.user.dto.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
public class UserService {

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    public UserInfoDTO getCurrentUserInfo() {
        Long userId = SecurityUtils.getRequiredUserId();
        log.debug("获取用户信息，userId: {}", userId);

        // 从 Session 中获取用户基本信息
        String username = SecurityUtils.getSessionValue("username");
        String nickname = SecurityUtils.getSessionValue("nickname");
        String avatar = SecurityUtils.getSessionValue("avatar");

        // 从 Sa-Token 中获取用户权限和角色
        List<String> permissions = StpUtil.getPermissionList();
        List<String> roles = StpUtil.getRoleList();

        return UserInfoDTO.builder()
                .userId(userId)
                .username(username)
                .nickname(nickname)
                .avatar(avatar)
                .permissions(permissions)
                .roles(roles)
                .build();
    }
}
