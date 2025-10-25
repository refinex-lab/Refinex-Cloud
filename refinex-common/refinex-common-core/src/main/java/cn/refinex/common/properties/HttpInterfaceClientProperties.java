package cn.refinex.common.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Http 接口客户端配置属性
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "http-interface-client")
public class HttpInterfaceClientProperties {

    /**
     * 基础 URL (http://IP:PORT、http://DOMAIN:PORT、http://service-name)
     */
    @NotBlank(message = "基础 URL 不能为空")
    private String baseUrl;
}
