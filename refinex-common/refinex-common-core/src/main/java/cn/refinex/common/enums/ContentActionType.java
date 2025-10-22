package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 内容操作类型
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ContentActionType {

    VIEW("VIEW", "查看"),
    LIKE("LIKE", "点赞"),
    COLLECT("COLLECT", "收藏"),
    SHARE("SHARE", "分享"),
    ;

    private final String value;
    private final String description;
}
