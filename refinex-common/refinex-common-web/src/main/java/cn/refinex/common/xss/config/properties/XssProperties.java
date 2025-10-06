package cn.refinex.common.xss.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * XSS 配置属性类
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "refinex.xss")
public class XssProperties {

    /**
     * 是否开启 XSS 过滤
     */
    private Boolean enabled = Boolean.TRUE;

    /**
     * 不开启 XSS 过滤的 URL 列表
     */
    private List<String> excludeUrls = new ArrayList<>();
}
