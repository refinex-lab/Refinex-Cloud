package cn.refinex.common.web.handler;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.exception.SystemException;
import cn.refinex.common.exception.code.ResultCode;
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

    @ExceptionHandler(BusinessException.class)
    public ApiResult<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 发生业务异常", requestUri, e);
        return ApiResult.fromException(e);
    }

    @ExceptionHandler(SystemException.class)
    public ApiResult<Void> handleSystemException(SystemException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 发生系统异常", requestUri, e);
        return ApiResult.fromException(e);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResult<Void> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 不支持 {} 方法", requestUri, e.getMethod(), e);
        return ApiResult.failure(ResultCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(ServletException.class)
    public ApiResult<Void> handleServletException(ServletException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 发生 Servlet 异常", requestUri, e);
        return ApiResult.failure(ResultCode.INTERNAL_ERROR);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ApiResult<Void> handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 缺失路径变量 {}", requestUri, e.getVariableName(), e);
        return ApiResult.failure(ResultCode.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResult<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 路径变量 {} 类型错误", requestUri, e.getName(), e);
        return ApiResult.failure(ResultCode.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ApiResult<Void> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 未找到对应的 Handler", requestUri, e);
        return ApiResult.failure(ResultCode.NOT_FOUND);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IOException.class)
    public ApiResult<Void> handleIOException(IOException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 发生 IO 异常", requestUri, e);
        return ApiResult.failure(ResultCode.INTERNAL_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResult<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 发生运行时异常", requestUri, e);
        return ApiResult.failure(ResultCode.INTERNAL_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<Void> handleException(Exception e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 发生异常", requestUri, e);
        return ApiResult.failure(ResultCode.INTERNAL_ERROR);
    }

    @ExceptionHandler(BindException.class)
    public ApiResult<Void> handleBindException(BindException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 绑定异常", requestUri, e);
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ApiResult.failure(ResultCode.BAD_REQUEST.getCode(), errorMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResult<Void> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 约束校验异常", requestUri, e);
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        return ApiResult.failure(ResultCode.BAD_REQUEST.getCode(), errorMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 方法参数校验异常", requestUri, e);
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ApiResult.failure(ResultCode.BAD_REQUEST.getCode(), errorMessage);
    }

    @ExceptionHandler(JsonParseException.class)
    public ApiResult<Void> handleJsonParseException(JsonParseException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} JSON 解析异常", requestUri, e);
        return ApiResult.failure(ResultCode.BAD_REQUEST.getCode(), e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResult<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        log.error("请求地址 {} 参数解析异常", requestUri, e);
        return ApiResult.failure(ResultCode.BAD_REQUEST.getCode(), e.getMessage());
    }
}
