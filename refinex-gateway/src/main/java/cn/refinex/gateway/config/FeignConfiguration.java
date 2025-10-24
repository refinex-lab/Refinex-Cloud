package cn.refinex.gateway.config;

import cn.refinex.api.platform.client.user.UserRemoteService;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 客户端配置类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Configuration(value = "gatewayFeignConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {UserRemoteService.class})
public class FeignConfiguration {
}
