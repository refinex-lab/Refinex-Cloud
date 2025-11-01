package cn.refinex.ai;

import cn.refinex.common.annotation.EnableHttpInterfaceClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Refinex Cloud AI Application
 *
 * @author Refinex
 * @since 1.0.0
 */
@EnableAsync
@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "cn.refinex")
@EnableHttpInterfaceClients(basePackages = {"cn.refinex.ai.client", "cn.refinex.common"}) // 启用自动扫描和注册 HTTP Interface 客户端
public class RefinexAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefinexAiApplication.class, args);
    }
}
