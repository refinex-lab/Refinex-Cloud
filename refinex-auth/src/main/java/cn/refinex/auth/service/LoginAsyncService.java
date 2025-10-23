package cn.refinex.auth.service;

import java.time.LocalDateTime;

/**
 * 登录异步服务
 * <p>
 * 提供登录相关的异步操作，不阻塞登录响应
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface LoginAsyncService {

    /**
     * 异步记录登录日志
     * <p>
     * 说明：
     * 1. 解析 User-Agent 获取浏览器和操作系统信息
     * 2. 记录登录成功或失败日志
     * 3. 异常不会传播到调用方
     * </p>
     *
     * @param userId      用户 ID（失败时可能为 null）
     * @param username    用户名（失败时可能为 null）
     * @param loginType   登录类型
     * @param loginIp     登录 IP
     * @param userAgent   User-Agent 字符串
     * @param deviceType  设备类型（PC、APP、H5）
     * @param loginStatus 登录状态（0 成功, 1 失败）
     * @param failReason  失败原因（成功时为 null）
     */
    void recordLoginLog(Long userId, String username, Integer loginType, String loginIp, String userAgent, String deviceType, Integer loginStatus, String failReason);

    /**
     * 异步更新用户最后登录信息
     * <p>
     * 说明：
     * 1. 更新 sys_user 表的 last_login_time 和 last_login_ip
     * 2. 异常不会传播到调用方
     * </p>
     *
     * @param userId    用户 ID
     * @param loginTime 登录时间
     * @param loginIp   登录 IP
     */
    void updateLastLoginInfo(Long userId, LocalDateTime loginTime, String loginIp);
}

