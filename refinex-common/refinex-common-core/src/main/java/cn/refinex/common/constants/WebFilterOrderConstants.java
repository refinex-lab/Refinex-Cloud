package cn.refinex.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Web 过滤器顺序常量
 * 规则：数值越小，优先级越高
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WebFilterOrderConstants {

    /**
     * CORS 过滤器顺序, 必须在最前面
     */
    public static final int CORS_FILTER = Integer.MIN_VALUE;

    /**
     * 链路追踪过滤器顺序, 在 CORS 过滤器后面
     */
    public static final int TRACE_FILTER = CORS_FILTER + 1;

     /**
     * 请求体缓存过滤器顺序
     */
    public static final int REQUEST_BODY_CACHE_FILTER = Integer.MIN_VALUE + 500;

    /**
     * API 访问日志过滤器顺序, 需要保证在 RequestBodyCacheFilter 后面
     */
    public static final int API_ACCESS_LOG_FILTER = -103;

    /**
     * XSS 过滤器顺序, 需要保证在 RequestBodyCacheFilter 后面
     */
    public static final int XSS_FILTER = -102;

    /**
     * Flowable 过滤器顺序, 需要保证在 Spring Security 过滤后面
     */
    public static final int FLOWABLE_FILTER = -98;

    /**
     * 用户上下文拦截器顺序, 在所有业务拦截器之前，确保正确设置用户上下文
     */
    public static final int USER_CONTEXT_FILTER = -90;
}
