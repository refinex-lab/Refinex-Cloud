package cn.refinex.common.web.config.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.validation.annotation.Validated;

/**
 * Web 配置属性
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Validated
@ConditionalOnProperty(prefix = "refinex.web")
public class WebProperties {
}
