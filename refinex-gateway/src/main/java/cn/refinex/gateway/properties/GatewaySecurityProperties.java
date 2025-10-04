package cn.refinex.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关安全配置属性
 * <p>
 * 说明：
 * 1. 从配置文件读取白名单路径
 * 2. 支持 Ant 风格通配符
 * 3. 支持动态配置
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway.security")
public class GatewaySecurityProperties {

    /**
     * 是否启用鉴权，默认 true
     */
    private Boolean enabled = true;

    /**
     * 白名单路径（支持 Ant 风格通配符）
     * <p>
     * 示例：
     * - /auth/login
     * - /actuator/**
     * - /swagger-ui/**
     * </p>
     */
    private List<String> whitelistPaths = new ArrayList<>();

    /**
     * 是否打印鉴权日志，默认 false
     */
    private Boolean logEnabled = false;
}

