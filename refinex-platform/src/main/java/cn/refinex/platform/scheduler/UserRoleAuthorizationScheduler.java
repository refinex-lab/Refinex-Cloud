package cn.refinex.platform.scheduler;

import cn.refinex.platform.repository.sys.SysUserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 用户角色授权定时任务
 * <p>
 * 功能：定时清理过期的临时授权记录
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRoleAuthorizationScheduler {

    private final SysUserRoleRepository userRoleRepository;

    /**
     * 清理过期的临时授权
     * <p>
     * 执行策略：每分钟执行一次
     * <p>
     * 处理逻辑：
     * 1. 查找 valid_until 不为空且小于当前时间的记录
     * 2. 物理删除这些记录
     * 3. 记录删除数量
     */
    @Scheduled(cron = "0 * * * * ?")
    public void cleanExpiredTemporaryAuthorizations() {
        try {
            log.debug("开始清理过期的临时授权...");

            int deletedCount = userRoleRepository.deleteExpiredTemporaryAuthorizations();

            if (deletedCount > 0) {
                log.info("清理过期临时授权完成，删除记录数: {}", deletedCount);
            } else {
                log.debug("没有需要清理的过期临时授权");
            }
        } catch (Exception e) {
            log.error("清理过期临时授权失败", e);
        }
    }
}

