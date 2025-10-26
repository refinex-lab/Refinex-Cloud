package cn.refinex.kb.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 内容类型枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ContentType {

    MARKDOWN("MARKDOWN", "Markdown", "Markdown格式文档"),
    RICHTEXT("RICHTEXT", "富文本", "富文本编辑器内容"),
    VIDEO("VIDEO", "视频", "视频内容"),
    MIXED("MIXED", "混合", "混合内容类型");

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 中文描述
     */
    private final String description;

    /**
     * 缓存映射
     */
    private static final Map<String, ContentType> CODE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(ContentType::getCode, Function.identity()));

    /**
     * 根据 code 获取枚举
     *
     * @param code 类型编码
     * @return 内容类型枚举，如果不存在则返回 null
     */
    public static ContentType fromCode(String code) {
        return CODE_MAP.get(code);
    }

    /**
     * 获取描述（根据 code）
     *
     * @param code 类型编码
     * @return 中文描述，如果不存在则返回 "未知"
     */
    public static String getDescription(String code) {
        ContentType type = fromCode(code);
        return type != null ? type.getDescription() : "未知";
    }

    /**
     * 验证 code 是否有效
     *
     * @param code 类型编码
     * @return 是否有效
     */
    public static boolean isValid(String code) {
        return CODE_MAP.containsKey(code);
    }
}

