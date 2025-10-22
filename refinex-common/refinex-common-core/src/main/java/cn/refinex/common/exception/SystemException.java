package cn.refinex.common.exception;

import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.exception.code.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 系统异常类
 * <p>
 * 用于系统级别的错误，如数据库连接失败、文件 IO 异常等。
 * 默认使用 {@link HttpStatusCode#INTERNAL_SERVER_ERROR} (500) 状态码。
 *
 * @author Refinex
 * @since 1.0.0
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SystemException extends BaseException {

    // ============================== 构造方法 ===================================

    /**
     * 构造方法（默认错误码：500 服务器内部错误）
     *
     * @param errorMessage 错误信息
     */
    public SystemException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * 构造方法（带原始异常，默认使用 500 状态码）
     *
     * @param errorMessage 错误信息
     * @param cause        原始异常（可为 null）
     */
    public SystemException(String errorMessage, Throwable cause) {
        super(HttpStatusCode.INTERNAL_SERVER_ERROR, errorMessage, cause);
    }

    /**
     * 构造方法（使用 HttpStatusCode 枚举）
     *
     * @param statusCode HTTP 状态码枚举
     */
    public SystemException(HttpStatusCode statusCode) {
        super(statusCode);
    }

    /**
     * 构造方法（使用 HttpStatusCode 枚举和自定义消息）
     *
     * @param statusCode   HTTP 状态码枚举
     * @param errorMessage 自定义错误信息
     */
    public SystemException(HttpStatusCode statusCode, String errorMessage) {
        super(statusCode, errorMessage);
    }

    /**
     * 构造方法（使用 HttpStatusCode 枚举、自定义消息和原始异常）
     *
     * @param statusCode   HTTP 状态码枚举
     * @param errorMessage 自定义错误信息
     * @param cause        原始异常
     */
    public SystemException(HttpStatusCode statusCode, String errorMessage, Throwable cause) {
        super(statusCode, errorMessage, cause);
    }

    /**
     * 构造方法（使用 ErrorCode 接口，兼容业务错误码）
     *
     * @param errorCode 错误码接口实现
     */
    public SystemException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 构造方法（使用 ErrorCode 接口和自定义消息）
     *
     * @param errorCode    错误码接口实现
     * @param errorMessage 自定义错误信息
     */
    public SystemException(ErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * 构造方法（使用 ErrorCode 接口、自定义消息和原始异常）
     *
     * @param errorCode    错误码接口实现
     * @param errorMessage 自定义错误信息
     * @param cause        原始异常
     */
    public SystemException(ErrorCode errorCode, String errorMessage, Throwable cause) {
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
    public SystemException(int errorCode, String errorMessage) {
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
    public SystemException(int errorCode, String errorMessage, Throwable cause) {
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
    public SystemException(int errorCode, String errorMessage, long timestamp) {
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
    public SystemException(int errorCode, String errorMessage, long timestamp, Throwable cause) {
        super(errorCode, errorMessage, timestamp, cause);
    }
}