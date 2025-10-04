package cn.refinex.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Refinex Cloud Platform Application
 *
 * @author Refinex
 * @since 1.0.0
 */
@EnableDiscoveryClient
@SpringBootApplication
public class RefinexPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefinexPlatformApplication.class, args);
    }
}
