package cn.refinex.common.security.enums;

import cn.refinex.common.exception.code.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;

/**
 * 安全异常码枚举, 定义认证和授权相关的异常码
 * <p>
 * 错误码规范：
 * <ul>
 * <li>认证相关异常码：SECURITY-401xx</li>
 * <li>授权相关异常码：SECURITY-403xx</li>
 * <li>其他安全异常码：SECURITY-500xx</li>
 * </ul>
 * </p>
 *
 * @author Refinex
 * @since 2025-10-04
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SecurityErrorCode implements ErrorCode {

    // 认证相关异常码 (SECURITY-401xx)

    /**
     * 未登录或登录已过期
     */
    NOT_LOGIN("SECURITY-40101", "未登录或登录已过期"),

    /**
     * 未提供登录凭证
     */
    NOT_TOKEN("SECURITY-40102", "未提供登录凭证"),

    /**
     * 登录凭证无效
     */
    INVALID_TOKEN("SECURITY-40103", "登录凭证无效"),

    /**
     * 登录已过期
     */
    TOKEN_TIMEOUT("SECURITY-40104", "登录已过期，请重新登录"),

    /**
     * 账号已在其他设备登录
     */
    BE_REPLACED("SECURITY-40105", "您的账号已在其他设备登录"),

    /**
     * 账号已被强制下线
     */
    KICK_OUT("SECURITY-40106", "您已被强制下线"),

    // 授权相关异常码 (SECURITY-403xx)

    /**
     * 权限不足
     */
    NOT_PERMISSION("SECURITY-40301", "权限不足"),

    /**
     * 角色权限不足
     */
    NOT_ROLE("SECURITY-40302", "角色权限不足"),

    /**
     * 账号已被封禁
     */
    DISABLE_SERVICE("SECURITY-40303", "账号已被封禁"),

    // 验证码相关异常码 (SECURITY-406xx)

    /**
     * 验证码已过期
     */
    CAPTCHA_EXPIRED("SECURITY-40601", "验证码已过期，请刷新后重试"),

    /**
     * 验证码不正确
     */
    CAPTCHA_INVALID("SECURITY-40602", "验证码不正确"),

    /**
     * 验证码不能为空
     */
    CAPTCHA_REQUIRED("SECURITY-40603", "验证码不能为空"),

    // 其他安全异常 (SECURITY-500xx)

    /**
     * 认证服务异常
     */
    AUTH_SERVICE_ERROR("SECURITY-50001", "认证服务异常"),

    /**
     * 验证码生成失败
     */
    CAPTCHA_GENERATE_FAILED("SECURITY-50601", "验证码生成失败"),
    ;

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 异常码
     */
    private final String code;

    /**
     * 异常信息
     */
    private final String message;
}

