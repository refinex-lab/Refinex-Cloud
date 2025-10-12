package cn.refinex.gateway.config.propertirs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * API 解密配置属性
 *
 * @author wdhcr
 * @since 1.0.0
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "api-decrypt")
public class ApiDecryptProperties {

    /**
     * 加密开关
     */
    private Boolean enabled;

    /**
     * 头部标识
     */
    private String headerFlag;
}
