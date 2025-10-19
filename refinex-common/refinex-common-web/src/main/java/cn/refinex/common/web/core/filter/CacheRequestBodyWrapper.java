package cn.refinex.common.web.core.filter;

import cn.refinex.common.utils.servlet.ServletUtils;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 缓存请求体 Wrapper, 用于缓存请求体, 方便后续支持 JSON 请求重复读取请求体
 *
 * @author 芋道源码
 * @since 1.0.0
 */
public class CacheRequestBodyWrapper extends HttpServletRequestWrapper {

    /**
     * 缓存请求体
     */
    private final byte[] body;

    /**
     * 构造函数
     *
     * @param request HttpServletRequest
     */
    public CacheRequestBodyWrapper(HttpServletRequest request) {
        super(request);
        body = ServletUtils.getRequestBodyBytes(request);
    }

    /**
     * 获取请求体 BufferedReader
     *
     * @return BufferedReader
     * @throws IOException IOException
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    /**
     * 获取请求体 ServletInputStream
     *
     * @return ServletInputStream
     * @throws IOException IOException
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(body);
        // 返回缓存的请求体 ServletInputStream
        return new ServletInputStream() {

            /**
             * 缓存请求体是否读取完成
             *
             * @return boolean
             */
            @Override
            public boolean isFinished() {
                return false;
            }

            /**
             * 缓存请求体是否准备就绪
             *
             * @return boolean
             */
            @Override
            public boolean isReady() {
                return false;
            }

            /**
             * 设置缓存请求体读取监听器
             *
             * @param readListener ReadListener
             */
            @Override
            public void setReadListener(ReadListener readListener) {

            }

            /**
             * 从缓存请求体读取字节
             *
             * @return int
             */
            @Override
            public int read() {
                // 关键：从缓存请求体读取字节而不是从原始请求体读取字节，因为原始请求体已经被读取一次了
                // 再次读取原始请求体会出现异常
                return inputStream.read();
            }

            /**
             * 缓存请求体可用字节数
             *
             * @return int
             */
            @Override
            public int available() {
                return body.length;
            }
        };
    }
}
