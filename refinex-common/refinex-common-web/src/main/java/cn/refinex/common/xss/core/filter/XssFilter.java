package cn.refinex.common.xss.core.filter;

import cn.refinex.common.utils.servlet.ServletUtils;
import cn.refinex.common.xss.config.properties.XssProperties;
import cn.refinex.common.xss.core.clean.XssCleaner;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * XSS 过滤器
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@AllArgsConstructor
public class XssFilter extends OncePerRequestFilter {

    private final XssProperties xssProperties;
    private final PathMatcher pathMatcher;
    private final XssCleaner xssCleaner;

    /**
     * 对请求参数进行 XSS 过滤
     *
     * @param request     HTTP 请求
     * @param response    HTTP 响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 将请求通过 XSS 请求包装类, 对请求参数进行 XSS 清理
        filterChain.doFilter(new XssRequestWrapper(request, xssCleaner), response);
    }

    /**
     * 判断是否需要跳过 XSS 过滤
     *
     * @param request HTTP 请求
     * @return 是否需要跳过
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // 未开启 XSS 过滤，直接返回 true
        if (Boolean.FALSE.equals(xssProperties.getEnabled())) {
            return true;
        }

        // 检查请求路径是否在排除列表中
        String uri = ServletUtils.getRequestUri(request);
        return xssProperties.getExcludeUrls().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }
}
