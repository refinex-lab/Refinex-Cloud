package cn.refinex.common.apilog.core.annotation;

import cn.refinex.common.apilog.core.enums.OperateTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 *
 * @author Refinex
 * @since 1.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogOperation {

    /**
     * 是否开启操作日志记录, 默认开启
     *
     * @return true 开启，false 关闭
     */
    boolean enabled() default true;

    /**
     * 是否记录请求参数, 默认开启
     *
     * @return true 开启，false 关闭
     */
    boolean recordRequestParams() default true;

    /**
     * 是否记录响应参数, 默认开启
     *
     * @return true 开启，false 关闭
     */
    boolean recordResponseBody() default true;

    /**
     * 敏感参数数组, 用于过滤请求参数中的敏感信息
     * <p>
     * 例如：password、confirmPassword、oldPassword、newPassword 等
     */
    String[] sensitiveParams() default {};

    /**
     * 操作模块, 为空则尝试获取 {@link io.swagger.v3.oas.annotations.tags.Tag#name()} 属性值
     */
    String operateModule() default "";

    /**
     * 操作描述, 为空则尝试获取 {@link io.swagger.v3.oas.annotations.Operation#summary()} 属性值
     */
    String operateDesc() default "";

    /**
     * 操作类型
     */
    OperateTypeEnum[] operationType() default {};
}
