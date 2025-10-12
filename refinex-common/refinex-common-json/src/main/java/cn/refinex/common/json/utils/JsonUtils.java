package cn.refinex.common.json.utils;

import cn.refinex.common.json.support.ObjectMapperHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JSON 工具类 - 基于 Jackson（ObjectMapper）
 * <p>
 * 基于 Spring Boot 全局 {@link ObjectMapper} 的统一封装。
 * <ul>
 *     <li>自动注入 Spring 上下文中的全局 ObjectMapper</li>
 *     <li>线程安全、支持泛型安全转换、深拷贝</li>
 *     <li>支持 JsonNode 树节点操作</li>
 * </ul>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
public class JsonUtils implements ApplicationContextAware {

    /**
     * 从 Spring 上下文中自动注入全局 ObjectMapper
     *
     * @param applicationContext Spring 应用上下文
     * @throws BeansException 若无法获取全局 ObjectMapper 实例
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            ObjectMapper mapper = applicationContext.getBean(ObjectMapper.class);
            ObjectMapperHolder.set(mapper);
            log.info("[Refinex-JSON] 已注入全局 ObjectMapper 实例：{}", mapper.getClass().getName());
        } catch (BeansException e) {
            log.error("[Refinex-JSON] 获取 ObjectMapper Bean 失败，将使用默认实例", e);
            ObjectMapperHolder.set(new ObjectMapper());
        }
    }

    // ============================================================
    // JSON 序列化
    // ============================================================

    /**
     * 将对象序列化为 JSON 字符串。
     *
     * @param value 任意对象
     * @return JSON 字符串；若转换失败返回 "{}"
     */
    public static String toJson(@Nullable Object value) {
        if (value == null) {
            return "{}";
        }
        try {
            return ObjectMapperHolder.get().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("[Refinex-JSON] 序列化失败: {}", e.getMessage(), e);
            return "{}";
        }
    }

    /**
     * 将对象序列化为美化后的 JSON 字符串。
     *
     * @param value 任意对象
     * @return 格式化 JSON；失败返回 "{}"
     */
    public static String toPrettyJson(@Nullable Object value) {
        if (value == null) {
            return "{}";
        }
        try {
            return ObjectMapperHolder.get().writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("[Refinex-JSON] 美化序列化失败: {}", e.getMessage(), e);
            return "{}";
        }
    }

    // ============================================================
    // JSON 反序列化
    // ============================================================

    /**
     * 将 JSON 字符串反序列化为指定类型对象。
     *
     * @param json  JSON 字符串
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 目标对象；若解析失败返回 null
     */
    @Nullable
    public static <T> T fromJson(@Nullable String json, Class<T> clazz) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return ObjectMapperHolder.get().readValue(json, clazz);
        } catch (IOException e) {
            log.warn("[Refinex-JSON] 反序列化失败，目标类型={}，错误={}", clazz.getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * 使用 TypeReference 反序列化复杂泛型类型。
     *
     * @param json          JSON 字符串
     * @param typeReference 类型引用
     * @param <T>           泛型
     * @return 解析结果；失败返回 null
     */
    @Nullable
    public static <T> T fromJson(@Nullable String json, TypeReference<T> typeReference) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return ObjectMapperHolder.get().readValue(json, typeReference);
        } catch (IOException e) {
            log.warn("[Refinex-JSON] 泛型反序列化失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解析为 Map 对象。
     *
     * @param json JSON 字符串
     * @return Map；失败返回空 Map
     */
    public static Map<String, Object> toMap(@Nullable String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return ObjectMapperHolder.get().readValue(json, new TypeReference<>() {
            });
        } catch (IOException e) {
            log.warn("[Refinex-JSON] 转换为 Map 失败: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * 解析为 List 对象。
     *
     * @param json  JSON 字符串
     * @param clazz 元素类型
     * @param <T>   泛型
     * @return List；失败返回空列表
     */
    public static <T> List<T> toList(@Nullable String json, Class<T> clazz) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            ObjectMapper objectMapper = ObjectMapperHolder.get();
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            log.warn("[Refinex-JSON] 转换为 List 失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 解析为 Set 对象。
     *
     * @param json  JSON 字符串
     * @param clazz 元素类型
     * @param <T>   泛型
     * @return Set；失败返回空 Set
     */
    public static <T> Set<T> toSet(@Nullable String json, Class<T> clazz) {
        if (json == null || json.isBlank()) {
            return Collections.emptySet();
        }
        try {
            ObjectMapper objectMapper = ObjectMapperHolder.get();
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(Set.class, clazz));
        } catch (IOException e) {
            log.warn("[Refinex-JSON] 转换为 Set 失败: {}", e.getMessage());
            return Collections.emptySet();
        }
    }

    // ============================================================
    // JSON 树节点操作
    // ============================================================

    /**
     * 将 JSON 转换为 {@link JsonNode} 树模型。
     *
     * @param json JSON 字符串
     * @return JsonNode；失败返回 null
     */
    @Nullable
    public static JsonNode toTree(@Nullable String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return ObjectMapperHolder.get().readTree(json);
        } catch (IOException e) {
            log.warn("[Refinex-JSON] 解析 JsonNode 失败: {}", e.getMessage());
            return null;
        }
    }

    // ============================================================
    // 对象转换 & 深拷贝
    // ============================================================

    /**
     * 将对象转换为另一类型（字段名匹配拷贝）。
     *
     * @param source     源对象
     * @param targetType 目标类型
     * @param <T>        泛型
     * @return 目标对象；失败返回 null
     */
    @Nullable
    public static <T> T convert(@Nullable Object source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.get().convertValue(source, targetType);
        } catch (IllegalArgumentException e) {
            log.warn("[Refinex-JSON] 对象转换失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 对象深拷贝（序列化 + 反序列化方式）。
     *
     * @param source 源对象
     * @param type   目标类型
     * @param <T>    泛型
     * @return 新对象；失败返回 null
     */
    @Nullable
    public static <T> T deepCopy(@Nullable Object source, Class<T> type) {
        if (source == null) {
            return null;
        }
        try {
            // 先将对象序列化为 JSON 字符串，再反序列化到目标类型
            ObjectMapper objectMapper = ObjectMapperHolder.get();
            String json = objectMapper.writeValueAsString(source);
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            log.warn("[Refinex-JSON] 深拷贝失败: {}", e.getMessage());
            return null;
        }
    }
}
