package cn.refinex.common.exception;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 业务异常类
 * <p>
 * 用于表示业务逻辑层的错误，例如参数校验失败、状态不合法、资源不可用等。
 * 该异常在高频场景下默认关闭堆栈填充，以优化性能（可通过覆盖策略调整）。
 *
 * @author Refinex
 * @since 1.0.0
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends BaseException {

    // ============================== 构造方法 ===================================
    /**
     * 构造方法
     *
     * @param module       异常来源模块
     * @param errorCode    标准化错误码（建议区间：1000-1999）
     * @param errorMessage 错误信息
     */
    public BusinessException(String module, String errorCode, String errorMessage) {
        super(module, errorCode, errorMessage);
    }

    /**
     * 构造方法（带原始异常）
     *
     * @param module       异常来源模块
     * @param errorCode    标准化错误码（建议区间：1000-1999）
     * @param errorMessage 错误信息
     * @param cause        原始异常（可为 null）
     */
    public BusinessException(String module, String errorCode, String errorMessage, Throwable cause) {
        super(module, errorCode, errorMessage, cause);
    }

    /**
     * 构造方法（自定义时间戳）
     *
     * @param module       异常来源模块
     * @param errorCode    标准化错误码（建议区间：1000-1999）
     * @param errorMessage 错误信息
     * @param timestamp    异常发生时间（epoch 毫秒）
     */
    public BusinessException(String module, String errorCode, String errorMessage, long timestamp) {
        super(module, errorCode, errorMessage, timestamp);
    }

    /**
     * 构造方法（自定义时间戳 + 原始异常）
     *
     * @param module       异常来源模块
     * @param errorCode    标准化错误码（建议区间：1000-1999）
     * @param errorMessage 错误信息
     * @param timestamp    异常发生时间（epoch 毫秒）
     * @param cause        原始异常（可为 null）
     */
    public BusinessException(String module, String errorCode, String errorMessage, long timestamp, Throwable cause) {
        super(module, errorCode, errorMessage, timestamp, cause);
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