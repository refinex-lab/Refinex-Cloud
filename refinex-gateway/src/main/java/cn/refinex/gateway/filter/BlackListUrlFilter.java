package cn.refinex.gateway.filter;

import cn.refinex.gateway.utils.WebFluxUtils;
import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 黑名单 URL 过滤
 *
 * @author ruoyi
 * @author Refinex
 * @since 1.0.0
 */
@Component
public class BlackListUrlFilter extends AbstractGatewayFilterFactory<BlackListUrlFilter.Config> {

    /**
     * 构造函数，初始化配置类
     */
    public BlackListUrlFilter() {
        super(Config.class);
    }

    /**
     * 应用黑名单 URL 过滤
     *
     * @param config 配置参数
     * @return 网关过滤器实例
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 获取请求路径
            String url = exchange.getRequest().getURI().getPath();
            // 检查请求路径是否在黑名单中
            if (config.matchBlacklist(url)) {
                return WebFluxUtils.webFluxResponseWriter(exchange.getResponse(), "请求地址不允许访问");
            }

            // 不是黑名单中的路径，继续过滤
            return chain.filter(exchange);
        };
    }

    /**
     * 黑名单 URL 配置类
     */
    @Data
    public static class Config {

        /**
         * 黑名单 URL 列表
         */
        private List<String> blacklistUrl;

        /**
         * 编译后的黑名单 URL 正则表达式列表
         */
        private List<Pattern> blacklistUrlPattern = new ArrayList<>();

        /**
         * 设置黑名单 URL 列表
         *
         * @param blacklistUrl 黑名单 URL 列表
         */
        public void setBlacklistUrl(List<String> blacklistUrl) {
            this.blacklistUrl = blacklistUrl;
            this.blacklistUrlPattern.clear();
            this.blacklistUrl.forEach(url -> this.blacklistUrlPattern
                    .add(Pattern.compile(
                            url.replace("**", "(.*?)"),
                            Pattern.CASE_INSENSITIVE)
                    )
            );
        }

        /**
         * 检查 URL 是否匹配黑名单中的正则表达式
         *
         * @param url 请求路径
         * @return 如果 URL 匹配黑名单中的正则表达式，则返回 true；否则返回 false
         */
        public boolean matchBlacklist(String url) {
            return !blacklistUrlPattern.isEmpty()
                    && blacklistUrlPattern.stream().anyMatch(p -> p.matcher(url).find());
        }
    }
}
