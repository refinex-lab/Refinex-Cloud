package cn.refinex.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Refinex Cloud Platform Application
 *
 * @author Refinex
 * @since 1.0.0
 */
@EnableAsync
@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "cn.refinex")
public class RefinexPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefinexPlatformApplication.class, args);
    }
}
