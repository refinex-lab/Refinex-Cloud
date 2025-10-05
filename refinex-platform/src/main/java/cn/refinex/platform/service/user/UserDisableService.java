package cn.refinex.platform.service.user;

import cn.hutool.core.util.StrUtil;
import cn.refinex.common.security.util.SecurityUtils;
import cn.refinex.platform.controller.user.dto.request.DisableRequest;
import cn.refinex.platform.controller.user.dto.response.DisableStatusResponse;
import cn.refinex.platform.service.notification.UserNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 账号封禁管理服务
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDisableService {

    private final UserNotificationService notificationService;

    /**
     * 封禁账号
     *
     * @param request 封禁请求
     */
    public void disable(DisableRequest request) {
        log.info("封禁账号，request: {}", request);

        // 1. 如果需要踢人下线，先踢下线
        if (Boolean.TRUE.equals(request.getKickout())) {
            SecurityUtils.kickout(request.getUserId());
            log.info("踢人下线成功，userId: {}", request.getUserId());
        }

        // 2. 执行封禁
        if (StrUtil.isNotBlank(request.getService())) {
            // 分类封禁
            SecurityUtils.disable(request.getUserId(), request.getService(), request.getSeconds());
            log.info("分类封禁成功，userId: {}, service: {}, seconds: {}, reason: {}",
                    request.getUserId(), request.getService(), request.getSeconds(), request.getReason());
        } else {
            // 全局封禁
            SecurityUtils.disable(request.getUserId(), request.getSeconds());
            log.info("全局封禁成功，userId: {}, seconds: {}, reason: {}",
                    request.getUserId(), request.getSeconds(), request.getReason());
        }

        // 3. 发送封禁通知邮件
        notificationService.sendDisableNotification(
                request.getUserId(),
                request.getService(),
                request.getSeconds(),
                request.getReason()
        );
    }

    /**
     * 解封账号
     *
     * @param userId  用户 ID
     * @param service 服务类型（可选）
     */
    public void untie(Long userId, String service) {
        log.info("解封账号，userId: {}, service: {}", userId, service);

        if (StrUtil.isNotBlank(service)) {
            // 解封指定服务
            SecurityUtils.untieDisable(userId, service);
            log.info("解封指定服务成功，userId: {}, service: {}", userId, service);
        } else {
            // 解封全局
            SecurityUtils.untieDisable(userId);
            log.info("解封全局成功，userId: {}", userId);
        }

        // 发送解封通知邮件
        notificationService.sendUntieNotification(userId, service);
    }

    /**
     * 查询封禁状态
     *
     * @param userId  用户 ID
     * @param service 服务类型（可选）
     * @return 封禁状态
     */
    public DisableStatusResponse getStatus(Long userId, String service) {
        log.info("查询封禁状态，userId: {}, service: {}", userId, service);

        boolean disabled;
        long remainingTime;
        String disableType;

        if (StrUtil.isNotBlank(service)) {
            // 查询指定服务封禁状态
            disabled = SecurityUtils.isDisable(userId, service);
            remainingTime = SecurityUtils.getDisableTime(userId, service);
            disableType = service;
        } else {
            // 查询全局封禁状态
            disabled = SecurityUtils.isDisable(userId);
            remainingTime = SecurityUtils.getDisableTime(userId);
            disableType = "全局";
        }

        return DisableStatusResponse.builder()
                .userId(userId)
                .disabled(disabled)
                .remainingTime(remainingTime)
                .disableType(disableType)
                .build();
    }
}

