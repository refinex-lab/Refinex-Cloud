package cn.refinex.common.web.config;

import cn.refinex.common.web.config.properties.WebProperties;
import jakarta.servlet.Filter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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
