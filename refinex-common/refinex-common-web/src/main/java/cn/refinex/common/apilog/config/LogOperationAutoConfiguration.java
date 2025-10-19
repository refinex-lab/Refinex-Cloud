package cn.refinex.common.apilog.config;

import cn.refinex.common.apilog.core.filter.LogOperationFilter;
import cn.refinex.common.apilog.core.interceptor.LogOperationInterceptor;
import cn.refinex.common.constants.WebFilterOrderConstants;
import cn.refinex.common.web.config.WebAutoConfiguration;
import cn.refinex.common.web.config.properties.WebProperties;
import cn.refinex.platform.api.facade.LogOperationFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 操作日志自动配置类
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@AutoConfiguration(after = WebAutoConfiguration.class) // 确保在 WebAutoConfiguration 之后
public class LogOperationAutoConfiguration implements WebMvcConfigurer {

    /**
     * 创建操作日志过滤器
     *
     * @param webProperties      Web 配置属性
     * @param applicationName    应用名称
     * @param logOperationClient 操作日志客户端
     * @return FilterRegistrationBean
     */
    @Bean
    @ConditionalOnProperty(prefix = "refinex.log-operation", value = "enabled", matchIfMissing = true)
    public FilterRegistrationBean<LogOperationFilter> logOperationFilter(WebProperties webProperties, @Value("${spring.application.name}") String applicationName, LogOperationFacade logOperationClient) {
        LogOperationFilter logOperationFilter = new LogOperationFilter(webProperties, applicationName, logOperationClient);
        return WebAutoConfiguration.createFilterBean(logOperationFilter, WebFilterOrderConstants.API_ACCESS_LOG_FILTER);
    }

    /**
     * 添加操作日志拦截器
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogOperationInterceptor());
    }
}
