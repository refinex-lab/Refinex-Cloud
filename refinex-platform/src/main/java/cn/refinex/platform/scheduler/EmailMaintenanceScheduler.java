package cn.refinex.platform.scheduler;

import cn.refinex.common.mail.config.properties.MailProperties;
import cn.refinex.platform.service.impl.EmailVerifyCodeServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 邮件维护调度器
 * <p>
 * 负责定时维护任务，如清理过期数据、更新状态等
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "refinex.mail", name = "enabled", havingValue = "true")
public class EmailMaintenanceScheduler {

    private final EmailVerifyCodeServiceImpl verifyCodeService;
    private final MailProperties mailProperties;

    /**
     * 更新过期验证码状态
     * <p>
     * 定时扫描验证码表，将过期的验证码状态更新为已过期
     * </p>
     */
    @Scheduled(cron = "${refinex.mail.verify-code.expire-check-cron:0 0 * * * ?}")
    public void expireOldVerifyCodes() {
        if (Boolean.FALSE.equals(mailProperties.getEnabled())) {
            return;
        }

        try {
            log.debug("开始更新过期验证码状态");
            int expiredCount = verifyCodeService.updateExpiredCodes();
            
            if (expiredCount > 0) {
                log.info("更新过期验证码状态完成，更新数量: {}", expiredCount);
            }
        } catch (Exception e) {
            log.error("更新过期验证码状态失败", e);
        }
    }

}

