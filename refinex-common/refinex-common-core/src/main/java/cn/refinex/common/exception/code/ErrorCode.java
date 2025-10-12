package cn.refinex.common.exception.code;

import java.io.Serializable;

/**
 * 统一错误码抽象
 * <p>
 * 既支持基础通用状态码（如 "200"、"400"、"404"、"401"、"500"），也支持业务模块码（如 "COMMON-1001"）。
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface ErrorCode extends Serializable {

    /**
     * 返回标准化错误码字符串
     */
    int getCode();

    /**
     * 返回中文提示信息
     */
    String getMessage();
}