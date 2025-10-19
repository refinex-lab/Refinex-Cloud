package cn.refinex.platform.api.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 注册来源
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RegisterSource {

    WEB,
    IOS,
    ANDROID,
    H5,
    ;
}