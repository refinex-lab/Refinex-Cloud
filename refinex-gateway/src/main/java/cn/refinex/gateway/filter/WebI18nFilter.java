package cn.refinex.gateway.filter;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Locale;

/**
 * 国际化请求过滤器
 *
 * @author Lion Li
 * @since 1.0.0
 */
@Component
public class WebI18nFilter implements WebFilter, Ordered {

    /**
     * 过滤请求
     *
     * @param exchange 服务器Web交换
     * @param chain    网关过滤器链
     * @return 响应 Mono
     */
    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String language = exchange.getRequest().getHeaders().getFirst(HttpHeaders.CONTENT_LANGUAGE);
        Locale locale = Locale.getDefault();

        if (language != null && !language.isEmpty()) {
            String[] split = language.split("_");
            locale = new Locale(split[0], split[1]);
        }

        LocaleContextHolder.setLocaleContext(new SimpleLocaleContext(locale), true);
        return chain.filter(exchange);
    }

    /**
     * 获取过滤器顺序
     *
     * @return 顺序值
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
