package cn.refinex.auth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 * <p>
 * 说明：
 * 1. 仅使用 Spring Security 的 BCrypt 密码加密功能
 * 2. 禁用 Spring Security 的默认认证和授权功能
 * 3. 认证和授权由 Sa-Token 负责
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置密码编码器（BCrypt）
     * <p>
     * BCrypt 特点：
     * 1. 自动加盐
     * 2. 强度可配置（默认 10）
     * 3. 每次加密结果不同，但验证结果一致
     * </p>
     *
     * @return PasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("配置 BCrypt 密码编码器");
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置 Spring Security 过滤器链
     * <p>
     * 说明：
     * 1. 禁用 CSRF（前后端分离项目不需要）
     * 2. 禁用 Session（使用 Sa-Token 管理 Session）
     * 3. 禁用 HTTP Basic 认证
     * 4. 禁用表单登录
     * 5. 允许所有请求（认证由 Sa-Token 负责）
     * </p>
     *
     * @param http HttpSecurity 对象
     * @return SecurityFilterChain 实例
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("配置 Spring Security 过滤器链（禁用默认认证，使用 Sa-Token）");

        http
                // 禁用 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 禁用 HTTP Basic 认证
                .httpBasic(AbstractHttpConfigurer::disable)
                // 禁用表单登录
                .formLogin(AbstractHttpConfigurer::disable)
                // 禁用登出
                .logout(AbstractHttpConfigurer::disable)
                // 允许所有请求（认证由 Sa-Token 负责）
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}

