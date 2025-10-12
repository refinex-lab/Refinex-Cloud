package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录类型枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum LoginType {

    PASSWORD,
    EMAIL;
}
