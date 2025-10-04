package cn.refinex.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Refinex Cloud Auth Application
 *
 * @author Refinex
 * @since 1.0.0
 */
@EnableDiscoveryClient
@SpringBootApplication
public class RefinexAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefinexAuthApplication.class, args);
    }
}
