package cn.refinex.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Refinex Cloud Auth Application
 *
 * @author Refinex
 * @since 1.0.0
 */
@EnableAsync
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"cn.refinex.api.platform.client"}) // 扫描 Feign 客户端接口的包路径生成动态代理类
@SpringBootApplication(scanBasePackages = "cn.refinex")
public class RefinexAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefinexAuthApplication.class, args);
    }
}
