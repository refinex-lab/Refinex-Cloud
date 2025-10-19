package cn.refinex.common.web.core.filter;

import cn.refinex.common.web.config.properties.WebProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * API 请求过滤器，记录请求日志
 *
 * @author 芋道源码
 * @since 1.0.0
 */
@RequiredArgsConstructor
public abstract class ApiRequestFilter extends OncePerRequestFilter {

    protected final WebProperties webProperties;

    /**
     * 判断是否需要过滤
     *
     * @param request 当前请求
     * @return 是否需要过滤
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 只过滤 API 请求地址, 暂时使用总控开关，后期考虑根据各服务请求前缀进行过滤
        // String apiUri = request.getRequestURI().substring(request.getContextPath().length());
        return !webProperties.getFilterApiRequest();
    }
}
