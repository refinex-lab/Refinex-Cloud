package cn.refinex.common.web.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Web 配置属性
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "refinex.web")
public class WebProperties {

    @NotNull(message = "APP API 不能为空")
    private Api appApi = new Api("/app-api", "**.controller.app.**");

    @NotNull(message = "Admin API 不能为空")
    private Api adminApi = new Api("/admin-api", "**.controller.admin.**");

    @Data
    @Valid
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Api {

        /**
         * API 前缀, 所有 Controller 提供的 RESTFul API 的统一前缀
         * <p>
         * 目的：通过该前缀避免 Swagger、Actuator 意外通过 Nginx 暴露出来给外部带来的安全性问题。
         * 如此一来，Nginx 只需要配置转发到 /api/** 的所有请求到 Spring Boot 应用即可。
         */
        @NotEmpty(message = "API 前缀不能为空")
        private String prefix;

        /**
         * Controller 所在包的 Ant 路径规则
         * <p>
         * 目的：给该 Controller 设置指定的 {@link #prefix}
         */
        @NotEmpty(message = "Controller 所在包不能为空")
        private String controller;
    }
}
