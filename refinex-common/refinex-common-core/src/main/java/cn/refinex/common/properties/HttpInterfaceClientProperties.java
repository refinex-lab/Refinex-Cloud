package cn.refinex.common.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

/**
 * Http 接口客户端配置属性
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Validated
@RefreshScope
@ConfigurationProperties(prefix = "http-interface-client")
public class HttpInterfaceClientProperties {

    /**
     * 默认基础 URL (用于网关调用场景)
     * 格式：http://IP:PORT、http://DOMAIN:PORT、http://gateway-url
     */
    @NotBlank(message = "默认基础 URL 不能为空")
    private String baseUrl;

    /**
     * 按服务名配置的 URL 映射 (用于服务间直连场景)
     * key: 服务名 (如 refinex-auth、refinex-platform)
     * value: 服务地址 (支持 http://IP:PORT 或 lb://service-name 格式)
     * <p>
     * 示例配置：
     * <pre>{@code
     * http-interface-client:
     *   services:
     *     refinex-auth: lb://refinex-auth
     *     refinex-platform: lb://refinex-platform
     * }</pre>
     */
    private Map<String, String> services = new HashMap<>();

    /**
     * 是否优先使用服务直连 (默认 true)
     * true: 优先使用 services 配置的服务地址，未配置时降级到 baseUrl
     * false: 始终使用 baseUrl (网关模式)
     */
    private boolean preferDirectCall = true;

    /**
     * 根据服务名获取目标 URL
     *
     * @param serviceName 服务名
     * @return 目标 URL
     */
    public String getServiceUrl(String serviceName) {
        // 如果启用服务直连且配置了该服务的地址，则使用服务地址
        if (preferDirectCall && services.containsKey(serviceName)) {
            return services.get(serviceName);
        }
        // 否则使用默认 baseUrl (网关地址)
        return baseUrl;
    }
}
