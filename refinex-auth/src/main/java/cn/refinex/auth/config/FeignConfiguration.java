package cn.refinex.auth.config;

import cn.refinex.platform.api.facade.EmailFacade;
import cn.refinex.platform.api.facade.UserFacade;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 客户端配置类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Configuration(value = "authFeignConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {UserFacade.class, EmailFacade.class})
public class FeignConfiguration {
}
