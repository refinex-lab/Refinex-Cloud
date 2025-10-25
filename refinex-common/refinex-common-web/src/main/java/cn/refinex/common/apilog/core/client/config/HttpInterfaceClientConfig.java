package cn.refinex.common.apilog.core.client.config;

import cn.refinex.common.annotation.EnableHttpInterfaceClients;
import org.springframework.context.annotation.Configuration;

/**
 * HTTP Interface 客户端配置
 * 启用自动扫描和注册 HTTP Interface 客户端
 *
 * @author Refinex
 * @since 1.0.0
 */
@Configuration
@EnableHttpInterfaceClients(basePackages = "cn.refinex.common.apilog.core.client")
public class HttpInterfaceClientConfig {
    // 无需任何代码，所有客户端将自动扫描和注册
}

