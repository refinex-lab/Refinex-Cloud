package cn.refinex.common.invoker;

import cn.refinex.common.annotation.QueryParams;
import cn.refinex.common.json.utils.JsonUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.service.invoker.HttpRequestValues;
import org.springframework.web.service.invoker.HttpServiceArgumentResolver;

import java.util.Collection;
import java.util.Map;

/**
 * 通用的对象转查询参数解析器, 使用反射自动处理任意 POJO 对象
 *
 * @author Refinex
 * @since 1.0.0
 */
public class GenericQueryParamArgumentResolver implements HttpServiceArgumentResolver {

    /**
     * 解析方法参数, 将对象转换为查询参数
     *
     * @param argument      方法参数值
     * @param parameter     方法参数元数据
     * @param requestValues HTTP 请求值构建器
     * @return 如果参数是 {@link QueryParams} 注解的对象, 则返回 true; 否则返回 false
     */
    @Override
    public boolean resolve(@Nullable Object argument, MethodParameter parameter, @NonNull HttpRequestValues.Builder requestValues) {
        // 检查参数是否有自定义注解(@QueryParams)
        if (!parameter.hasParameterAnnotation(QueryParams.class)) {
            return false;
        }

        // 如果参数为 null, 则不添加查询参数
        if (argument == null) {
            return true;
        }

        try {
            // 将对象转换为 Map
            @SuppressWarnings("unchecked")
            Map<String, Object> paramMap = JsonUtils.convert(argument, Map.class);
            if (MapUtils.isEmpty(paramMap)) {
                return true;
            }

            // 添加到请求参数
            paramMap.forEach((key, value) -> {
                if (value != null) {
                    if (value instanceof Collection<?> collection) {
                        // 处理集合类型(多值参数)
                        collection.forEach(item -> requestValues.addRequestParameter(key, item.toString()));
                    } else {
                        // 处理单值参数
                        requestValues.addRequestParameter(key, value.toString());
                    }
                }
            });

            return true;
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("无法将参数转换为查询参数: " + parameter.getParameterName(), e);
        }
    }
}
