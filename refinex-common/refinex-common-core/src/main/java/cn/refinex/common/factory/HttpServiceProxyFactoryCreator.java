package cn.refinex.common.factory;

import cn.refinex.common.config.RestClientConfig;
import cn.refinex.common.properties.HttpInterfaceClientProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * HttpServiceProxyFactory 创建器
 * <p>
 * 提供便捷的方法来为不同服务创建 HttpServiceProxyFactory
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpServiceProxyFactoryCreator {

    private final RestClientConfig restClientConfig;
    private final HttpInterfaceClientProperties properties;
    private final ObjectProvider<RestClient.Builder> normalBuilderProvider;
    private final ObjectProvider<RestClient.Builder> loadBalancedBuilderProvider;

    /**
     * 为指定服务创建 HttpServiceProxyFactory
     * <p>
     * 自动根据配置选择直连或网关模式，支持负载均衡
     *
     * @param serviceName 服务名（如 refinex-auth、refinex-platform）
     * @return HttpServiceProxyFactory 实例
     */
    public HttpServiceProxyFactory createForService(String serviceName) {
        log.info("为服务 [{}] 创建 HttpServiceProxyFactory", serviceName);
        return restClientConfig.createProxyFactoryForService(
                serviceName,
                normalBuilderProvider,
                loadBalancedBuilderProvider,
                properties
        );
    }

    /**
     * 创建 HTTP Interface 客户端代理
     * <p>
     * 这是一个便捷方法，一步完成 Factory 创建和 Client 代理生成
     *
     * @param serviceName 服务名
     * @param clientClass HTTP Interface 接口类
     * @param <T>         接口类型
     * @return 客户端代理实例
     */
    public <T> T createClient(String serviceName, Class<T> clientClass) {
        log.info("为服务 [{}] 创建 HTTP Interface 客户端: {}", serviceName, clientClass.getSimpleName());
        HttpServiceProxyFactory factory = createForService(serviceName);
        return factory.createClient(clientClass);
    }
}

