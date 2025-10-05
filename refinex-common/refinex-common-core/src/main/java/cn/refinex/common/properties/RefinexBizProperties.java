package cn.refinex.common.properties;

import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 业务相关配置属性
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@SpringBootConfiguration
@ConfigurationProperties(prefix = "refinex.biz")
public class RefinexBizProperties {

    /**
     * 数据库敏感数据加密密钥
     */
    private String dbSensitiveDataKey;
}
