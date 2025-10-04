package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评论状态
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum CommentStatus {

    PENDING("0", "待审核"),
    PUBLISHED("1", "已发布"),
    DELETED("2", "已删除"),
    ;

    private final String value;
    private final String description;
}
