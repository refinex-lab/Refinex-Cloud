package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 空间类型
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum SpaceType {

    PERSONAL(0, "个人知识库"),
    COURSE(1, "课程专栏"),
    VIDEO(2, "视频专栏"),
    ;

    private final Integer value;
    private final String description;
}
