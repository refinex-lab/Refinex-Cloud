package cn.refinex.common.domain;

import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.exception.BaseException;
import cn.refinex.common.exception.code.ErrorCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用接口返回对象（不可变、线程安全）
 *
 * @param <T>       载荷数据类型
 * @param code      状态码
 * @param message   用户友好的错误信息（中文）
 * @param data      载荷数据
 * @param timestamp 服务器时间戳（epoch 毫秒）
 * @author Refinex
 * @since 1.0.0
 */
public record ApiResult<T>(int code, String message, T data, long timestamp) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ============================== 静态工厂方法 ===================================

    /**
     * 成功返回（默认消息：操作成功）
     *
     * @param data 载荷数据
     * @param <T>  载荷数据类型
     * @return 成功返回对象
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(HttpStatusCode.OK.getCode(), HttpStatusCode.OK.getMessage(), data, System.currentTimeMillis());
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
        return new ApiResult<>(HttpStatusCode.OK.getCode(), message, data, System.currentTimeMillis());
    }

    /**
     * 成功返回（使用指定的 HTTP 状态码）
     *
     * @param statusCode HTTP 状态码枚举
     * @param data       载荷数据
     * @param <T>        载荷数据类型
     * @return 成功返回对象
     */
    public static <T> ApiResult<T> success(HttpStatusCode statusCode, T data) {
        return new ApiResult<>(statusCode.getCode(), statusCode.getMessage(), data, System.currentTimeMillis());
    }

    /**
     * 成功返回（使用指定的 HTTP 状态码和自定义消息）
     *
     * @param statusCode HTTP 状态码枚举
     * @param message    自定义成功消息
     * @param data       载荷数据
     * @param <T>        载荷数据类型
     * @return 成功返回对象
     */
    public static <T> ApiResult<T> success(HttpStatusCode statusCode, String message, T data) {
        return new ApiResult<>(statusCode.getCode(), message, data, System.currentTimeMillis());
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
     * 失败返回（使用 HTTP 状态码枚举）
     *
     * @param statusCode HTTP 状态码枚举
     * @param <T>        载荷数据类型
     * @return 失败返回对象
     */
    public static <T> ApiResult<T> failure(HttpStatusCode statusCode) {
        return new ApiResult<>(statusCode.getCode(), statusCode.getMessage(), null, System.currentTimeMillis());
    }

    /**
     * 失败返回（使用 HTTP 状态码枚举和自定义消息）
     *
     * @param statusCode HTTP 状态码枚举
     * @param message    自定义错误消息
     * @param <T>        载荷数据类型
     * @return 失败返回对象
     */
    public static <T> ApiResult<T> failure(HttpStatusCode statusCode, String message) {
        return new ApiResult<>(statusCode.getCode(), message, null, System.currentTimeMillis());
    }

    /**
     * 失败返回（依据统一错误码接口）
     *
     * @param errorCode 统一错误码接口实现
     * @param <T>       载荷数据类型
     * @return 失败返回对象
     */
    public static <T> ApiResult<T> failure(ErrorCode errorCode) {
        int code = (errorCode != null) ? errorCode.getCode() : HttpStatusCode.INTERNAL_SERVER_ERROR.getCode();
        String message = (errorCode != null) ? errorCode.getMessage() : HttpStatusCode.INTERNAL_SERVER_ERROR.getMessage();
        return new ApiResult<>(code, message, null, System.currentTimeMillis());
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
            return failure(HttpStatusCode.INTERNAL_SERVER_ERROR);
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
     * 复制当前对象，替换状态码（使用 HTTP 状态码枚举）
     *
     * @param statusCode HTTP 状态码枚举
     * @return 新的 ApiResult 对象
     */
    public ApiResult<T> withCode(HttpStatusCode statusCode) {
        return new ApiResult<>(statusCode.getCode(), this.message, this.data, this.timestamp);
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
        return HttpStatusCode.OK.getCode() == this.code
                || HttpStatusCode.CREATED.getCode() == this.code
                || HttpStatusCode.NO_CONTENT.getCode() == this.code
                || HttpStatusCode.ACCEPTED.getCode() == this.code;
    }

    /**
     * 是否为 2xx 成功状态码
     *
     * @return true 如果状态码在 200-299 范围内
     */
    public boolean is2xxSuccess() {
        return this.code >= 200 && this.code < 300;
    }

    /**
     * 是否为客户端错误（4xx）
     *
     * @return true 如果状态码在 400-499 范围内
     */
    public boolean isClientError() {
        return this.code >= 400 && this.code < 500;
    }

    /**
     * 是否为服务器错误（5xx）
     *
     * @return true 如果状态码在 500-599 范围内
     */
    public boolean isServerError() {
        return this.code >= 500 && this.code < 600;
    }

    /**
     * 是否为错误状态码（4xx 或 5xx）
     *
     * @return true 如果状态码在 400-599 范围内
     */
    public boolean isError() {
        return isClientError() || isServerError();
    }
}