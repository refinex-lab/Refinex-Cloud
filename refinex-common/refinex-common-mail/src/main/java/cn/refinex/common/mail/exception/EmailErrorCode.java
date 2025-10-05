package cn.refinex.common.mail.exception;

import cn.refinex.common.exception.code.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 邮件模块错误码
 * <p>
 * 错误码格式：MAIL-NNNN
 * <ul>
 * <li>业务异常：1000-1999</li>
 * <li>系统异常：2000-2999</li>
 * </ul>
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum EmailErrorCode implements ErrorCode {

    // ==================== 业务异常（1000-1999） ====================

    /**
     * 邮件模板不存在
     */
    TEMPLATE_NOT_FOUND("MAIL-1001", "邮件模板不存在"),

    /**
     * 邮件模板编码已存在
     */
    TEMPLATE_CODE_EXISTS("MAIL-1002", "邮件模板编码已存在"),

    /**
     * 邮件模板编码重复
     */
    TEMPLATE_CODE_DUPLICATE("MAIL-1002", "邮件模板编码已存在"),

    /**
     * 邮件模板无效
     */
    TEMPLATE_INVALID("MAIL-1003", "邮件模板无效"),

    /**
     * 邮件模板创建失败
     */
    TEMPLATE_CREATE_FAILED("MAIL-1004", "邮件模板创建失败"),

    /**
     * 系统模板不允许删除
     */
    TEMPLATE_SYSTEM_NOT_DELETABLE("MAIL-1005", "系统模板不允许删除"),

    /**
     * 邮件模板已停用
     */
    TEMPLATE_DISABLED("MAIL-1006", "邮件模板已停用"),

    /**
     * 邮件模板变量缺失
     */
    TEMPLATE_VARIABLE_MISSING("MAIL-1007", "邮件模板变量缺失"),

    /**
     * 邮件模板渲染失败
     */
    TEMPLATE_RENDER_FAILED("MAIL-1008", "邮件模板渲染失败"),

    /**
     * 收件人邮箱地址无效
     */
    INVALID_RECIPIENT_EMAIL("MAIL-1005", "收件人邮箱地址无效"),

    /**
     * 邮件发送失败
     */
    SEND_FAILED("MAIL-1006", "邮件发送失败"),

    /**
     * 邮件队列任务不存在
     */
    QUEUE_TASK_NOT_FOUND("MAIL-1007", "邮件队列任务不存在"),

    /**
     * 验证码发送过于频繁
     */
    VERIFY_CODE_SEND_TOO_FREQUENT("MAIL-1008", "验证码发送过于频繁，请稍后重试"),

    /**
     * 验证码不存在或已过期
     */
    VERIFY_CODE_NOT_FOUND_OR_EXPIRED("MAIL-1009", "验证码不存在或已过期"),

    /**
     * 验证码错误
     */
    VERIFY_CODE_INCORRECT("MAIL-1010", "验证码错误"),

    /**
     * 验证码已使用
     */
    VERIFY_CODE_ALREADY_USED("MAIL-1011", "验证码已使用"),

    /**
     * 验证码已使用
     */
    VERIFY_CODE_USED("MAIL-1011", "验证码已使用"),

    /**
     * 验证码已失效
     */
    VERIFY_CODE_INVALID("MAIL-1012", "验证码已失效"),

    /**
     * 验证码已过期
     */
    VERIFY_CODE_EXPIRED("MAIL-1013", "验证码已过期"),

    // ==================== 系统异常（2000-2999） ====================

    /**
     * SMTP 配置不存在
     */
    SMTP_CONFIG_NOT_FOUND("MAIL-2001", "SMTP 配置不存在"),

    /**
     * SMTP 连接失败
     */
    SMTP_CONNECTION_FAILED("MAIL-2002", "SMTP 连接失败"),

    /**
     * SMTP 认证失败
     */
    SMTP_AUTH_FAILED("MAIL-2003", "SMTP 认证失败"),

    /**
     * 邮件发送超时
     */
    SEND_TIMEOUT("MAIL-2004", "邮件发送超时"),

    /**
     * 邮件队列处理失败
     */
    QUEUE_PROCESS_FAILED("MAIL-2005", "邮件队列处理失败"),

    /**
     * 邮件日志记录失败
     */
    LOG_RECORD_FAILED("MAIL-2006", "邮件日志记录失败"),

    /**
     * 验证码生成失败
     */
    VERIFY_CODE_GENERATE_FAILED("MAIL-2007", "验证码生成失败"),

    /**
     * Redis 操作失败
     */
    REDIS_OPERATION_FAILED("MAIL-2008", "Redis 操作失败");

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误信息
     */
    private final String message;
}

