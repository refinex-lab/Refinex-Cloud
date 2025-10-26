package cn.refinex.kb.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 内容行为类型枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ContentActionType {

    VIEW("VIEW", "浏览", "View"),
    LIKE("LIKE", "点赞", "Like"),
    COLLECT("COLLECT", "收藏", "Collect"),
    SHARE("SHARE", "分享", "Share");

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 中文描述
     */
    private final String description;

    /**
     * 英文描述
     */
    private final String descriptionEn;

    /**
     * 缓存映射
     */
    private static final Map<String, ContentActionType> CODE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(ContentActionType::getCode, Function.identity()));

    /**
     * 根据 code 获取枚举
     *
     * @param code 类型编码
     * @return 行为类型枚举，如果不存在则返回 null
     */
    public static ContentActionType fromCode(String code) {
        return CODE_MAP.get(code);
    }

    /**
     * 获取描述（根据 code）
     *
     * @param code 类型编码
     * @return 中文描述，如果不存在则返回 "未知"
     */
    public static String getDescription(String code) {
        ContentActionType type = fromCode(code);
        return type != null ? type.getDescription() : "未知";
    }

    /**
     * 获取英文描述（根据 code）
     *
     * @param code 类型编码
     * @return 英文描述，如果不存在则返回 "Unknown"
     */
    public static String getDescriptionEn(String code) {
        ContentActionType type = fromCode(code);
        return type != null ? type.getDescriptionEn() : "Unknown";
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

