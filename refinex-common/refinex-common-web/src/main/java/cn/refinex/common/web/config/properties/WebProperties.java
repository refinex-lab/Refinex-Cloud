package cn.refinex.common.web.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Web 配置属性
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "refinex.web")
public class WebProperties {

    /**
     * 是否过滤 API 请求
     */
    private Boolean filterApiRequest = true;
}
