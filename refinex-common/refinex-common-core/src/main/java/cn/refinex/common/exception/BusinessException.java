package cn.refinex.common.exception;

import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.exception.code.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 业务异常类
 * <p>
 * 用于业务逻辑验证失败等场景。推荐使用 {@link HttpStatusCode#UNPROCESSABLE_ENTITY} (422) 
 * 或其他合适的 4xx 状态码来表示业务错误。
 *
 * @author Refinex
 * @since 1.0.0
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends BaseException {

    // ============================== 构造方法 ===================================

    /**
     * 构造方法（仅错误信息，默认使用 500 状态码）
     *
     * @param errorMessage 错误信息
     */
    public BusinessException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * 构造方法（带原始异常，默认使用 500 状态码）
     *
     * @param errorMessage 错误信息
     * @param cause        原始异常（可为 null）
     */
    public BusinessException(String errorMessage, Throwable cause) {
        super(HttpStatusCode.INTERNAL_SERVER_ERROR, errorMessage, cause);
    }

    /**
     * 构造方法（使用 HttpStatusCode 枚举）
     *
     * @param statusCode HTTP 状态码枚举
     */
    public BusinessException(HttpStatusCode statusCode) {
        super(statusCode);
    }

    /**
     * 构造方法（使用 HttpStatusCode 枚举和自定义消息）
     *
     * @param statusCode   HTTP 状态码枚举
     * @param errorMessage 自定义错误信息
     */
    public BusinessException(HttpStatusCode statusCode, String errorMessage) {
        super(statusCode, errorMessage);
    }

    /**
     * 构造方法（使用 HttpStatusCode 枚举、自定义消息和原始异常）
     *
     * @param statusCode   HTTP 状态码枚举
     * @param errorMessage 自定义错误信息
     * @param cause        原始异常
     */
    public BusinessException(HttpStatusCode statusCode, String errorMessage, Throwable cause) {
        super(statusCode, errorMessage, cause);
    }

    /**
     * 构造方法（使用 ErrorCode 接口，兼容业务错误码）
     *
     * @param errorCode 错误码接口实现
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 构造方法（使用 ErrorCode 接口和自定义消息）
     *
     * @param errorCode    错误码接口实现
     * @param errorMessage 自定义错误信息
     */
    public BusinessException(ErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * 构造方法（使用 ErrorCode 接口、自定义消息和原始异常）
     *
     * @param errorCode    错误码接口实现
     * @param errorMessage 自定义错误信息
     * @param cause        原始异常
     */
    public BusinessException(ErrorCode errorCode, String errorMessage, Throwable cause) {
        super(errorCode, errorMessage, cause);
    }

    /**
     * 构造方法（直接使用状态码数值）
     * <p>
     * 注意：推荐使用 {@link HttpStatusCode} 枚举替代直接传入 int 值，以确保状态码的规范性。
     *
     * @param errorCode    标准化错误码
     * @param errorMessage 错误信息
     */
    public BusinessException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * 构造方法（直接使用状态码数值，带原始异常）
     * <p>
     * 注意：推荐使用 {@link HttpStatusCode} 枚举替代直接传入 int 值，以确保状态码的规范性。
     *
     * @param errorCode    标准化错误码
     * @param errorMessage 错误信息
     * @param cause        原始异常（可为 null）
     */
    public BusinessException(int errorCode, String errorMessage, Throwable cause) {
        super(errorCode, errorMessage, cause);
    }

    /**
     * 构造方法（自定义时间戳）
     * <p>
     * 注意：推荐使用 {@link HttpStatusCode} 枚举替代直接传入 int 值，以确保状态码的规范性。
     *
     * @param errorCode    标准化错误码
     * @param errorMessage 错误信息
     * @param timestamp    异常发生时间（epoch 毫秒）
     */
    public BusinessException(int errorCode, String errorMessage, long timestamp) {
        super(errorCode, errorMessage, timestamp);
    }

    /**
     * 构造方法（自定义时间戳 + 原始异常）
     * <p>
     * 注意：推荐使用 {@link HttpStatusCode} 枚举替代直接传入 int 值，以确保状态码的规范性。
     *
     * @param errorCode    标准化错误码
     * @param errorMessage 错误信息
     * @param timestamp    异常发生时间（epoch 毫秒）
     * @param cause        原始异常（可为 null）
     */
    public BusinessException(int errorCode, String errorMessage, long timestamp, Throwable cause) {
        super(errorCode, errorMessage, timestamp, cause);
    }

    // ============================== 性能优化 ===================================
    /**
     * 关闭堆栈填充以优化性能（适用于高频业务异常场景）。
     * 如需定位复杂问题，可临时改为调用父类默认行为。
     *
     * @return 当前异常实例本身
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}