package cn.refinex.common.security.interceptor;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Sa-Token Feign 拦截器
 * <p>
 * 自动将当前请求的 Token 传递到 Feign 调用的下游服务
 * </p>
 *
 * @author Refinex
 * @since 2025-10-04
 */
@Slf4j
public class SaTokenFeignInterceptor implements RequestInterceptor {

    /**
     * 应用 Sa-Token Feign 拦截器
     *
     * @param template Feign 请求模板
     */
    @Override
    public void apply(RequestTemplate template) {
        try {
            // 获取当前请求的 Token
            String token = StpUtil.getTokenValue();

            if (StringUtils.isNotBlank(token)) {
                // 将 Token 添加到请求头（使用 Sa-Token 配置的 token-name）
                String tokenName = StpUtil.getTokenName();
                template.header(tokenName, token);
                log.debug("Feign 调用添加 Token: {} = {}", tokenName, maskToken(token));
            } else {
                log.debug("Feign 调用未获取到 Token");
            }
        } catch (NotLoginException e) {
            log.debug("Feign 调用时用户未登录");
        } catch (Exception e) {
            log.error("Feign 拦截器异常", e);
        }
    }

    /**
     * 脱敏 Token（仅显示前后各 6 位）
     *
     * @param token Token
     * @return 脱敏后的 Token
     */
    private String maskToken(String token) {
        if (token == null || token.length() <= 12) {
            return "***";
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 6);
    }
}

