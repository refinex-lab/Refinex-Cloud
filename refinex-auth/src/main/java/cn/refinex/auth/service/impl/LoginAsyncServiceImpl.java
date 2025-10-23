package cn.refinex.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.refinex.auth.domain.entity.LogLogin;
import cn.refinex.auth.repository.LoginLogRepository;
import cn.refinex.auth.repository.SysUserRepository;
import cn.refinex.auth.service.LoginAsyncService;
import cn.refinex.common.enums.LoginType;
import cn.refinex.common.utils.device.DeviceUtils;
import cn.refinex.common.utils.ip.Ip2RegionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 登录异步服务实现类
 * <p>
 * 提供登录相关的异步操作，不阻塞登录响应
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAsyncServiceImpl implements LoginAsyncService {

    private final LoginLogRepository loginLogRepository;
    private final SysUserRepository sysUserRepository;

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
    @Async
    public void recordLoginLog(Long userId, String username, Integer loginType, String loginIp, String userAgent, String deviceType, Integer loginStatus, String failReason) {
        try {
            // 1. 解析 User-Agent 获取浏览器和操作系统信息
            String browser = "Unknown";
            String os = "Unknown";
            if (StrUtil.isNotBlank(userAgent)) {
                browser = DeviceUtils.getBrowserName(userAgent);
                os = DeviceUtils.getOperatingSystemName(userAgent);
            }

            // 2. 构建登录日志对象
            LogLogin logLogin = LogLogin.builder()
                    .userId(userId)
                    .username(username)
                    // 当前仅支持密码登录
                    .loginType(LoginType.fromCode(loginType).getInfo())
                    .loginIp(loginIp)
                    // IP 地址解析
                    .loginLocation(Ip2RegionUtils.getRegionInfo(loginIp).getFullAddress())
                    .browser(browser)
                    .os(os)
                    .deviceType(deviceType)
                    .loginStatus(loginStatus)
                    .failReason(failReason)
                    .createTime(LocalDateTime.now())
                    .build();

            // 3. 插入登录日志
            loginLogRepository.insert(logLogin);
        } catch (Exception e) {
            log.error("异步记录登录日志失败，username: {}, loginStatus: {}", username, loginStatus, e);
        }
    }

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
    @Async
    public void updateLastLoginInfo(Long userId, LocalDateTime loginTime, String loginIp) {
        try {
            sysUserRepository.updateLastLoginInfo(userId, loginTime, loginIp);
        } catch (Exception e) {
            log.error("异步更新最后登录信息失败，userId: {}", userId, e);
        }
    }
}
