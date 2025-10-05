package cn.refinex.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Refinex Cloud Auth Application
 *
 * @author Refinex
 * @since 1.0.0
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "cn.refinex")
public class RefinexAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefinexAuthApplication.class, args);
    }
}
