package cn.refinex.common.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 基础异常类
 * <p>
 * 统一定义微服务异常的基础结构与行为，作为所有自定义异常的父类。 包含所属模块、错误码、错误信息与时间戳等核心属性，便于日志、监控与排障。
 * <p>
 * 错误码规范：
 * <ul>
 * <li>格式：MODULE-NNNN，例如 COMMON-1001、ORDER-2001</li>
 * <li>MODULE：模块大写短名（如 COMMON、USER、ORDER 等）</li>
 * <li>NNNN：四位数字编码，在模块内维护分配表</li>
 * </ul>
 * <p>
 * 时间戳：使用 epoch 毫秒（System.currentTimeMillis），便于与日志系统对齐。
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    // ============================== 属性定义 ===================================

    /**
     * 所属模块标识（如：COMMON、USER、ORDER）
     */
    private final String module;

    /**
     * 标准化错误码（格式：MODULE-NNNN）
     */
    private final String errorCode;

    /**
     * 错误信息（详细描述）
     */
    private final String errorMessage;

    /**
     * 异常发生时间戳（epoch 毫秒）
     */
    private final long timestamp;

    // ============================== 构造方法 ===================================

    /**
     * 构造方法
     *
     * @param module 异常来源模块
     * @param errorCode 标准化错误码
     * @param errorMessage 错误信息
     */
    protected BaseException(String module, String errorCode, String errorMessage) {
        super(errorMessage);
        this.module = module;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 构造方法（带原始异常）
     *
     * @param module 异常来源模块
     * @param errorCode 标准化错误码
     * @param errorMessage 错误信息
     * @param cause 原始异常（可为 null）
     */
    protected BaseException(String module, String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.module = module;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 构造方法（自定义时间戳）
     *
     * @param module 异常来源模块
     * @param errorCode 标准化错误码
     * @param errorMessage 错误信息
     * @param timestamp 异常发生时间（epoch 毫秒）
     */
    protected BaseException(String module, String errorCode, String errorMessage, long timestamp) {
        super(errorMessage);
        this.module = module;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }

    /**
     * 构造方法（自定义时间戳 + 原始异常）
     *
     * @param module 异常来源模块
     * @param errorCode 标准化错误码
     * @param errorMessage 错误信息
     * @param timestamp 异常发生时间（epoch 毫秒）
     * @param cause 原始异常（可为 null）
     */
    protected BaseException(String module, String errorCode, String errorMessage, long timestamp,
            Throwable cause) {
        super(errorMessage, cause);
        this.module = module;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }

    // ============================== 方法定义 ===================================

    /**
     * 返回异常消息（与 {@link #errorMessage} 一致），保持与父类行为一致性。
     *
     * @return 错误信息
     */
    @Override
    public String getMessage() {
        return errorMessage;
    }
}
