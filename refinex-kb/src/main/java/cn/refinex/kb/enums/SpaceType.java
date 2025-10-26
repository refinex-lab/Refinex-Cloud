package cn.refinex.kb.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 内容空间类型枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SpaceType {

    PERSONAL_KNOWLEDGE(0, "个人知识库", "Personal Knowledge Base"),
    COURSE_COLUMN(1, "课程专栏", "Course Column"),
    VIDEO_COLUMN(2, "视频专栏", "Video Column");

    /**
     * 类型值
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
    private static final Map<Integer, SpaceType> CODE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(SpaceType::getCode, Function.identity()));

    /**
     * 根据 code 获取枚举
     *
     * @param code 类型值
     * @return 空间类型枚举，如果不存在则返回 null
     */
    public static SpaceType fromCode(Integer code) {
        return CODE_MAP.get(code);
    }

    /**
     * 获取描述（根据 code）
     *
     * @param code 类型值
     * @return 中文描述，如果不存在则返回 "未知"
     */
    public static String getDescription(Integer code) {
        SpaceType type = fromCode(code);
        return type != null ? type.getDescription() : "未知";
    }

    /**
     * 获取英文描述（根据 code）
     *
     * @param code 类型值
     * @return 英文描述，如果不存在则返回 "Unknown"
     */
    public static String getDescriptionEn(Integer code) {
        SpaceType type = fromCode(code);
        return type != null ? type.getDescriptionEn() : "Unknown";
    }

    /**
     * 验证 code 是否有效
     *
     * @param code 类型值
     * @return 是否有效
     */
    public static boolean isValid(Integer code) {
        return CODE_MAP.containsKey(code);
    }
}

