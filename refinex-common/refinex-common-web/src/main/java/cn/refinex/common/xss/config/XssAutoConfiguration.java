package cn.refinex.common.xss.config;

import cn.refinex.common.constants.WebFilterOrderConstants;
import cn.refinex.common.web.config.WebAutoConfiguration;
import cn.refinex.common.xss.config.properties.XssProperties;
import cn.refinex.common.xss.core.clean.JsoupXssCleaner;
import cn.refinex.common.xss.core.clean.XssCleaner;
import cn.refinex.common.xss.core.filter.XssFilter;
import cn.refinex.common.xss.core.json.XssStringJsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * XSS 自动配置类
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(XssProperties.class)
@ConditionalOnProperty(prefix = "refinex.xss", name = "enabled", havingValue = "true", matchIfMissing = true)
public class XssAutoConfiguration implements WebMvcConfigurer {

    /**
     * 创建 XssCleaner Bean, 用于对字符串进行 XSS 过滤
     *
     * @return XssCleaner
     */
    @Bean
    @ConditionalOnMissingBean(XssCleaner.class)
    public XssCleaner xssCleaner() {
        return new JsoupXssCleaner();
    }

    /**
     * 创建 XssJacksonCustomizer Bean, 用于在 Jackson 序列化时进行 XSS 过滤
     *
     * @param properties  XSS 配置类
     * @param pathMatcher 路径匹配器
     * @param xssCleaner  XSS 清理器
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    @ConditionalOnMissingBean(name = "xssJacksonCustomizer")
    @ConditionalOnBean(ObjectMapper.class)
    @ConditionalOnProperty(prefix = "refinex.xss", name = "enabled", havingValue = "true", matchIfMissing = true)
    public Jackson2ObjectMapperBuilderCustomizer xssJacksonCustomizer(XssProperties properties, PathMatcher pathMatcher, XssCleaner xssCleaner) {
        // 使用 XssStringJsonDeserializer 在反序列化时进行 XSS 过滤
        return builder -> builder.deserializerByType(String.class, new XssStringJsonDeserializer(properties, pathMatcher, xssCleaner));
    }

    /**
     * 创建 XssFilter Bean, 用于在 Servlet 中进行 XSS 过滤
     *
     * @param properties  XSS 配置类
     * @param pathMatcher 路径匹配器
     * @param xssCleaner  XSS 清理器
     * @return FilterRegistrationBean
     */
    @Bean
    @ConditionalOnBean(XssCleaner.class)
    public FilterRegistrationBean<XssFilter> xssFilter(XssProperties properties, PathMatcher pathMatcher, XssCleaner xssCleaner) {
        return WebAutoConfiguration.createFilterBean(new XssFilter(properties, pathMatcher, xssCleaner), WebFilterOrderConstants.XSS_FILTER);
    }
}
