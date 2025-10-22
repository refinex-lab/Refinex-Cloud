package cn.refinex.auth.config;

import cn.refinex.api.platform.client.EmailServiceClient;
import cn.refinex.api.platform.client.UserServiceClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 客户端配置类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Configuration(value = "authFeignConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {UserServiceClient.class, EmailServiceClient.class})
public class FeignConfiguration {
}
