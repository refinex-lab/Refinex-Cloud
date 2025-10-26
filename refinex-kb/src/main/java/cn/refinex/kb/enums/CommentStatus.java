package cn.refinex.kb.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 评论状态枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CommentStatus {

    PENDING_AUDIT(0, "待审核", "Pending Audit"),
    PUBLISHED(1, "已发布", "Published"),
    DELETED(2, "已删除", "Deleted");

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
    private static final Map<Integer, CommentStatus> CODE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(CommentStatus::getCode, Function.identity()));

    /**
     * 根据 code 获取枚举
     *
     * @param code 状态值
     * @return 评论状态枚举，如果不存在则返回 null
     */
    public static CommentStatus fromCode(Integer code) {
        return CODE_MAP.get(code);
    }

    /**
     * 获取描述（根据 code）
     *
     * @param code 状态值
     * @return 中文描述，如果不存在则返回 "未知"
     */
    public static String getDescription(Integer code) {
        CommentStatus status = fromCode(code);
        return status != null ? status.getDescription() : "未知";
    }

    /**
     * 获取英文描述（根据 code）
     *
     * @param code 状态值
     * @return 英文描述，如果不存在则返回 "Unknown"
     */
    public static String getDescriptionEn(Integer code) {
        CommentStatus status = fromCode(code);
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

