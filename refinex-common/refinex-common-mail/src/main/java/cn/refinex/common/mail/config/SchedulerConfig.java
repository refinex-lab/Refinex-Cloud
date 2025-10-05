package cn.refinex.common.mail.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务配置类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
    // 启用 Spring 定时任务支持
}

