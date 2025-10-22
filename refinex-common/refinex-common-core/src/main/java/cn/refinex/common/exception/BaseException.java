package cn.refinex.common.exception;

import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.exception.code.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;

/**
 * 基础异常类
 * <p>
 * 使用标准 HTTP 状态码 ({@link HttpStatusCode}) 作为默认错误码体系。
 * 时间戳：使用 epoch 毫秒（System.currentTimeMillis），便于与日志系统对齐。
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    // ============================== 属性定义 ===================================

    /**
     * 标准化错误码
     */
    private final int errorCode;

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
     * 构造方法（默认错误码：500 服务器内部错误）
     *
     * @param errorMessage 错误信息
     */
    protected BaseException(String errorMessage) {
        super(errorMessage);
        this.errorCode = HttpStatusCode.INTERNAL_SERVER_ERROR.getCode();
        this.errorMessage = errorMessage;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 构造方法（使用 HttpStatusCode 枚举）
     *
     * @param statusCode HTTP 状态码枚举
     */
    protected BaseException(HttpStatusCode statusCode) {
        super(statusCode.getMessage());
        this.errorCode = statusCode.getCode();
        this.errorMessage = statusCode.getMessage();
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 构造方法（使用 HttpStatusCode 枚举和自定义消息）
     *
     * @param statusCode   HTTP 状态码枚举
     * @param errorMessage 自定义错误信息
     */
    protected BaseException(HttpStatusCode statusCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = statusCode.getCode();
        this.errorMessage = errorMessage;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 构造方法（使用 HttpStatusCode 枚举、自定义消息和原始异常）
     *
     * @param statusCode   HTTP 状态码枚举
     * @param errorMessage 自定义错误信息
     * @param cause        原始异常
     */
    protected BaseException(HttpStatusCode statusCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = statusCode.getCode();
        this.errorMessage = errorMessage;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 构造方法（使用 ErrorCode 接口，兼容业务错误码）
     *
     * @param errorCode 错误码接口实现
     */
    protected BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorCode.getMessage();
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 构造方法（使用 ErrorCode 接口和自定义消息）
     *
     * @param errorCode    错误码接口实现
     * @param errorMessage 自定义错误信息
     */
    protected BaseException(ErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorMessage;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 构造方法（使用 ErrorCode 接口、自定义消息和原始异常）
     *
     * @param errorCode    错误码接口实现
     * @param errorMessage 自定义错误信息
     * @param cause        原始异常
     */
    protected BaseException(ErrorCode errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorMessage;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 构造方法（直接使用状态码数值）
     * <p>
     * 注意：推荐使用 {@link HttpStatusCode} 枚举替代直接传入 int 值，以确保状态码的规范性。
     *
     * @param errorCode    标准化错误码
     * @param errorMessage 错误信息
     */
    protected BaseException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = System.currentTimeMillis();
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
    protected BaseException(int errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = System.currentTimeMillis();
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
    protected BaseException(int errorCode, String errorMessage, long timestamp) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
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
    protected BaseException(int errorCode, String errorMessage, long timestamp, Throwable cause) {
        super(errorMessage, cause);
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
