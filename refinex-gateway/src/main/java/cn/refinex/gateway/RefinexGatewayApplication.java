package cn.refinex.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Refinex Cloud Gateway Application
 *
 * @author Refinex
 * @since 1.0.0
 */
@EnableDiscoveryClient
@SpringBootApplication
public class RefinexGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefinexGatewayApplication.class, args);
    }
}
