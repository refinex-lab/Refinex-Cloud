package cn.refinex.common.domain;

import cn.refinex.common.exception.BaseException;
import cn.refinex.common.exception.code.ErrorCode;
import cn.refinex.common.exception.code.ResultCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用接口返回对象（不可变、线程安全）
 *
 * @param <T> 载荷数据类型
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private final int code;

    /**
     * 用户友好的错误信息（中文）
     */
    private final String message;

    /**
     * 载荷数据
     */
    private final T data;

    /**
     * 服务器时间戳（epoch 毫秒）
     */
    private final long timestamp;

    // ============================== 静态工厂方法 ===================================

    /**
     * 成功返回（默认消息：操作成功）
     *
     * @param data 载荷数据
     * @param <T>  载荷数据类型
     * @return 成功返回对象
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data, System.currentTimeMillis());
    }

    /**
     * 成功返回（自定义成功消息）
     *
     * @param message 成功消息
     * @param data    载荷数据
     * @param <T>     载荷数据类型
     * @return 成功返回对象
     */
    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(ResultCode.SUCCESS.getCode(), message, data, System.currentTimeMillis());
    }

    /**
     * 失败返回（指定码与消息，data 为 null）
     *
     * @param code    状态码
     * @param message 用户友好的错误信息（中文）
     * @param <T>     载荷数据类型
     * @return 失败返回对象
     */
    public static <T> ApiResult<T> failure(int code, String message) {
        return new ApiResult<>(code, message, null, System.currentTimeMillis());
    }

    /**
     * 失败返回（依据统一错误码接口）
     *
     * @param errorCode 统一错误码接口实现
     * @param <T>       载荷数据类型
     * @return 失败返回对象
     */
    public static <T> ApiResult<T> failure(ErrorCode errorCode) {
        int c = (errorCode != null) ? errorCode.getCode() : ResultCode.INTERNAL_ERROR.getCode();
        String m = (errorCode != null) ? errorCode.getMessage() : ResultCode.INTERNAL_ERROR.getMessage();
        return new ApiResult<>(c, m, null, System.currentTimeMillis());
    }

    /**
     * 从基础异常转换为标准返回（保留异常时间戳与消息）
     *
     * @param ex  基础异常对象
     * @param <T> 载荷数据类型
     * @return 失败返回对象
     */
    public static <T> ApiResult<T> fromException(BaseException ex) {
        if (ex == null) {
            return failure(ResultCode.INTERNAL_ERROR.getCode(), ResultCode.INTERNAL_ERROR.getMessage());
        }
        return new ApiResult<>(ex.getErrorCode(), ex.getErrorMessage(), null, ex.getTimestamp());
    }

    // ============================== 链式复制方法（保持不可变） ========================

    /**
     * 复制当前对象，替换状态码
     *
     * @param code 新状态码
     * @return 新的 ApiResult 对象
     */
    public ApiResult<T> withCode(int code) {
        return new ApiResult<>(code, this.message, this.data, this.timestamp);
    }

    /**
     * 复制当前对象，替换错误消息
     *
     * @param message 新错误消息
     * @return 新的 ApiResult 对象
     */
    public ApiResult<T> withMessage(String message) {
        return new ApiResult<>(this.code, message, this.data, this.timestamp);
    }

    /**
     * 复制当前对象，替换载荷数据
     *
     * @param data 新载荷数据
     * @return 新的 ApiResult 对象
     */
    public ApiResult<T> withData(T data) {
        return new ApiResult<>(this.code, this.message, data, this.timestamp);
    }

    /**
     * 复制当前对象，替换服务器时间戳
     *
     * @param timestamp 新服务器时间戳（epoch 毫秒）
     * @return 新的 ApiResult 对象
     */
    public ApiResult<T> withTimestamp(long timestamp) {
        return new ApiResult<>(this.code, this.message, this.data, timestamp);
    }

    // ============================== 判断方法 ======================================

    /**
     * 是否成功（状态码为 200）
     *
     * @return true 如果状态码为 200，否则 false
     */
    public boolean isSuccess() {
        return ResultCode.SUCCESS.getCode() == this.code;
    }
}