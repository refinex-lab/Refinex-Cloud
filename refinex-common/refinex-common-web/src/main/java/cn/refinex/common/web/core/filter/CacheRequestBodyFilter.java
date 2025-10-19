package cn.refinex.common.web.core.filter;

import cn.hutool.core.util.StrUtil;
import cn.refinex.common.utils.servlet.ServletUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Request Body 缓存 Filter, 用于缓存请求体, 方便后续支持 JSON 请求重复读取请求体
 *
 * @author 芋道源码
 * @since 1.0.0
 */
public class CacheRequestBodyFilter extends OncePerRequestFilter {

    /**
     * 忽略的 URI 数组
     * <p>
     * 1. 排除 Spring Boot Admin 相关请求，避免客户端链接中断导致异常（see <a href="https://github.com/YunaiV/ruoyi-vue-pro/issues/795">795 ISSUE</a>）
     * 2. 排除 actuator 相关请求
     */
    private static final String[] IGNORE_URIS = {"/admin/", "/actuator/"};

    /**
     * 缓存请求体
     *
     * @param request     HttpServletRequest
     * @param response    HttpServletResponse
     * @param filterChain FilterChain
     * @throws ServletException ServletException
     * @throws IOException      IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 关键：将原始请求体包装成缓存请求体，方便后续支持 JSON 请求重复读取请求体
        filterChain.doFilter(new CacheRequestBodyWrapper(request), response);
    }

    /**
     * 是否应该不过滤
     *
     * @param request HttpServletRequest
     * @return boolean
     * @throws ServletException ServletException
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 被排除的 URL 不进行过滤
        String requestURI = request.getRequestURI();
        if (StrUtil.startWithAny(requestURI, IGNORE_URIS)) {
            return true;
        }

        // 仅处理 JSON 请求内容
        return !ServletUtils.isJsonRequest(request);
    }
}
