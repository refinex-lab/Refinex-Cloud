package cn.refinex.auth.config;

import cn.refinex.api.platform.client.email.EmailRemoteService;
import cn.refinex.api.platform.client.user.UserRemoteService;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 客户端配置类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Configuration(value = "authFeignConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {UserRemoteService.class, EmailRemoteService.class})
public class FeignConfiguration {
}
