package cn.refinex.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Refinex Cloud Platform Application
 *
 * @author Refinex
 * @since 1.0.0
 */
@EnableAsync
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "cn.refinex")
@EnableFeignClients(basePackages = "cn.refinex")
public class RefinexPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefinexPlatformApplication.class, args);
    }
}
