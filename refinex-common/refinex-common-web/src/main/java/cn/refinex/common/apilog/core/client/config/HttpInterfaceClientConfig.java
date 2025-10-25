package cn.refinex.common.apilog.core.client.config;

import cn.refinex.common.apilog.core.client.PlatformLoggerServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * 基于 RestClient 的 Http Interface 客户端配置类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Configuration("apiLogHttpInterfaceClientConfig")
public class HttpInterfaceClientConfig {

    /**
     * 平台日志服务客户端 Bean 定义
     *
     * @param factory HttpServiceProxyFactory 实例，用于创建客户端代理
     * @return PlatformEmailServiceClient 实例，用于调用平台邮箱服务接口
     */
    @Bean
    public PlatformLoggerServiceClient platformLoggerServiceClient(HttpServiceProxyFactory factory) {
        // 创建客户端代理
        return factory.createClient(PlatformLoggerServiceClient.class);
    }
}
