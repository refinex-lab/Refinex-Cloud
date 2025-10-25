package cn.refinex.auth.client.config;

import cn.refinex.auth.client.PlatformEmailServiceClient;
import cn.refinex.auth.client.PlatformUserServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * 基于 RestClient 的 Http Interface 客户端配置类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Configuration("authHttpInterfaceClientConfig")
public class HttpInterfaceClientConfig {

    /**
     * 平台邮箱服务客户端 Bean 定义
     *
     * @param factory HttpServiceProxyFactory 实例，用于创建客户端代理
     * @return PlatformEmailServiceClient 实例，用于调用平台邮箱服务接口
     */
    @Bean
    public PlatformEmailServiceClient platformEmailServiceClient(HttpServiceProxyFactory factory) {
        // 创建客户端代理
        return factory.createClient(PlatformEmailServiceClient.class);
    }

    /**
     * 平台用户服务客户端 Bean 定义
     *
     * @param factory HttpServiceProxyFactory 实例，用于创建客户端代理
     * @return PlatformUserServiceClient 实例，用于调用平台用户服务接口
     */
    @Bean
    public PlatformUserServiceClient platformUserServiceClient(HttpServiceProxyFactory factory) {
        // 创建客户端代理
        return factory.createClient(PlatformUserServiceClient.class);
    }
}
