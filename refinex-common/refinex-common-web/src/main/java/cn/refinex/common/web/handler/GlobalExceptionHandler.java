package cn.refinex.common.web.handler;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.exception.SystemException;
import cn.refinex.common.utils.servlet.ServletUtils;
import com.fasterxml.jackson.core.JsonParseException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResult<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.warn("请求地址 {} 发生业务异常: {}", requestUri, e.getErrorMessage());
        return ApiResult.fromException(e);
    }

    /**
     * 处理系统异常
     */
    @ExceptionHandler(SystemException.class)
    public ApiResult<Void> handleSystemException(SystemException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 发生系统异常: {}", requestUri, e.getErrorMessage(), e);
        return ApiResult.fromException(e);
    }

    /**
     * 处理 HTTP 请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResult<Void> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 不支持 {} 方法", requestUri, e.getMethod());
        return ApiResult.failure(HttpStatusCode.METHOD_NOT_ALLOWED, "不支持 " + e.getMethod() + " 请求方法");
    }

    /**
     * 处理 Servlet 异常
     */
    @ExceptionHandler(ServletException.class)
    public ApiResult<Void> handleServletException(ServletException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 发生 Servlet 异常", requestUri, e);
        return ApiResult.failure(HttpStatusCode.INTERNAL_SERVER_ERROR, "服务器处理请求时发生错误");
    }

    /**
     * 处理路径变量缺失异常
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public ApiResult<Void> handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 缺失路径变量 {}", requestUri, e.getVariableName());
        return ApiResult.failure(HttpStatusCode.BAD_REQUEST, "缺失路径变量: " + e.getVariableName());
    }

    /**
     * 处理方法参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResult<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 路径变量 {} 类型错误", requestUri, e.getName());
        return ApiResult.failure(HttpStatusCode.BAD_REQUEST, "参数类型错误: " + e.getName());
    }

    /**
     * 处理 404 异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ApiResult<Void> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 未找到对应的 Handler", requestUri);
        return ApiResult.failure(HttpStatusCode.NOT_FOUND, "请求的资源不存在");
    }

    /**
     * 处理 IO 异常
     */
    @ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IOException.class)
    public ApiResult<Void> handleIOException(IOException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 发生 IO 异常", requestUri, e);
        return ApiResult.failure(HttpStatusCode.INTERNAL_SERVER_ERROR, "文件处理失败");
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ApiResult<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 发生运行时异常", requestUri, e);
        return ApiResult.failure(HttpStatusCode.INTERNAL_SERVER_ERROR, "系统运行时错误，请稍后重试");
    }

    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResult<Void> handleException(Exception e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 发生未知异常", requestUri, e);
        return ApiResult.failure(HttpStatusCode.INTERNAL_SERVER_ERROR, "系统错误，请稍后重试");
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ApiResult<Void> handleBindException(BindException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("请求地址 {} 参数绑定异常: {}", requestUri, errorMessage);
        return ApiResult.failure(HttpStatusCode.BAD_REQUEST, errorMessage);
    }

    /**
     * 处理约束校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResult<Void> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.error("请求地址 {} 约束校验异常: {}", requestUri, errorMessage);
        return ApiResult.failure(HttpStatusCode.BAD_REQUEST, errorMessage);
    }

    /**
     * 处理方法参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("请求地址 {} 方法参数校验异常: {}", requestUri, errorMessage);
        return ApiResult.failure(HttpStatusCode.BAD_REQUEST, errorMessage);
    }

    /**
     * 处理 JSON 解析异常
     */
    @ExceptionHandler(JsonParseException.class)
    public ApiResult<Void> handleJsonParseException(JsonParseException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} JSON 解析异常", requestUri, e);
        return ApiResult.failure(HttpStatusCode.BAD_REQUEST, "JSON 格式错误，请检查请求体");
    }

    /**
     * 处理 HTTP 消息不可读异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResult<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 参数解析异常", requestUri, e);
        return ApiResult.failure(HttpStatusCode.BAD_REQUEST, "请求参数格式错误，请检查请求体");
    }
}
