package cn.refinex.common.security.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.util.SaResult;
import cn.refinex.common.utils.spring.SpringUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 安全自动配置类
 *
 * @author Lion Li
 * @since 1.0.0
 */
@AutoConfiguration
public class SecurityAutoConfiguration implements WebMvcConfigurer {

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
     * 添加 Sa-Token 拦截器
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 路由拦截器拦截所有路径，自定义拦截验证规则
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }

    /**
     * 注册 Sa-Token 全局过滤器进行所有路径的鉴权操作
     *
     * @return SaServletFilter 实例
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                // 过滤所有路径
                .addInclude("/**")
                // 排除 actuator 路径
                .addExclude("/actuator", "/actuator/**")
                // 排除 Swagger/Knife4j 相关路径
                .addExclude("/doc.html", "/doc.html/**")
                .addExclude("/swagger-ui.html", "/swagger-ui/**")
                .addExclude("/swagger-resources", "/swagger-resources/**")
                .addExclude("/v3/api-docs", "/v3/api-docs/**")
                .addExclude("/webjars/**", "/favicon.ico")
                // 排除 auth 模块的 登录/注册/验证码 路径
                .addExclude("/auth/login", "/auth/register", "/captcha")
                // 排除 platform 模块的 操作日志 路径
                .addExclude("/logger")
                // 自定义认证规则
                .setAuth(obj -> {
                    // 检查是否校验 Same-Token (部分 rpc 插件有效)
                    Boolean checkSameToken = SaManager.getConfig().getCheckSameToken();
                    if (Boolean.TRUE.equals(checkSameToken)) {
                        // 校验当前 Request 上下文提供的 Same-Token 是否有效 (如果无效则抛出异常)
                        SaSameUtil.checkCurrentRequestToken();
                    }
                })
                // 自定义错误规则
                .setError(e -> SaResult.error("认证失败，无法访问系统资源").setCode(HttpStatus.UNAUTHORIZED.value()));
    }

    /**
     * 注册 Sa-Token 全局过滤器进行 actuator 路径的鉴权操作
     *
     * @return SaReactorFilter 实例
     */
    @Bean
    public SaServletFilter actuatorFilter() {
        // 从 Spring 环境中获取 actuator 用户名和密码, 用户名和密码通过元数据存储在 nacos 中
        String username = SpringUtils.getProperty("spring.cloud.nacos.discovery.metadata.username");
        String password = SpringUtils.getProperty("spring.cloud.nacos.discovery.metadata.userpassword");

        return new SaServletFilter()
                // 过滤 actuator 路径
                .addInclude("/actuator", "/actuator/**")
                // 自定义认证规则：检查 HttpBasic 认证是否正确
                .setAuth(obj -> SaHttpBasicUtil.check(username + ":" + password))
                // 自定义错误规则
                .setError(e -> SaResult.error(e.getMessage()).setCode(HttpStatus.UNAUTHORIZED.value()));
    }
}
