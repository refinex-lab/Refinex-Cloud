package cn.refinex.common.exception;

import cn.refinex.common.exception.code.ResultCode;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 业务异常类
 *
 * @author Refinex
 * @since 1.0.0
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends BaseException {

    // ============================== 构造方法 ===================================

    /**
     * 构造方法（仅错误信息）
     *
     * @param errorMessage 错误信息
     */
    public BusinessException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * 构造方法（带原始异常）
     *
     * @param errorMessage 错误信息
     * @param cause        原始异常（可为 null）
     */
    public BusinessException(String errorMessage, Throwable cause) {
        super(ResultCode.INTERNAL_ERROR.getCode(), errorMessage, cause);
    }

    /**
     * 构造方法
     *
     * @param errorCode    标准化错误码
     * @param errorMessage 错误信息
     */
    public BusinessException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * 构造方法（带原始异常）
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