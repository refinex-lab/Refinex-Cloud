package cn.refinex.common.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * HTTP Interface 客户端注解
 * <p>
 * 标记在 HTTP Interface 接口上，自动注册为 Spring Bean
 *
 * <p>使用示例：
 * <pre>{@code
 * @HttpInterfaceClient(serviceName = "refinex-platform")
 * @HttpExchange("/users")
 * public interface PlatformUserServiceClient {
 *     @GetExchange("/{id}")
 *     ApiResult<User> getUserById(@PathVariable Long id);
 * }
 * }</pre>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpInterfaceClient {

    /**
     * 服务名称（必填）
     * <p>
     * 对应 Nacos 注册的服务名或配置文件中的服务键
     * 例如：refinex-auth、refinex-platform
     *
     * @return 服务名称
     */
    @AliasFor("serviceName")
    String value() default "";

    /**
     * 服务名称（必填）
     * <p>
     * 对应 Nacos 注册的服务名或配置文件中的服务键
     * 例如：refinex-auth、refinex-platform
     *
     * @return 服务名称
     */
    @AliasFor("value")
    String serviceName() default "";

    /**
     * Bean 名称（可选）
     * <p>
     * 如果不指定，将使用接口类名首字母小写作为 Bean 名称
     *
     * @return Bean 名称
     */
    String beanName() default "";
}

