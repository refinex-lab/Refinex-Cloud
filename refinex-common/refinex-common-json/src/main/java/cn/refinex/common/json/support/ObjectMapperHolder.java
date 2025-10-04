package cn.refinex.common.json.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

/**
 * 全局 ObjectMapper 持有器，通过 ApplicationContext 初始化时注入，在静态上下文安全获取。
 *
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ObjectMapperHolder {

    private static volatile ObjectMapper objectMapper;

    /**
     * 设置全局 ObjectMapper 实例（仅允许设置一次）
     *
     * @param mapper 全局 ObjectMapper 实例
     */
    public static void set(ObjectMapper mapper) {
        Assert.notNull(mapper, "ObjectMapper must not be null");
        if (objectMapper == null) {
            synchronized (ObjectMapperHolder.class) {
                if (objectMapper == null) {
                    objectMapper = mapper;
                }
            }
        }
    }

    /**
     * 获取全局 ObjectMapper 实例
     *
     * @return 全局 ObjectMapper 实例
     */
    public static ObjectMapper get() {
        if (objectMapper == null) {
            throw new IllegalStateException("ObjectMapper has not been initialized yet.");
        }
        return objectMapper;
    }

    /**
     * 是否已初始化
     */
    public static boolean isInitialized() {
        return objectMapper != null;
    }
}
