package cn.refinex.common.protection.signature.core.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * HTTP API 签名注解
 *
 * @author Zhougang
 * @since 1.0.0
 */
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiSignature {

    /**
     * 同一个接口的超时时间，单位：秒 默认 60 秒
     *
     * @return 超时时间
     */
    int timeout() default 60;

    /**
     * 超时时间单位，默认秒
     *
     * @return 超时时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 签名错误时的提示信息
     *
     * @return 签名错误时的提示信息
     */
    String message() default "签名不正确";

    /**
     * 签名字段：appId (应用ID)
     *
     * @return 签名字段：appId (应用ID)
     */
    String appId() default "appId";

    /**
     * 签名字段：timestamp (时间戳)
     *
     * @return 签名字段：timestamp (时间戳)
     */
    String timestamp() default "timestamp";

    /**
     * 签名字段：nonce 随机数，10位以上
     *
     * @return 签名字段：nonce 随机数，10位以上
     */
    String nonce() default "nonce";

    /**
     * 签名字段：sign (客户端签名)
     *
     * @return 签名字段：sign (客户端签名)
     */
    String sign() default "sign";
}
