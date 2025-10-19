package cn.refinex.common.apilog.config;

import cn.refinex.platform.api.facade.LogOperationFacade;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 操作日志客户端自动配置类
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@AutoConfiguration
@EnableFeignClients(clients = {LogOperationFacade.class}) // 主要是引入相关的 Feign 客户端
public class LogOperationClientAutoConfiguration {
}
