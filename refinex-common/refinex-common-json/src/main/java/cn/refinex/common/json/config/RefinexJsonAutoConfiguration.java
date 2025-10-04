package cn.refinex.common.json.config;

import cn.refinex.common.json.config.properties.RefinexJsonProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.TimeZone;

/**
 * JSON 自动配置类
 * <p>
 * 基于 Spring Boot 官方推荐的 Jackson 机制:
 * <ul>
 *     <li>使用 {@link Jackson2ObjectMapperBuilderCustomizer} 进行增量配置，不破坏 Spring 默认 ObjectMapper。</li>
 *     <li>支持大小写不敏感、日期模块自动注册、容错特性、安全策略等。</li>
 * </ul>
 *
 * @author Refinex
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(RefinexJsonProperties.class)
public class RefinexJsonAutoConfiguration {

    /**
     * 定制 Jackson ObjectMapper。
     * <p>
     * 使用 {@link Jackson2ObjectMapperBuilderCustomizer} 进行属性、容错与模块配置。
     *
     * @param props JSON 配置属性
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    @ConditionalOnMissingBean(name = "refinexJacksonCustomizer")
    public Jackson2ObjectMapperBuilderCustomizer refinexJacksonCustomizer(RefinexJsonProperties props) {
        return builder -> {
            // 注册常用模块
            builder.modulesToInstall(
                    new ParameterNamesModule(),
                    new Jdk8Module(),
                    new JavaTimeModule()
            );

            // 日期与时区配置
            if (props.getDateFormat() != null && !props.getDateFormat().isBlank()) {
                builder.simpleDateFormat(props.getDateFormat());
            }
            builder.timeZone(TimeZone.getTimeZone(props.getTimeZone()));

            // 基础特性配置
            builder
                    // 允许单引号与无引号字段名
                    .featuresToEnable(
                            JsonParser.Feature.ALLOW_SINGLE_QUOTES,
                            JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES
                    )
                    // 禁用 timestamp 输出时间戳，改用 ISO8601
                    .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    // 忽略未知属性（容错反序列化）
                    .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            // 大小写不敏感属性映射
            if (props.isAcceptCaseInsensitiveProperties()) {
                builder.featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
            }

            // null 值序列化策略
            builder.serializationInclusion(
                    props.isIgnoreNull() ? JsonInclude.Include.NON_NULL : JsonInclude.Include.ALWAYS
            );

            // 美化输出
            if (props.isPrettyPrint()) {
                builder.featuresToEnable(SerializationFeature.INDENT_OUTPUT);
            } else {
                builder.featuresToDisable(SerializationFeature.INDENT_OUTPUT);
            }
        };
    }
}
