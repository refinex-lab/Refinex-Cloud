package cn.refinex.auth.config;

import cn.dev33.satoken.same.SaSameUtil;
import cn.refinex.common.satoken.core.util.LoginHelper;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign 客户端配置类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Configuration
public class FeignAuthConfiguration {

    /**
     * 全局 Feign 拦截器：
     * 1) 透传外部请求的 Authorization 与 clientid（用户态调用场景）
     * 2) 始终附带 Same-Token（服务态信任场景）
     */
    @Bean
    public RequestInterceptor globalFeignAuthRequestInterceptor() {
        return template -> {
            // 透传外部请求的 Authorization 与 clientid（用户态调用场景）
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();

                // 透传外部请求的 Authorization 头（用户态调用场景）
                String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
                if (authorization != null && !authorization.isEmpty()) {
                    template.header(HttpHeaders.AUTHORIZATION, authorization);
                }

                // 透传外部请求的 clientid 头（用户态调用场景）
                String clientId = request.getHeader(LoginHelper.CLIENT_KEY);
                if (clientId != null && !clientId.isEmpty()) {
                    template.header(LoginHelper.CLIENT_KEY, clientId);
                }
            }

            // 服务态认证：Same-Token，始终附带 Same-Token（服务态信任场景）
            template.header(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());
        };
    }
}
