package cn.refinex.common.web.handler;

import cn.refinex.common.domain.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.Objects;

import static cn.refinex.common.constants.SystemCommonConstants.API_RESULT_ATTR_NAME;

/**
 * 全局响应体处理器
 * <p>
 * 目的：记录 Controller 返回结果，用于 {@link cn.refinex.common.apilog.core.filter.LogOperationFilter} 记录操作日志
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@ControllerAdvice
public class GlobalResponseBodyHandler implements ResponseBodyAdvice {

    /**
     * 判断是否需要拦截响应体
     *
     * @param returnType    方法参数
     * @param converterType 转换器类型
     * @return 是否需要拦截响应体
     */
    @Override
    @SuppressWarnings("NullableProblems") // 避免 IDEA 警告
    public boolean supports(MethodParameter returnType, Class converterType) {
        Method method = returnType.getMethod();
        if (Objects.isNull(method)) {
            return false;
        }

        // 只拦截返回结果为 ApiResult 的方法
        return method.getReturnType() == ApiResult.class;
    }

    /**
     * 拦截响应体，写入响应到 Attribute
     *
     * @param body                  响应体
     * @param returnType            方法参数
     * @param selectedContentType   选中的内容类型
     * @param selectedConverterType 选中的转换器类型
     * @param request               请求对象
     * @param response              响应对象
     * @return 包装后的响应体
     */
    @Override
    @SuppressWarnings("NullableProblems") // 避免 IDEA 警告
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        servletRequest.setAttribute(API_RESULT_ATTR_NAME, body);
        return body;
    }
}
