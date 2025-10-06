package cn.refinex.common.swagger.config.properties;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Swagger 配置属性
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "refinex.swagger")
public class SwaggerProperties {

    /**
     * 是否开启 Swagger
     */
    private boolean enabled = true;

    @NotEmpty(message = "标题不能为空")
    private String title;

    @NotEmpty(message = "描述不能为空")
    private String description;

    @NotEmpty(message = "版本不能为空")
    private String version;

    @NotEmpty(message = "联系人不能为空")
    private String contactName;

    @NotEmpty(message = "联系人邮箱不能为空")
    private String contactEmail;

    @NotEmpty(message = "联系人 URL不能为空")
    private String contactUrl;

    @NotEmpty(message = "许可证不能为空")
    private String license;

    @NotEmpty(message = "许可证 URL不能为空")
    private String licenseUrl;

}
