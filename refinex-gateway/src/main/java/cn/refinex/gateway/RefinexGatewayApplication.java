package cn.refinex.gateway;

import cn.refinex.common.annotation.EnableHttpInterfaceClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Refinex Cloud Gateway Application
 *
 * @author Refinex
 * @since 1.0.0
 */
@EnableDiscoveryClient
@SpringBootApplication(
        scanBasePackages = "cn.refinex",
        exclude = {DataSourceAutoConfiguration.class} // 网关不配置数据源, 需要排除数据源自动配置
)
@EnableHttpInterfaceClients(basePackages = {"cn.refinex.gateway.client", "cn.refinex.common"}) // 启用自动扫描和注册 HTTP Interface 客户端
public class RefinexGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefinexGatewayApplication.class, args);
    }
}
