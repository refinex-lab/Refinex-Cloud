package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文档状态
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum DocStatus {

    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    SOLD_OFF(2, "已下架"),
    ;

    private final Integer value;
    private final String description;
}
