package cn.refinex.kb.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文档状态枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum DocumentStatus {

    DRAFT(0, "草稿", "Draft"),
    PUBLISHED(1, "已发布", "Published"),
    OFFLINE(2, "已下架", "Offline");

    /**
     * 状态值
     */
    private final Integer code;

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
    private static final Map<Integer, DocumentStatus> CODE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(DocumentStatus::getCode, Function.identity()));

    /**
     * 根据 code 获取枚举
     *
     * @param code 状态值
     * @return 文档状态枚举，如果不存在则返回 null
     */
    public static DocumentStatus fromCode(Integer code) {
        return CODE_MAP.get(code);
    }

    /**
     * 获取描述（根据 code）
     *
     * @param code 状态值
     * @return 中文描述，如果不存在则返回 "未知"
     */
    public static String getDescription(Integer code) {
        DocumentStatus status = fromCode(code);
        return status != null ? status.getDescription() : "未知";
    }

    /**
     * 获取英文描述（根据 code）
     *
     * @param code 状态值
     * @return 英文描述，如果不存在则返回 "Unknown"
     */
    public static String getDescriptionEn(Integer code) {
        DocumentStatus status = fromCode(code);
        return status != null ? status.getDescriptionEn() : "Unknown";
    }

    /**
     * 验证 code 是否有效
     *
     * @param code 状态值
     * @return 是否有效
     */
    public static boolean isValid(Integer code) {
        return CODE_MAP.containsKey(code);
    }
}

