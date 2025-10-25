package cn.refinex.auth;

import cn.refinex.common.annotation.EnableHttpInterfaceClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Refinex Cloud Auth Application
 *
 * @author Refinex
 * @since 1.0.0
 */
@EnableAsync
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "cn.refinex")
@EnableHttpInterfaceClients(basePackages = "cn.refinex.auth.client") // 启用自动扫描和注册 HTTP Interface 客户端
public class RefinexAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefinexAuthApplication.class, args);
    }
}
