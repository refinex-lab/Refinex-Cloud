package cn.refinex.auth.service;

import cn.refinex.auth.domain.dto.request.RecordLoginLogRequest;

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
     * @param request 记录登录日志请求 DTO
     */
    void recordLoginLog(RecordLoginLogRequest request);

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

