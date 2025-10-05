package cn.refinex.platform.service.user;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.refinex.common.security.util.SecurityUtils;
import cn.refinex.common.utils.Fn;
import cn.refinex.platform.controller.user.dto.UserSessionDTO;
import cn.refinex.platform.controller.user.dto.request.KickoutRequest;
import cn.refinex.platform.service.notification.UserNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户会话管理服务
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionService {

    private final UserNotificationService notificationService;

    /**
     * 查询用户登录设备列表
     *
     * @param userId 用户 ID
     * @return 会话列表
     */
    public List<UserSessionDTO> listUserSessions(Long userId) {
        log.info("查询用户登录设备列表，userId: {}", userId);

        List<UserSessionDTO> sessions = new ArrayList<>();

        // 获取用户的所有 Token
        List<String> tokenValueList = StpUtil.getTokenValueListByLoginId(userId);

        for (String tokenValue : tokenValueList) {
            try {
                // 获取 Token 的 Session
                SaSession session = StpUtil.getSessionBySessionId("token:" + tokenValue);
                if (session == null) {
                    continue;
                }

                // 获取设备类型
                String deviceType = Fn.getString(session.get("deviceType"), null);
                if (StrUtil.isBlank(deviceType)) {
                    deviceType = "UNKNOWN";
                }

                // 获取 Token 剩余有效期
                long tokenTimeout = StpUtil.getTokenTimeout(tokenValue);

                // 构建会话信息
                // 注意：SaSession 没有 getUpdateTime() 方法，最后活跃时间使用创建时间代替
                UserSessionDTO sessionDTO = UserSessionDTO.builder()
                        .tokenValue(tokenValue)
                        .deviceType(deviceType)
                        .loginTime(session.getCreateTime())
                        .tokenTimeout(tokenTimeout)
                        // 使用创建时间代替
                        .lastActivityTime(session.getCreateTime())
                        .build();

                sessions.add(sessionDTO);
            } catch (Exception e) {
                log.warn("获取 Token 会话信息失败，tokenValue: {}", tokenValue, e);
            }
        }

        log.info("查询用户登录设备列表成功，userId: {}, sessionCount: {}", userId, sessions.size());
        return sessions;
    }

    /**
     * 踢人下线
     *
     * @param request 踢人下线请求
     */
    public void kickout(KickoutRequest request) {
        log.info("踢人下线，request: {}", request);

        // 优先级：tokenValue > deviceType > userId
        if (StrUtil.isNotBlank(request.getTokenValue())) {
            // 按 Token 踢下线
            SecurityUtils.kickoutByTokenValue(request.getTokenValue());
            log.info("按 Token 踢人下线成功，tokenValue: {}", request.getTokenValue());
        } else if (StrUtil.isNotBlank(request.getDeviceType())) {
            // 按设备类型踢下线
            SecurityUtils.kickout(request.getUserId(), request.getDeviceType());
            log.info("按设备类型踢人下线成功，userId: {}, deviceType: {}", request.getUserId(), request.getDeviceType());
        } else {
            // 踢出所有设备
            SecurityUtils.kickout(request.getUserId());
            log.info("踢出所有设备成功，userId: {}", request.getUserId());
        }

        // 发送踢人下线通知邮件
        notificationService.sendKickoutNotification(
                request.getUserId(),
                request.getDeviceType(),
                request.getReason()
        );
    }

    /**
     * 踢出用户所有设备
     *
     * @param userId 用户 ID
     */
    public void kickoutAll(Long userId) {
        log.info("踢出用户所有设备，userId: {}", userId);
        SecurityUtils.kickout(userId);
        log.info("踢出用户所有设备成功，userId: {}", userId);

        // 发送踢人下线通知邮件
        notificationService.sendKickoutNotification(userId, null, "管理员操作");
    }
}

