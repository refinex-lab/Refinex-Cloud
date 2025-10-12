package cn.refinex.gateway;

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
public class RefinexGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefinexGatewayApplication.class, args);
    }
}
