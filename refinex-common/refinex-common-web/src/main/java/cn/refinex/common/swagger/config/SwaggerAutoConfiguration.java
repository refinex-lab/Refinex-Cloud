package cn.refinex.common.swagger.config;

import cn.refinex.common.swagger.config.properties.SwaggerProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

/**
 * Swagger 自动配置类, 基于 OpenAPI + Springdoc 实现
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass({OpenAPI.class})
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(prefix = "refinex.swagger", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerAutoConfiguration {

    /**
     * 创建 OpenAPI 对象
     *
     * @param properties Swagger 配置类
     * @return OpenAPI 对象
     */
    @Bean
    public OpenAPI createApi(SwaggerProperties properties) {
        // 构建 SecuritySchemes
        Map<String, SecurityScheme> securitySchemas = buildSecuritySchemes();

        // 构建 OpenAPI 对象
        OpenAPI openAPI = new OpenAPI()
                // 配置 Info 信息
                .info(buildInfo(properties))
                // 配置 SecuritySchemes
                .components(new Components().securitySchemes(securitySchemas))
                .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION));

        // 配置 SecurityRequirement
        securitySchemas.keySet().forEach(key -> openAPI.addSecurityItem(new SecurityRequirement().addList(key)));
        return openAPI;
    }

    /**
     * 构建 OpenAPI 的 All 分组, 包含所有的路径
     *
     * @return GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi allGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("all")
                // 这里直接包含所有的路径好了
                .pathsToMatch("/**")
                .build();
    }

    /**
     * 构建 OpenAPI 的 Info 信息
     *
     * @param properties Swagger 配置类
     * @return Info 信息
     */
    private Info buildInfo(SwaggerProperties properties) {
        return new Info()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .version(properties.getVersion())
                .contact(new Contact()
                        .name(properties.getContactName())
                        .email(properties.getContactEmail())
                        .url(properties.getContactUrl()))
                .license(new License()
                        .name(properties.getLicense())
                        .url(properties.getLicenseUrl()));
    }

    /**
     * 构建 OpenAPI 的 SecuritySchemes, 通过请求头 Authorization 传递 Bearer Token
     *
     * @return SecuritySchemes
     */
    private Map<String, SecurityScheme> buildSecuritySchemes() {
        Map<String, SecurityScheme> securitySchemes = new HashMap<>();
        SecurityScheme securityScheme = new SecurityScheme()
                // 类型
                .type(SecurityScheme.Type.APIKEY)
                // 请求头的 name
                .name(HttpHeaders.AUTHORIZATION)
                // token 所在位置
                .in(SecurityScheme.In.HEADER);
        securitySchemes.put(HttpHeaders.AUTHORIZATION, securityScheme);
        return securitySchemes;
    }
}
