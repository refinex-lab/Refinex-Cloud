package cn.refinex.gateway.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.exception.code.ResultCode;
import cn.refinex.common.json.utils.JsonUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;

/**
 * WebFlux 工具类
 *
 * @author Lion Li
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WebFluxUtils {

    /**
     * 获取原始请求URL
     *
     * @param exchange 服务器Web交换对象
     * @return 原始请求URL
     */
    public static String getOriginalRequestUrl(ServerWebExchange exchange) {
        // 获取原始请求URL
        ServerHttpRequest request = exchange.getRequest();
        LinkedHashSet<URI> uris = exchange.getAttributeOrDefault(GATEWAY_ORIGINAL_REQUEST_URL_ATTR, new LinkedHashSet<>());

        // 如果存在原始请求URL，返回第一个；否则返回当前请求URL
        URI requestUri = uris.stream().findFirst().orElse(request.getURI());

        // 构建原始请求URL
        return UriComponentsBuilder.fromPath(requestUri.getRawPath()).build().toUriString();
    }

    /**
     * 判断是否为JSON请求
     *
     * @param exchange 服务器Web交换对象
     * @return 是否为JSON请求
     */
    public static boolean isJsonRequest(ServerWebExchange exchange) {
        // 获取请求头中的 Content-Type
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);

        // 如果 Content-Type 为空，默认返回 false
        if (StringUtils.isBlank(header)) {
            return false;
        }

        // 判断 Content-Type 是否为 JSON 类型
        return StringUtils.startsWithIgnoreCase(header, MediaType.APPLICATION_JSON_VALUE);
    }

    /**
     * 从请求体中解析JSON字符串
     *
     * @param serverHttpRequest 服务器HTTP请求对象
     * @return JSON字符串
     */
    public static String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        // 获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();

        // 订阅请求体，将数据转换为字符串
        body.subscribe(buffer -> {
            try (DataBuffer.ByteBufferIterator iterator = buffer.readableByteBuffers()) {
                CharBuffer charBuffer = StandardCharsets.UTF_8.decode(iterator.next());
                DataBufferUtils.release(buffer);
                bodyRef.set(charBuffer.toString());
            }
        });

        // 返回解析后的JSON字符串
        return bodyRef.get();
    }

    /**
     * 从缓存请求体中解析JSON字符串
     *
     * @param exchange 服务器Web交换对象
     * @return JSON字符串
     */
    public static String resolveBodyFromCacheRequest(ServerWebExchange exchange) {
        // 从缓存中获取请求体
        Object obj = exchange.getAttributes().get(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR);
        if (ObjectUtil.isNull(obj)) {
            return null;
        }

        // 转换为 DataBuffer
        DataBuffer buffer = (DataBuffer) obj;
        try (DataBuffer.ByteBufferIterator iterator = buffer.readableByteBuffers()) {
            StringBuilder sb = new StringBuilder();
            iterator.forEachRemaining(e -> sb.append(StandardCharsets.UTF_8.decode(e)));
            return sb.toString();
        }
    }

    /**
     * 响应写入器
     *
     * @param response 服务器HTTP响应对象
     * @param value    响应值
     * @return 响应写入器
     */
    public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, Object value) {
        return webFluxResponseWriter(response, HttpStatus.OK, value, ResultCode.INTERNAL_ERROR.getCode());
    }

    /**
     * 响应写入器
     *
     * @param response 服务器HTTP响应对象
     * @param value    响应值
     * @param code     响应码
     * @return 响应写入器
     */
    public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, Object value, int code) {
        return webFluxResponseWriter(response, HttpStatus.OK, value, code);
    }

    /**
     * 响应写入器
     *
     * @param response 服务器HTTP响应对象
     * @param status   HTTP状态码
     * @param value    响应值
     * @param code     响应码
     * @return 响应写入器
     */
    public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, HttpStatus status, Object value, int code) {
        return webFluxResponseWriter(response, MediaType.APPLICATION_JSON_VALUE, status, value, code);
    }

    /**
     * 响应写入器
     *
     * @param response    服务器HTTP响应对象
     * @param contentType 响应内容类型
     * @param status      HTTP状态码
     * @param value       响应值
     * @param code        响应码
     * @return 响应写入器
     */
    public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, String contentType, HttpStatus status, Object value, int code) {
        // 设置响应状态码和内容类型
        response.setStatusCode(status);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, contentType);

        // 构建响应体
        ApiResult<?> result = ApiResult.failure(code, value.toString());
        DataBuffer dataBuffer = response.bufferFactory().wrap(JsonUtils.toJson(result).getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }
}
