package cn.refinex.common.json.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JSON 配置属性
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "refinex.json")
public class RefinexJsonProperties {

    /**
     * 日期格式（默认：yyyy-MM-dd HH:mm:ss）
     */
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时区（默认：Asia/Shanghai）
     */
    private String timeZone = "Asia/Shanghai";

    /**
     * 是否忽略未知属性（反序列化容错）
     */
    private boolean failOnUnknownProperties = false;

    /**
     * 是否大小写不敏感
     */
    private boolean acceptCaseInsensitiveProperties = true;

    /**
     * 是否忽略 null 值
     */
    private boolean ignoreNull = true;

    /**
     * 是否允许单引号
     */
    private boolean allowSingleQuotes = true;

    /**
     * 是否允许未加引号的字段名
     */
    private boolean allowUnquotedFieldNames = true;

    /**
     * 是否格式化输出
     */
    private boolean prettyPrint = false;
}
