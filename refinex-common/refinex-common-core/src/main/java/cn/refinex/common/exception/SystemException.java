package cn.refinex.common.exception;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 系统异常类
 * <p>
 * 用于表示系统级错误，例如外部依赖不可用、资源耗尽、网络故障等。
 * 默认保留完整堆栈信息，以便问题定位与排障。
 *
 * @author Refinex
 * @since 1.0.0
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SystemException extends BaseException {

    // ============================== 构造方法 ===================================
    /**
     * 构造方法
     *
     * @param module       异常来源模块
     * @param errorCode    标准化错误码（建议区间：2000-2999）
     * @param errorMessage 错误信息
     */
    public SystemException(String module, String errorCode, String errorMessage) {
        super(module, errorCode, errorMessage);
    }

    /**
     * 构造方法（带原始异常）
     *
     * @param module       异常来源模块
     * @param errorCode    标准化错误码（建议区间：2000-2999）
     * @param errorMessage 错误信息
     * @param cause        原始异常（可为 null）
     */
    public SystemException(String module, String errorCode, String errorMessage, Throwable cause) {
        super(module, errorCode, errorMessage, cause);
    }

    /**
     * 构造方法（自定义时间戳）
     *
     * @param module       异常来源模块
     * @param errorCode    标准化错误码（建议区间：2000-2999）
     * @param errorMessage 错误信息
     * @param timestamp    异常发生时间（epoch 毫秒）
     */
    public SystemException(String module, String errorCode, String errorMessage, long timestamp) {
        super(module, errorCode, errorMessage, timestamp);
    }

    /**
     * 构造方法（自定义时间戳 + 原始异常）
     *
     * @param module       异常来源模块
     * @param errorCode    标准化错误码（建议区间：2000-2999）
     * @param errorMessage 错误信息
     * @param timestamp    异常发生时间（epoch 毫秒）
     * @param cause        原始异常（可为 null）
     */
    public SystemException(String module, String errorCode, String errorMessage, long timestamp, Throwable cause) {
        super(module, errorCode, errorMessage, timestamp, cause);
    }
}