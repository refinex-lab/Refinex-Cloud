package cn.refinex.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 用户密码配置属性
 *
 * @author Lion Li
 * @since 1.0.0
 */
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "user.password")
public class UserPasswordProperties {

    /**
     * 密码最大错误次数, 默认5次
     */
    private Integer maxRetryCount = 5;

    /**
     * 密码锁定时间, 单位: 分钟, 默认10分钟
     */
    private Integer lockTime = 10;
}
