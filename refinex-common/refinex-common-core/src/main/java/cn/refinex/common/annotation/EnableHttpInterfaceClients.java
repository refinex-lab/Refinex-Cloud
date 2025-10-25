package cn.refinex.common.annotation;

import cn.refinex.common.registrar.HttpInterfaceClientRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 启用 HTTP Interface 客户端自动扫描和注册
 *
 * <p>使用示例：
 * <pre>{@code
 * @Configuration
 * @EnableHttpInterfaceClients(basePackages = "cn.refinex.auth.client")
 * public class ClientConfig {
 * }
 * }</pre>
 *
 * <p>或者更简单的方式，直接在启动类上使用：
 * <pre>{@code
 * @SpringBootApplication
 * @EnableHttpInterfaceClients
 * public class AuthApplication {
 *     public static void main(String[] args) {
 *         SpringApplication.run(AuthApplication.class, args);
 *     }
 * }
 * }</pre>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(HttpInterfaceClientRegistrar.class)
public @interface EnableHttpInterfaceClients {

    /**
     * 要扫描的基础包路径
     * <p>
     * 如果不指定，将扫描标注此注解的类所在的包及其子包
     *
     * @return 基础包路径数组
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * 要扫描的基础包路径
     * <p>
     * 如果不指定，将扫描标注此注解的类所在的包及其子包
     *
     * @return 基础包路径数组
     */
    @AliasFor("value")
    String[] basePackages() default {};

    /**
     * 要扫描的基础类
     * <p>
     * 将扫描这些类所在的包及其子包
     *
     * @return 基础类数组
     */
    Class<?>[] basePackageClasses() default {};
}

