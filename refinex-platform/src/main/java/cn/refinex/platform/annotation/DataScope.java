package cn.refinex.platform.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限注解
 * <p>
 * 用于标记需要进行数据权限控制的方法
 * </p>
 *
 * @author Refinex
 * @since 2025-10-04
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 数据权限范围
     */
    DataScopeType value() default DataScopeType.SELF;

    /**
     * 数据权限范围枚举
     */
    enum DataScopeType {
        /**
         * 全部数据权限
         */
        ALL,

        /**
         * 部门数据权限
         */
        DEPT,

        /**
         * 部门及以下数据权限
         */
        DEPT_AND_CHILD,

        /**
         * 仅本人数据权限
         */
        SELF
    }
}

