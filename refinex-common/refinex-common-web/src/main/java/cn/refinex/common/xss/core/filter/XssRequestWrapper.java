package cn.refinex.common.xss.core.filter;

import cn.refinex.common.xss.core.clean.XssCleaner;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * XSS 请求包装类, 包装 HTTP 请求, 对请求参数进行 XSS 清理
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
public class XssRequestWrapper extends HttpServletRequestWrapper {

    private final XssCleaner xssCleaner;

    /**
     * 构造函数
     *
     * @param request    原始 HTTP 请求
     * @param xssCleaner XSS 清理器
     */
    public XssRequestWrapper(HttpServletRequest request, XssCleaner xssCleaner) {
        super(request);
        this.xssCleaner = xssCleaner;
    }

    /**
     * 获取请求参数映射, 对参数值进行 XSS 清理
     *
     * @return 请求参数映射
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new LinkedHashMap<>();
        Map<String, String[]> parameters = super.getParameterMap();

        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            String[] values = entry.getValue();
            for (int i = 0; i < values.length; i++) {
                // 对参数值进行 XSS 清理，添加异常保护
                try {
                    values[i] = xssCleaner.clean(values[i]);
                } catch (Exception e) {
                    // 如果 XSS 清理失败，记录日志并保留原始值
                    log.error("XSS cleaning failed for parameter, error: {}", e.getMessage(), e);
                    // values[i] 保持原值不变
                }
            }
            map.put(entry.getKey(), values);
        }

        return map;
    }

    /**
     * 获取请求参数值数组, 对参数值进行 XSS 清理
     *
     * @param name 参数名
     * @return 参数值数组
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return new String[0];
        }

        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            // 对参数值进行 XSS 清理，添加异常保护
            try {
                encodedValues[i] = xssCleaner.clean(values[i]);
            } catch (Exception e) {
                // 如果 XSS 清理失败，记录日志并使用原始值
                log.error("XSS cleaning failed for parameter value, error: {}", e.getMessage(), e);
                encodedValues[i] = values[i];
            }
        }

        return encodedValues;
    }

    /**
     * 获取请求参数值, 对参数值进行 XSS 清理
     *
     * @param name 参数名
     * @return 参数值
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value == null) {
            return null;
        }

        // 对参数值进行 XSS 清理，添加异常保护
        try {
            return xssCleaner.clean(value);
        } catch (Exception e) {
            // 如果 XSS 清理失败，记录日志并返回原始值
            log.error("XSS cleaning failed for parameter, error: {}", e.getMessage(), e);
            return value;
        }
    }

    /**
     * 获取请求属性值, 对属性值进行 XSS 清理
     *
     * @param name 属性名
     * @return 属性值
     */
    @Override
    public Object getAttribute(String name) {
        Object value = super.getAttribute(name);
        if (value instanceof String strValue) {
            // 对属性值进行 XSS 清理，添加异常保护
            try {
                return xssCleaner.clean(strValue);
            } catch (Exception e) {
                // 如果 XSS 清理失败，记录日志并返回原始值
                log.error("XSS cleaning failed for attribute, error: {}", e.getMessage(), e);
                return strValue;
            }
        }

        return value;
    }

    /**
     * 获取请求头值, 对头值进行 XSS 清理
     *
     * @param name 头名
     * @return 头值
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }

        // 对头值进行 XSS 清理，添加异常保护
        try {
            return xssCleaner.clean(value);
        } catch (Exception e) {
            // 如果 XSS 清理失败，记录日志并返回原始值，避免影响正常请求处理
            log.error("XSS cleaning failed for header: {}, error: {}", name, e.getMessage(), e);
            return value;
        }
    }

    /**
     * 获取请求查询字符串, 对查询字符串进行 XSS 清理
     *
     * @return 查询字符串
     */
    @Override
    public String getQueryString() {
        String value = super.getQueryString();
        if (value == null) {
            return null;
        }

        // 对查询字符串进行 XSS 清理，添加异常保护
        try {
            return xssCleaner.clean(value);
        } catch (Exception e) {
            // 如果 XSS 清理失败，记录日志并返回原始值
            log.error("XSS cleaning failed for query string, error: {}", e.getMessage(), e);
            return value;
        }
    }
}
