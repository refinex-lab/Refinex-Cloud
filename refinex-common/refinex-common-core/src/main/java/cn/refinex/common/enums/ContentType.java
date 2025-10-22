package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 内容类型
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ContentType {

    MARKDOWN("MARKDOWN", "Markdown"),
    RICHTEXT("RICHTEXT", "富文本"),
    VIDEO("VIDEO", "视频"),
    MIXED("MIXED", "混合"),
    ;

    private final String value;
    private final String description;
}
