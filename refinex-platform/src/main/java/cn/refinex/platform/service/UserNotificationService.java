package cn.refinex.platform.service;

/**
 * 用户通知服务
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface UserNotificationService {

    /**
     * 发送踢人下线通知
     *
     * @param userId     被踢下线的用户 ID
     * @param deviceType 设备类型
     * @param reason     下线原因
     */
    void sendKickoutNotification(Long userId, String deviceType, String reason);

    /**
     * 发送账号封禁通知
     *
     * @param userId  被封禁的用户 ID
     * @param service 封禁服务（null 表示全局封禁）
     * @param seconds 封禁时长（秒）
     * @param reason  封禁原因
     */
    void sendDisableNotification(Long userId, String service, long seconds, String reason);

    /**
     * 发送账号解封通知
     *
     * @param userId  被解封的用户 ID
     * @param service 解封服务（null 表示全局解封）
     */
    void sendUntieNotification(Long userId, String service);
}
