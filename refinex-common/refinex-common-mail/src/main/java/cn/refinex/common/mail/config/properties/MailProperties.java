package cn.refinex.common.mail.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 邮件配置属性类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "refinex.mail")
public class MailProperties {

    /**
     * 是否启用邮件功能
     */
    private Boolean enabled = true;

    /**
     * 默认 SMTP 配置 ID
     */
    private String defaultSmtp = "default";

    /**
     * SMTP 配置列表
     */
    private List<SmtpConfig> smtpConfigs = new ArrayList<>();

    /**
     * 验证码配置
     */
    private VerifyCodeConfig verifyCode = new VerifyCodeConfig();

    /**
     * 队列配置
     */
    private QueueConfig queue = new QueueConfig();

    /**
     * 重试配置
     */
    private RetryConfig retry = new RetryConfig();

    /**
     * 验证码配置类
     */
    @Data
    public static class VerifyCodeConfig {

        /**
         * 验证码有效期（分钟）
         */
        private Integer expireMinutes = 5;

        /**
         * 验证码长度
         */
        private Integer codeLength = 6;

        /**
         * 验证码类型（NUMERIC、ALPHA、ALPHANUMERIC）
         */
        private String codeType = "NUMERIC";

        /**
         * 验证码邮件模板编码
         */
        private String templateCode = "VERIFY_CODE";

        /**
         * 频率限制配置
         */
        private RateLimitConfig rateLimit = new RateLimitConfig();

        /**
         * 频率限制配置类
         */
        @Data
        public static class RateLimitConfig {

            /**
             * 是否启用频率限制
             */
            private Boolean enabled = true;

            /**
             * 同一邮箱每分钟发送次数
             */
            private Integer emailPerMinute = 1;

            /**
             * 同一 IP 每分钟发送次数
             */
            private Integer ipPerMinute = 5;
        }
    }

    /**
     * 队列配置类
     */
    @Data
    public static class QueueConfig {

        /**
         * 是否启用队列功能
         */
        private Boolean enabled = true;

        /**
         * 队列扫描间隔（秒）
         */
        private Integer scanIntervalSeconds = 10;

        /**
         * 每次处理数量
         */
        private Integer batchSize = 50;

        /**
         * 定时任务扫描间隔（秒）
         */
        private Integer scheduledScanIntervalSeconds = 30;
    }

    /**
     * 重试配置类
     */
    @Data
    public static class RetryConfig {

        /**
         * 是否启用重试
         */
        private Boolean enabled = true;

        /**
         * 最大重试次数
         */
        private Integer maxAttempts = 3;

        /**
         * 退避倍数
         */
        private Integer backoffMultiplier = 2;

        /**
         * 初始重试间隔（秒）
         */
        private Integer initialIntervalSeconds = 60;

        /**
         * 重试任务扫描间隔（分钟）
         */
        private Integer scanIntervalMinutes = 5;
    }
}

