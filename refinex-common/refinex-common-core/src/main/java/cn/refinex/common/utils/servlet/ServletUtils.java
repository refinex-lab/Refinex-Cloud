package cn.refinex.common.utils.servlet;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Servlet 工具类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServletUtils {

    /**
     * 默认字符编码
     */
    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * JSON响应内容类型
     */
    private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    /**
     * 文本响应内容类型
     */
    private static final String CONTENT_TYPE_TEXT = "text/plain;charset=UTF-8";

    /**
     * HTML响应内容类型
     */
    private static final String CONTENT_TYPE_HTML = "text/html;charset=UTF-8";

    /**
     * XML响应内容类型
     */
    private static final String CONTENT_TYPE_XML = "application/xml;charset=UTF-8";

    /**
     * 文件下载内容类型
     */
    private static final String CONTENT_TYPE_DOWNLOAD = "application/octet-stream";

    /**
     * 未知IP标识
     */
    private static final String UNKNOWN = "unknown";

    /**
     * 本地IP地址
     */
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    /**
     * IP地址最大长度
     */
    private static final int IP_MAX_LENGTH = 15;

    /**
     * 代理IP分隔符
     */
    private static final String IP_SEPARATOR = ",";

    // ==================== 请求参数获取 ====================

    /**
     * 获取请求参数（String类型）
     * <p>
     * 如果参数不存在或为空，返回null。
     * </p>
     *
     * @param request   HttpServletRequest对象
     * @param paramName 参数名称
     * @return 参数值
     * @throws IllegalArgumentException 如果request或参数名为null
     */
    public static String getParameter(HttpServletRequest request, String paramName) {
        validateRequest(request);
        validateNotBlank(paramName, "参数名称不能为空");
        String value = request.getParameter(paramName);
        return StrUtil.isBlank(value) ? null : value.trim();
    }

    /**
     * 获取请求参数（带默认值）
     *
     * @param request      HttpServletRequest对象
     * @param paramName    参数名称
     * @param defaultValue 默认值
     * @return 参数值，不存在时返回默认值
     * @throws IllegalArgumentException 如果request或参数名为null
     */
    public static String getParameter(HttpServletRequest request, String paramName, String defaultValue) {
        String value = getParameter(request, paramName);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取请求参数（Integer类型）
     *
     * @param request   HttpServletRequest对象
     * @param paramName 参数名称
     * @return 参数值，解析失败返回null
     * @throws IllegalArgumentException 如果request或参数名为null
     */
    public static Integer getParameterAsInt(HttpServletRequest request, String paramName) {
        String value = getParameter(request, paramName);
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("参数{}转换为Integer失败: {}", paramName, value);
            return null;
        }
    }

    /**
     * 获取请求参数（Integer类型，带默认值）
     *
     * @param request      HttpServletRequest对象
     * @param paramName    参数名称
     * @param defaultValue 默认值
     * @return 参数值，解析失败返回默认值
     * @throws IllegalArgumentException 如果request或参数名为null
     */
    public static Integer getParameterAsInt(HttpServletRequest request, String paramName, Integer defaultValue) {
        Integer value = getParameterAsInt(request, paramName);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取请求参数（Long类型）
     *
     * @param request   HttpServletRequest对象
     * @param paramName 参数名称
     * @return 参数值，解析失败返回null
     * @throws IllegalArgumentException 如果request或参数名为null
     */
    public static Long getParameterAsLong(HttpServletRequest request, String paramName) {
        String value = getParameter(request, paramName);
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            log.warn("参数{}转换为Long失败: {}", paramName, value);
            return null;
        }
    }

    /**
     * 获取请求参数（Long类型，带默认值）
     *
     * @param request      HttpServletRequest对象
     * @param paramName    参数名称
     * @param defaultValue 默认值
     * @return 参数值，解析失败返回默认值
     * @throws IllegalArgumentException 如果request或参数名为null
     */
    public static Long getParameterAsLong(HttpServletRequest request, String paramName, Long defaultValue) {
        Long value = getParameterAsLong(request, paramName);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取请求参数（Boolean类型）
     * <p>
     * 支持true/false、yes/no、1/0、on/off等格式。
     * </p>
     *
     * @param request   HttpServletRequest对象
     * @param paramName 参数名称
     * @return 参数值，解析失败返回null
     * @throws IllegalArgumentException 如果request或参数名为null
     */
    public static Boolean getParameterAsBoolean(HttpServletRequest request, String paramName) {
        String value = getParameter(request, paramName);
        if (value == null) {
            return null;
        }
        return "true".equalsIgnoreCase(value)
                || "yes".equalsIgnoreCase(value)
                || "1".equals(value)
                || "on".equalsIgnoreCase(value);
    }

    /**
     * 获取请求参数（Boolean类型，带默认值）
     *
     * @param request      HttpServletRequest对象
     * @param paramName    参数名称
     * @param defaultValue 默认值
     * @return 参数值，解析失败返回默认值
     * @throws IllegalArgumentException 如果request或参数名为null
     */
    public static Boolean getParameterAsBoolean(HttpServletRequest request, String paramName, Boolean defaultValue) {
        Boolean value = getParameterAsBoolean(request, paramName);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取所有请求参数
     *
     * @param request HttpServletRequest对象
     * @return 参数Map，键为参数名，值为参数值数组
     * @throws IllegalArgumentException 如果request为null
     */
    public static Map<String, String[]> getParameterMap(HttpServletRequest request) {
        validateRequest(request);
        return new HashMap<>(request.getParameterMap());
    }

    /**
     * 获取所有请求参数（扁平化）
     * <p>
     * 如果参数有多个值，只取第一个值。
     * </p>
     *
     * @param request HttpServletRequest对象
     * @return 参数Map，键为参数名，值为参数值
     * @throws IllegalArgumentException 如果request为null
     */
    public static Map<String, String> getParameterMapFlat(HttpServletRequest request) {
        validateRequest(request);
        Map<String, String[]> paramMap = request.getParameterMap();
        Map<String, String> result = new HashMap<>(paramMap.size());

        paramMap.forEach((key, values) -> {
            if (values != null && values.length > 0 && StrUtil.isNotBlank(values[0])) {
                result.put(key, values[0].trim());
            }
        });

        return result;
    }

    /**
     * 获取请求参数数组
     *
     * @param request   HttpServletRequest对象
     * @param paramName 参数名称
     * @return 参数值数组
     * @throws IllegalArgumentException 如果request或参数名为null
     */
    public static String[] getParameterValues(HttpServletRequest request, String paramName) {
        validateRequest(request);
        validateNotBlank(paramName, "参数名称不能为空");
        return request.getParameterValues(paramName);
    }

    // ==================== 请求头操作 ====================

    /**
     * 获取请求头
     *
     * @param request    HttpServletRequest对象
     * @param headerName 请求头名称
     * @return 请求头值
     * @throws IllegalArgumentException 如果request或请求头名称为null
     */
    public static String getHeader(HttpServletRequest request, String headerName) {
        validateRequest(request);
        validateNotBlank(headerName, "请求头名称不能为空");
        String value = request.getHeader(headerName);
        return StrUtil.isBlank(value) ? null : value.trim();
    }

    /**
     * 获取请求头（带默认值）
     *
     * @param request      HttpServletRequest对象
     * @param headerName   请求头名称
     * @param defaultValue 默认值
     * @return 请求头值，不存在时返回默认值
     * @throws IllegalArgumentException 如果request或请求头名称为null
     */
    public static String getHeader(HttpServletRequest request, String headerName, String defaultValue) {
        String value = getHeader(request, headerName);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取所有请求头
     *
     * @param request HttpServletRequest对象
     * @return 请求头Map
     * @throws IllegalArgumentException 如果request为null
     */
    public static Map<String, String> getHeaderMap(HttpServletRequest request) {
        validateRequest(request);
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            if (StrUtil.isNotBlank(headerValue)) {
                headerMap.put(headerName, headerValue.trim());
            }
        }

        return headerMap;
    }

    /**
     * 获取User-Agent
     *
     * @param request HttpServletRequest对象
     * @return User-Agent值
     * @throws IllegalArgumentException 如果request为null
     */
    public static String getUserAgent(HttpServletRequest request) {
        return getHeader(request, "User-Agent");
    }

    /**
     * 获取Referer
     *
     * @param request HttpServletRequest对象
     * @return Referer值
     * @throws IllegalArgumentException 如果request为null
     */
    public static String getReferer(HttpServletRequest request) {
        return getHeader(request, "Referer");
    }

    /**
     * 获取Content-Type
     *
     * @param request HttpServletRequest对象
     * @return Content-Type值
     * @throws IllegalArgumentException 如果request为null
     */
    public static String getContentType(HttpServletRequest request) {
        validateRequest(request);
        return request.getContentType();
    }

    // ==================== 请求体读取 ====================

    /**
     * 读取请求体内容（String格式）
     * <p>
     * 适用于读取POST请求的JSON、XML等文本数据。
     * 注意：请求体只能读取一次，读取后会关闭流。
     * </p>
     *
     * @param request HttpServletRequest对象
     * @return 请求体内容
     * @throws IllegalArgumentException 如果request为null
     */
    public static String getRequestBody(HttpServletRequest request) {
        validateRequest(request);
        try {
            BufferedReader reader = request.getReader();
            return IoUtil.read(reader);
        } catch (IOException e) {
            log.error("读取请求体失败", e);
            return null;
        }
    }

    /**
     * 读取请求体内容（byte数组格式）
     *
     * @param request HttpServletRequest对象
     * @return 请求体字节数组
     * @throws IllegalArgumentException 如果request为null
     */
    public static byte[] getRequestBodyBytes(HttpServletRequest request) {
        validateRequest(request);
        try {
            return IoUtil.readBytes(request.getInputStream());
        } catch (IOException e) {
            log.error("读取请求体字节失败", e);
            return null;
        }
    }

    /**
     * 读取JSON请求体并转换为对象
     *
     * @param request HttpServletRequest对象
     * @param clazz   目标类型
     * @param <T>     对象类型
     * @return 转换后的对象，解析失败返回null
     * @throws IllegalArgumentException 如果request或目标类型为null
     */
    public static <T> T getJsonBody(HttpServletRequest request, Class<T> clazz) {
        validateRequest(request);
        Objects.requireNonNull(clazz, "目标类型不能为null");

        try {
            String body = getRequestBody(request);
            if (StrUtil.isBlank(body)) {
                return null;
            }
            return JSONUtil.toBean(body, clazz);
        } catch (Exception e) {
            log.error("解析JSON请求体失败", e);
            return null;
        }
    }

    // ==================== 请求信息获取 ====================

    /**
     * 获取请求方法
     *
     * @param request HttpServletRequest对象
     * @return 请求方法（GET、POST等）
     * @throws IllegalArgumentException 如果request为null
     */
    public static String getMethod(HttpServletRequest request) {
        validateRequest(request);
        return request.getMethod();
    }

    /**
     * 判断是否为GET请求
     *
     * @param request HttpServletRequest对象
     * @return 是否为GET请求
     * @throws IllegalArgumentException 如果request为null
     */
    public static boolean isGetRequest(HttpServletRequest request) {
        return "GET".equalsIgnoreCase(getMethod(request));
    }

    /**
     * 判断是否为POST请求
     *
     * @param request HttpServletRequest对象
     * @return 是否为POST请求
     * @throws IllegalArgumentException 如果request为null
     */
    public static boolean isPostRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(getMethod(request));
    }

    /**
     * 判断是否为Ajax请求
     * <p>
     * 通过X-Requested-With请求头判断。
     * </p>
     *
     * @param request HttpServletRequest对象
     * @return 是否为Ajax请求
     * @throws IllegalArgumentException 如果request为null
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String xRequestedWith = getHeader(request, "X-Requested-With");
        return "XMLHttpRequest".equalsIgnoreCase(xRequestedWith);
    }

    /**
     * 获取请求URI
     *
     * @param request HttpServletRequest对象
     * @return 请求URI
     * @throws IllegalArgumentException 如果request为null
     */
    public static String getRequestUri(HttpServletRequest request) {
        validateRequest(request);
        return request.getRequestURI();
    }

    /**
     * 获取请求URL
     *
     * @param request HttpServletRequest对象
     * @return 请求URL
     * @throws IllegalArgumentException 如果request为null
     */
    public static String getRequestUrl(HttpServletRequest request) {
        validateRequest(request);
        return request.getRequestURL().toString();
    }

    /**
     * 获取完整请求URL（包含查询参数）
     *
     * @param request HttpServletRequest对象
     * @return 完整请求URL
     * @throws IllegalArgumentException 如果request为null
     */
    public static String getFullRequestUrl(HttpServletRequest request) {
        validateRequest(request);
        String url = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (StrUtil.isNotBlank(queryString)) {
            url += "?" + queryString;
        }
        return url;
    }

    /**
     * 获取查询参数字符串
     *
     * @param request HttpServletRequest对象
     * @return 查询参数字符串
     * @throws IllegalArgumentException 如果request为null
     */
    public static String getQueryString(HttpServletRequest request) {
        validateRequest(request);
        return request.getQueryString();
    }

    /**
     * 获取上下文路径
     *
     * @param request HttpServletRequest对象
     * @return 上下文路径
     * @throws IllegalArgumentException 如果request为null
     */
    public static String getContextPath(HttpServletRequest request) {
        validateRequest(request);
        return request.getContextPath();
    }

    /**
     * 获取Servlet路径
     *
     * @param request HttpServletRequest对象
     * @return Servlet路径
     * @throws IllegalArgumentException 如果request为null
     */
    public static String getServletPath(HttpServletRequest request) {
        validateRequest(request);
        return request.getServletPath();
    }

    // ==================== 客户端IP获取 ====================

    /**
     * 获取客户端真实IP地址
     * <p>
     * 支持通过代理服务器和负载均衡器获取真实IP。
     * 按以下顺序尝试获取：X-Forwarded-For、X-Real-IP、Proxy-Client-IP、
     * WL-Proxy-Client-IP、HTTP_CLIENT_IP、HTTP_X_FORWARDED_FOR、RemoteAddr。
     * </p>
     *
     * @param request HttpServletRequest对象
     * @return 客户端IP地址
     * @throws IllegalArgumentException 如果request为null
     */
    public static String getClientIp(HttpServletRequest request) {
        validateRequest(request);

        String ip = getHeader(request, "X-Forwarded-For");
        if (isValidIp(ip)) {
            return getFirstIp(ip);
        }

        ip = getHeader(request, "X-Real-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = getHeader(request, "Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = getHeader(request, "WL-Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = getHeader(request, "HTTP_CLIENT_IP");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = getHeader(request, "HTTP_X_FORWARDED_FOR");
        if (isValidIp(ip)) {
            return getFirstIp(ip);
        }

        ip = request.getRemoteAddr();
        if (LOCALHOST_IPV6.equals(ip)) {
            ip = LOCALHOST_IPV4;
        }

        return ip;
    }

    /**
     * 验证IP地址是否有效
     *
     * @param ip IP地址
     * @return 是否有效
     */
    private static boolean isValidIp(String ip) {
        return StrUtil.isNotBlank(ip)
                && !UNKNOWN.equalsIgnoreCase(ip)
                && ip.length() <= IP_MAX_LENGTH;
    }

    /**
     * 从多个IP中获取第一个有效IP
     * <p>
     * 处理X-Forwarded-For中可能存在的多个IP地址。
     * </p>
     *
     * @param ips IP地址字符串（可能包含多个IP，用逗号分隔）
     * @return 第一个有效IP
     */
    private static String getFirstIp(String ips) {
        if (StrUtil.isBlank(ips)) {
            return ips;
        }
        String[] ipArray = ips.split(IP_SEPARATOR);
        for (String ip : ipArray) {
            ip = ip.trim();
            if (isValidIp(ip) && !isInternalIp(ip)) {
                return ip;
            }
        }
        return ipArray[0].trim();
    }

    /**
     * 判断是否为内网IP
     *
     * @param ip IP地址
     * @return 是否为内网IP
     */
    private static boolean isInternalIp(String ip) {
        if (StrUtil.isBlank(ip)) {
            return false;
        }

        if (LOCALHOST_IPV4.equals(ip) || LOCALHOST_IPV6.equals(ip)) {
            return true;
        }

        try {
            byte[] addr = InetAddress.getByName(ip).getAddress();
            return isInternalIp(addr);
        } catch (UnknownHostException e) {
            return false;
        }
    }

    /**
     * 判断字节数组表示的IP是否为内网IP
     *
     * @param addr IP地址字节数组
     * @return 是否为内网IP
     */
    private static boolean isInternalIp(byte[] addr) {
        if (addr == null || addr.length < 2) {
            return false;
        }

        final byte b0 = addr[0];
        final byte b1 = addr[1];

        // 10.x.x.x
        final byte section1 = 0x0A;
        // 172.16.x.x ~ 172.31.x.x
        final byte section2 = (byte) 0xAC;
        final byte section3 = (byte) 0x10;
        final byte section4 = (byte) 0x1F;
        // 192.168.x.x
        final byte section5 = (byte) 0xC0;
        final byte section6 = (byte) 0xA8;

        switch (b0) {
            case section1:
                return true;
            case section2:
                if (b1 >= section3 && b1 <= section4) {
                    return true;
                }
                break;
            case section5:
                if (b1 == section6) {
                    return true;
                }
                break;
            default:
                return false;
        }
        return false;
    }

    /**
     * 获取客户端主机名
     *
     * @param request HttpServletRequest对象
     * @return 客户端主机名
     * @throws IllegalArgumentException 如果request为null
     */
    public static String getClientHostname(HttpServletRequest request) {
        validateRequest(request);
        String ip = getClientIp(request);
        try {
            InetAddress address = InetAddress.getByName(ip);
            return address.getHostName();
        } catch (UnknownHostException e) {
            log.warn("无法解析主机名: {}", ip);
            return ip;
        }
    }

    // ==================== 响应输出 ====================

    /**
     * 输出JSON响应
     *
     * @param response HttpServletResponse对象
     * @param object   要输出的对象
     * @return 是否输出成功
     * @throws IllegalArgumentException 如果response为null
     */
    public static boolean writeJson(HttpServletResponse response, Object object) {
        validateResponse(response);
        try {
            response.setContentType(CONTENT_TYPE_JSON);
            response.setCharacterEncoding(DEFAULT_ENCODING);
            String json = JSONUtil.toJsonStr(object);
            PrintWriter writer = response.getWriter();
            writer.write(json);
            writer.flush();
            return true;
        } catch (IOException e) {
            log.error("输出JSON响应失败", e);
            return false;
        }
    }

    /**
     * 输出文本响应
     *
     * @param response HttpServletResponse对象
     * @param text     文本内容
     * @return 是否输出成功
     * @throws IllegalArgumentException 如果response为null
     */
    public static boolean writeText(HttpServletResponse response, String text) {
        validateResponse(response);
        try {
            response.setContentType(CONTENT_TYPE_TEXT);
            response.setCharacterEncoding(DEFAULT_ENCODING);
            PrintWriter writer = response.getWriter();
            writer.write(text != null ? text : "");
            writer.flush();
            return true;
        } catch (IOException e) {
            log.error("输出文本响应失败", e);
            return false;
        }
    }

    /**
     * 输出HTML响应
     *
     * @param response HttpServletResponse对象
     * @param html     HTML内容
     * @return 是否输出成功
     * @throws IllegalArgumentException 如果response为null
     */
    public static boolean writeHtml(HttpServletResponse response, String html) {
        validateResponse(response);
        try {
            response.setContentType(CONTENT_TYPE_HTML);
            response.setCharacterEncoding(DEFAULT_ENCODING);
            PrintWriter writer = response.getWriter();
            writer.write(html != null ? html : "");
            writer.flush();
            return true;
        } catch (IOException e) {
            log.error("输出HTML响应失败", e);
            return false;
        }
    }

    /**
     * 输出XML响应
     *
     * @param response HttpServletResponse对象
     * @param xml      XML内容
     * @return 是否输出成功
     * @throws IllegalArgumentException 如果response为null
     */
    public static boolean writeXml(HttpServletResponse response, String xml) {
        validateResponse(response);
        try {
            response.setContentType(CONTENT_TYPE_XML);
            response.setCharacterEncoding(DEFAULT_ENCODING);
            PrintWriter writer = response.getWriter();
            writer.write(xml != null ? xml : "");
            writer.flush();
            return true;
        } catch (IOException e) {
            log.error("输出XML响应失败", e);
            return false;
        }
    }

    /**
     * 设置文件下载响应头
     *
     * @param response HttpServletResponse对象
     * @param fileName 文件名
     * @return 是否设置成功
     * @throws IllegalArgumentException 如果response或文件名为null
     */
    public static boolean setDownloadHeader(HttpServletResponse response, String fileName) {
        validateResponse(response);
        validateNotBlank(fileName, "文件名不能为空");

        try {
            String encodedFileName = URLUtil.encode(fileName, StandardCharsets.UTF_8);
            response.setContentType(CONTENT_TYPE_DOWNLOAD);
            response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName);
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            return true;
        } catch (Exception e) {
            log.error("设置文件下载响应头失败", e);
            return false;
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 验证HttpServletRequest对象是否为空
     *
     * @param request HttpServletRequest对象
     * @throws IllegalArgumentException 如果request为null
     */
    private static void validateRequest(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("HttpServletRequest对象不能为空");
        }
    }

    /**
     * 验证HttpServletResponse对象是否为空
     *
     * @param response HttpServletResponse对象
     * @throws IllegalArgumentException 如果response为null
     */
    private static void validateResponse(HttpServletResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("HttpServletResponse对象不能为空");
        }
    }

    /**
     * 验证字符串是否为空
     *
     * @param value   字符串值
     * @param message 异常消息
     * @throws IllegalArgumentException 如果字符串为空
     */
    private static void validateNotBlank(String value, String message) {
        if (StrUtil.isBlank(value)) {
            throw new IllegalArgumentException(message);
        }
    }
}
