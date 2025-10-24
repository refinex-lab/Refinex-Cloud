package cn.refinex.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.refinex.common.jdbc.service.SensitiveDataService;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.common.utils.Fn;
import cn.refinex.common.utils.regex.RegexUtils;
import cn.refinex.platform.domain.entity.sys.SysUser;
import cn.refinex.platform.repository.sys.SysUserRepository;
import cn.refinex.platform.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户通知服务
 * <p>
 * 提供用户相关的邮件通知功能
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {

    private final SysUserRepository sysUserRepository;
    private final SensitiveDataService sensitiveDataService;
    private final EmailSendServiceImpl emailSendService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 发送踢人下线通知
     *
     * @param userId     被踢下线的用户 ID
     * @param deviceType 设备类型
     * @param reason     下线原因
     */
    @Override
    public void sendKickoutNotification(Long userId, String deviceType, String reason) {
        log.info("发送踢人下线通知，userId: {}, deviceType: {}", userId, deviceType);

        try {
            // 1. 获取用户邮箱
            String email = getUserEmail(userId);
            if (StrUtil.isBlank(email)) {
                log.warn("用户邮箱为空，跳过发送通知，userId: {}", userId);
                return;
            }

            // 2. 获取用户信息
            SysUser user = sysUserRepository.selectById(userId);
            if (user == null) {
                log.warn("用户不存在，跳过发送通知，userId: {}", userId);
                return;
            }

            // 3. 获取操作人信息
            String operatorName = getOperatorName();

            // 4. 构建模板变量
            Map<String, Object> variables = new HashMap<>();
            variables.put("username", user.getUsername());
            variables.put("deviceType", StrUtil.isNotBlank(deviceType) ? deviceType : "所有设备");
            variables.put("reason", StrUtil.isNotBlank(reason) ? reason : "管理员操作");
            variables.put("operatorName", operatorName);
            variables.put("kickoutTime", LocalDateTime.now().format(DATE_TIME_FORMATTER));

            // 5. 发送邮件
            emailSendService.sendWithTemplate("USER_KICKOUT", email, variables);
            log.info("踢人下线通知发送成功，userId: {}, email: {}", userId, RegexUtils.desensitizeEmail(email));

        } catch (Exception e) {
            log.error("发送踢人下线通知失败，userId: {}", userId, e);
            // 不抛出异常，避免影响主流程
        }
    }

    /**
     * 发送账号封禁通知
     *
     * @param userId  被封禁的用户 ID
     * @param service 封禁服务（null 表示全局封禁）
     * @param seconds 封禁时长（秒）
     * @param reason  封禁原因
     */
    @Override
    public void sendDisableNotification(Long userId, String service, long seconds, String reason) {
        log.info("发送账号封禁通知，userId: {}, service: {}, seconds: {}", userId, service, seconds);

        try {
            // 1. 获取用户邮箱
            String email = getUserEmail(userId);
            if (StrUtil.isBlank(email)) {
                log.warn("用户邮箱为空，跳过发送通知，userId: {}", userId);
                return;
            }

            // 2. 获取用户信息
            SysUser user = sysUserRepository.selectById(userId);
            if (user == null) {
                log.warn("用户不存在，跳过发送通知，userId: {}", userId);
                return;
            }

            // 3. 获取操作人信息
            String operatorName = getOperatorName();

            // 4. 计算到期时间
            LocalDateTime now = LocalDateTime.now();
            String expireTime = seconds == -1
                    ? "永久"
                    : now.plusSeconds(seconds).format(DATE_TIME_FORMATTER);

            String disableTimeDesc = seconds == -1
                    ? "永久封禁"
                    : String.format("%d 秒（约 %d 天 %d 小时）",
                    seconds, seconds / 86400, (seconds % 86400) / 3600);

            // 5. 构建模板变量
            Map<String, Object> variables = new HashMap<>();
            variables.put("username", user.getUsername());
            variables.put("service", StrUtil.isNotBlank(service) ? service : "全局");
            variables.put("disableSeconds", disableTimeDesc);
            variables.put("reason", StrUtil.isNotBlank(reason) ? reason : "违规操作");
            variables.put("operatorName", operatorName);
            variables.put("disableTime", now.format(DATE_TIME_FORMATTER));
            variables.put("expireTime", expireTime);

            // 6. 发送邮件
            emailSendService.sendWithTemplate("USER_DISABLE", email, variables);
            log.info("账号封禁通知发送成功，userId: {}, email: {}", userId, RegexUtils.desensitizeEmail(email));

        } catch (Exception e) {
            log.error("发送账号封禁通知失败，userId: {}", userId, e);
            // 不抛出异常，避免影响主流程
        }
    }

    /**
     * 发送账号解封通知
     *
     * @param userId  被解封的用户 ID
     * @param service 解封服务（null 表示全局解封）
     */
    @Override
    public void sendUntieNotification(Long userId, String service) {
        log.info("发送账号解封通知，userId: {}, service: {}", userId, service);

        try {
            // 1. 获取用户邮箱
            String email = getUserEmail(userId);
            if (StrUtil.isBlank(email)) {
                log.warn("用户邮箱为空，跳过发送通知，userId: {}", userId);
                return;
            }

            // 2. 获取用户信息
            SysUser user = sysUserRepository.selectById(userId);
            if (user == null) {
                log.warn("用户不存在，跳过发送通知，userId: {}", userId);
                return;
            }

            // 3. 获取操作人信息
            String operatorName = getOperatorName();

            // 4. 构建模板变量
            Map<String, Object> variables = new HashMap<>();
            variables.put("username", user.getUsername());
            variables.put("service", StrUtil.isNotBlank(service) ? service : "全局");
            variables.put("operatorName", operatorName);
            variables.put("untieTime", LocalDateTime.now().format(DATE_TIME_FORMATTER));

            // 5. 发送邮件
            emailSendService.sendWithTemplate("USER_UNTIE", email, variables);
            log.info("账号解封通知发送成功，userId: {}, email: {}", userId, RegexUtils.desensitizeEmail(email));

        } catch (Exception e) {
            log.error("发送账号解封通知失败，userId: {}", userId, e);
            // 不抛出异常，避免影响主流程
        }
    }

    /**
     * 获取用户真实邮箱
     *
     * @param userId 用户 ID
     * @return 真实邮箱，获取失败返回 null
     */
    private String getUserEmail(Long userId) {
        try {
            // 从 sys_sensitive 解密真实邮箱
            return sensitiveDataService.queryAndDecrypt(
                    "sys_user",
                    Fn.getString(userId,null),
                    "email"
            );
        } catch (Exception e) {
            log.error("获取用户真实邮箱失败，userId: {}", userId, e);
            return null;
        }
    }

    /**
     * 获取当前操作人名称
     *
     * @return 操作人名称
     */
    private String getOperatorName() {
        try {
            String username = LoginHelper.getUsername();
            return StrUtil.isNotBlank(username) ? username : "系统管理员";
        } catch (Exception e) {
            return "系统管理员";
        }
    }
}

