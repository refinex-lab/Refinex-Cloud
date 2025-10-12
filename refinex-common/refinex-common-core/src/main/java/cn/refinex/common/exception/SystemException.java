package cn.refinex.common.exception;

import cn.refinex.common.exception.code.ResultCode;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 系统异常类
 *
 * @author Refinex
 * @since 1.0.0
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SystemException extends BaseException {

    // ============================== 构造方法 ===================================

    /**
     * 构造方法（默认错误码：500）
     *
     * @param errorMessage 错误信息
     */
    public SystemException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * 构造方法（带原始异常）
     *
     * @param errorMessage 错误信息
     * @param cause        原始异常（可为 null）
     */
    public SystemException(String errorMessage, Throwable cause) {
        super(ResultCode.INTERNAL_ERROR.getCode(), errorMessage, cause);
    }

    /**
     * 构造方法
     *
     * @param errorCode    标准化错误码
     * @param errorMessage 错误信息
     */
    public SystemException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * 构造方法（带原始异常）
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
     *
     * @param errorCode    标准化错误码
     * @param errorMessage 错误信息
     * @param timestamp    异常发生时间（epoch 毫秒）
     * @param cause        原始异常（可为 null）
     */
    public SystemException(int errorCode, String errorMessage, long timestamp, Throwable cause) {
        super(errorCode, errorMessage, timestamp, cause);
    }

    /**
     * 构造方法（使用 ResultCode 枚举）
     *
     * @param resultCode 枚举类型的错误码（包含错误码和错误信息）
     */
    public SystemException(ResultCode resultCode) {
        super(resultCode.getCode(), resultCode.getMessage());
    }
}