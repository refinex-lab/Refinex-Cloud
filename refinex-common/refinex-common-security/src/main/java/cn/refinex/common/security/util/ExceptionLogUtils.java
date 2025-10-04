package cn.refinex.common.security.util;

import cn.refinex.common.security.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 异常日志工具类
 * <p>
 * 记录安全异常的详细信息，包括请求上下文和用户信息
 * </p>
 *
 * @author Refinex
 * @since 2025-10-04
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionLogUtils {

    /**
     * 记录安全异常日志
     *
     * @param exception 异常对象
     * @param request   请求对象
     */
    public static void logSecurityException(Exception exception, HttpServletRequest request) {
        if (request == null) {
            log.warn("安全异常: {}", exception.getMessage());
            return;
        }

        // 获取请求信息
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = getClientIp(request);

        // 获取用户信息
        Long userId = null;
        try {
            userId = SecurityUtils.getCurrentUserId();
        } catch (Exception e) {
            // 忽略，用户未登录
        }

        // 记录日志
        log.warn("安全异常 - 方法: {}, URI: {}, IP: {}, 用户ID: {}, 异常: {}",
                method, uri, ip, userId, exception.getMessage());
    }

    /**
     * 记录详细的安全异常日志（包含堆栈信息）
     *
     * @param exception 异常对象
     * @param request   请求对象
     */
    public static void logSecurityExceptionWithStackTrace(Exception exception, HttpServletRequest request) {
        if (request == null) {
            log.error("安全异常", exception);
            return;
        }

        // 获取请求信息
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = getClientIp(request);

        // 获取用户信息
        Long userId = null;
        try {
            userId = SecurityUtils.getCurrentUserId();
        } catch (Exception e) {
            // 忽略，用户未登录
        }

        // 记录日志（包含堆栈信息）
        log.error("安全异常 - 方法: {}, URI: {}, IP: {}, 用户ID: {}",
                method, uri, ip, userId, exception);
    }

    /**
     * 获取客户端 IP 地址
     *
     * @param request 请求对象
     * @return 客户端 IP
     */
    private static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 处理多个 IP 的情况（取第一个）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
}

