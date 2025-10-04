package cn.refinex.common.security.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;

/**
 * Feign 日志拦截器
 * <p>
 * 记录 Feign 调用的请求信息
 * </p>
 *
 * @author Refinex
 * @since 2025-10-04
 */
@Slf4j
public class FeignLogInterceptor implements RequestInterceptor {

    /**
     * 应用 Feign 日志拦截器
     *
     * @param template Feign 请求模板
     */
    @Override
    public void apply(RequestTemplate template) {
        String url = template.url();
        String method = template.method();

        log.info("Feign 调用开始: {} {}", method, url);

        // 记录请求头（排除敏感信息）
        Map<String, Collection<String>> headers = template.headers();
        headers.forEach((key, values) -> {
            if (!key.equalsIgnoreCase("Authorization") && !key.equalsIgnoreCase("satoken")) {
                log.debug("请求头: {} = {}", key, values);
            }
        });
    }
}

