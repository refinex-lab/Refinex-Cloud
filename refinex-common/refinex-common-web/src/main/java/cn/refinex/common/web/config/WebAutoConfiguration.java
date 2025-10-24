package cn.refinex.common.web.config;

import cn.refinex.common.constants.WebFilterOrderConstants;
import cn.refinex.common.web.config.properties.WebProperties;
import cn.refinex.common.web.core.filter.CacheRequestBodyFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 基础 Web 配置
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(WebProperties.class)
public class WebAutoConfiguration implements WebMvcConfigurer {

    /**
     * 注册 PasswordEncoder 实例，用于密码加密存储和校验
     *
     * @return PasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 缓存请求体过滤器
     * <p>
     * 作用：将原始请求体包装成缓存请求体，方便后续支持 JSON 请求重复读取请求体
     *
     * @return FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean<CacheRequestBodyFilter> requestBodyCacheFilter() {
        return createFilterBean(new CacheRequestBodyFilter(), WebFilterOrderConstants.REQUEST_BODY_CACHE_FILTER);
    }

    /**
     * 创建 Filter 注册 Bean
     *
     * @param filter  过滤器
     * @param order   执行顺序
     * @param <T>     过滤器类型
     * @return FilterRegistrationBean
     */
    public static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter, Integer order) {
        FilterRegistrationBean<T> filterRegistrationBean = new FilterRegistrationBean<>(filter);
        filterRegistrationBean.setOrder(order);
        return filterRegistrationBean;
    }
}
