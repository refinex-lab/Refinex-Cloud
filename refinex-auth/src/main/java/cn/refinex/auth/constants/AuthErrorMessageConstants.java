package cn.refinex.auth.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 认证模块错误信息常量类
 *
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthErrorMessageConstants {

    public static final String CAPTCHA_REQUIRED = "验证码不能为空";
    public static final String CAPTCHA_EXPIRED = "验证码已过期";
    public static final String CAPTCHA_INVALID = "验证码错误";
}
