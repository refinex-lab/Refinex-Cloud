package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户性别枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum UserSex {

    MALE("male", "男"),
    FEMALE("female", "女"),
    OTHER("other", "其他");

    private final String code;
    private final String info;
}
