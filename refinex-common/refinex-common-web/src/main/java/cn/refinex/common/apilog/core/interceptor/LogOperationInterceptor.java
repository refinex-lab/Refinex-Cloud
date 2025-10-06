package cn.refinex.common.apilog.core.interceptor;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.refinex.common.utils.servlet.ServletUtils;
import cn.refinex.common.utils.spring.SpringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * 操作日志拦截器, 在请求处理前后记录操作日志
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
public class LogOperationInterceptor implements HandlerInterceptor {

    public static final String ATTRIBUTE_HANDLER_METHOD = "HANDLER_METHOD";
    private static final String ATTRIBUTE_STOP_WATCH = "LogOperationInterceptor.StopWatch";

    /**
     * 预处理回调方法, 在控制器处理请求之前调用
     * 目的：
     * 1. 开启计时
     * 2. 打印 Controller 路径
     * 3. 记录 HandleMethod 到请求属性中，后面 LogOperationFilter 会用到
     *
     * @param request  当前请求对象
     * @param response 当前响应对象
     * @param handler  处理请求的处理器对象
     * @return true 继续处理，false 中断处理流程
     * @throws Exception 可能抛出的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 记录 HandleMethod，后面 LogOperationFilter 会用到
        HandlerMethod handlerMethod = handler instanceof HandlerMethod method ? method : null;
        if (Objects.nonNull(handlerMethod)) {
            request.setAttribute(ATTRIBUTE_HANDLER_METHOD, handlerMethod);
        }

        // 如果不是生产环境，就打印 request 日志
        if (!SpringUtils.isProd()) {
            Map<String, String> parameterMap = ServletUtils.getParameterMapFlat(request);
            String requestBody = ServletUtils.getRequestBody(request);
            String uri = ServletUtils.getRequestUri(request);

            if (MapUtils.isEmpty(parameterMap) && StringUtils.isBlank(requestBody)) {
                log.info("[preHandle][开始请求 URI({}), 请求参数为空, 请求体为空]", uri);
            } else {
                log.info("[preHandle][开始请求 URI({}), 请求参数为({}), 请求体为({})]", uri, parameterMap, requestBody);
            }

            // 开启计时
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            request.setAttribute(ATTRIBUTE_STOP_WATCH, stopWatch);

            // 打印 Controller 路径
            printHandlerMethodPosition(handlerMethod);
        }

        return true;
    }

    /**
     * 后处理回调方法, 在控制器处理请求完成后调用
     * 目的：打印请求处理耗时
     *
     * @param request  当前请求对象
     * @param response 当前响应对象
     * @param handler  处理请求的处理器对象
     * @param ex       处理请求过程中抛出的异常, 如果没有异常则为 null
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (!SpringUtils.isProd()) {
            StopWatch stopWatch = (StopWatch) request.getAttribute(ATTRIBUTE_STOP_WATCH);
            stopWatch.stop();

            String uri = ServletUtils.getRequestUri(request);
            log.info("[afterCompletion][请求处理耗时({})][请求 URI({})]", stopWatch.getTotalTimeMillis(), uri);
        }
    }

    /**
     * 打印 HandlerMethod 位置信息
     *
     * @param handlerMethod HandlerMethod 对象
     */
    private void printHandlerMethodPosition(HandlerMethod handlerMethod) {
        if (Objects.isNull(handlerMethod)) {
            return;
        }

        Method method = handlerMethod.getMethod();
        Class<?> clazz = method.getDeclaringClass();

        try {
            // 获取方法行号 lineNumber
            List<String> clazzContents = FileUtil.readUtf8Lines(
                    ResourceUtil.getResource(null, clazz)
                            .getPath()
                            .replace("/target/classes/", "/src/main/java/")
                            + clazz.getSimpleName() + ".java"
            );
            Optional<Integer> lineNumber = IntStream.range(0, clazzContents.size())
                    // 简单匹配，不考虑方法重名
                    .filter(i -> clazzContents.get(i).contains(" " + method.getName() + "("))
                    // 行号从 1 开始
                    .mapToObj(i -> i + 1)
                    .findFirst();
            if (lineNumber.isEmpty()) {
                return;
            }

            log.info("\tController 方法路径：{}({}.java:{})", clazz.getName(), clazz.getSimpleName(), lineNumber.get());
        } catch (Exception ignore) {
            // 忽略异常
        }
    }
}
