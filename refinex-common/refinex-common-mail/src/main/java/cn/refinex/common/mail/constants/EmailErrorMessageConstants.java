package cn.refinex.common.mail.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * 邮件模块错误信息
 *
 * @author Refinex
 * @since 1.0.0
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmailErrorMessageConstants {

    /**
     * 邮件模板不存在
     */
    public static final String TEMPLATE_NOT_FOUND = "邮件模板不存在";

    /**
     * 邮件模板编码已存在
     */
    public static final String TEMPLATE_CODE_EXISTS = "邮件模板编码已存在";

    /**
     * 邮件模板编码重复
     */
    public static final String TEMPLATE_CODE_DUPLICATE = "邮件模板编码已存在";

    /**
     * 邮件模板无效
     */
    public static final String TEMPLATE_INVALID = "邮件模板无效";

    /**
     * 邮件模板创建失败
     */
    public static final String TEMPLATE_CREATE_FAILED = "邮件模板创建失败";

    /**
     * 系统模板不允许删除
     */
    public static final String TEMPLATE_SYSTEM_NOT_DELETABLE = "系统模板不允许删除";

    /**
     * 邮件模板已停用
     */
    public static final String TEMPLATE_DISABLED = "邮件模板已停用";

    /**
     * 邮件模板变量缺失
     */
    public static final String TEMPLATE_VARIABLE_MISSING = "邮件模板变量缺失";

    /**
     * 邮件模板渲染失败
     */
    public static final String TEMPLATE_RENDER_FAILED = "邮件模板渲染失败";

    /**
     * 收件人邮箱地址无效
     */
    public static final String INVALID_RECIPIENT_EMAIL = "收件人邮箱地址无效";

    /**
     * 邮件发送失败
     */
    public static final String SEND_FAILED = "邮件发送失败";

    /**
     * 邮件队列任务不存在
     */
    public static final String QUEUE_TASK_NOT_FOUND = "邮件队列任务不存在";

    /**
     * 验证码发送过于频繁
     */
    public static final String VERIFY_CODE_SEND_TOO_FREQUENT = "验证码发送过于频繁，请稍后重试";

    /**
     * 验证码不存在或已过期
     */
    public static final String VERIFY_CODE_NOT_FOUND_OR_EXPIRED = "验证码不存在或已过期";

    /**
     * 验证码错误
     */
    public static final String VERIFY_CODE_INCORRECT = "验证码错误";

    /**
     * 验证码已使用
     */
    public static final String VERIFY_CODE_ALREADY_USED = "验证码已使用";

    /**
     * 验证码已使用
     */
    public static final String VERIFY_CODE_USED = "验证码已使用";

    /**
     * 验证码已失效
     */
    public static final String VERIFY_CODE_INVALID = "验证码已失效";

    /**
     * 验证码已过期
     */
    public static final String VERIFY_CODE_EXPIRED = "验证码已过期";

    /**
     * SMTP 配置不存在
     */
    public static final String SMTP_CONFIG_NOT_FOUND = "SMTP 配置不存在";

    /**
     * SMTP 连接失败
     */
    public static final String SMTP_CONNECTION_FAILED = "SMTP 连接失败";

    /**
     * SMTP 认证失败
     */
    public static final String SMTP_AUTH_FAILED = "SMTP 认证失败";

    /**
     * 邮件发送超时
     */
    public static final String SEND_TIMEOUT = "邮件发送超时";

    /**
     * 邮件队列处理失败
     */
    public static final String QUEUE_PROCESS_FAILED = "邮件队列处理失败";

    /**
     * 邮件日志记录失败
     */
    public static final String LOG_RECORD_FAILED = "邮件日志记录失败";

    /**
     * 验证码生成失败
     */
    public static final String VERIFY_CODE_GENERATE_FAILED = "验证码生成失败";

    /**
     * Redis 操作失败
     */
    public static final String REDIS_OPERATION_FAILED = "Redis 操作失败";

}

