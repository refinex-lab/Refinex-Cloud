package cn.refinex.common.enums;

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
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum RegisterSource {

    WEB("web"),
    IOS("ios"),
    ANDROID("android"),
    H5("h5"),
    ;

    private final String value;
}
