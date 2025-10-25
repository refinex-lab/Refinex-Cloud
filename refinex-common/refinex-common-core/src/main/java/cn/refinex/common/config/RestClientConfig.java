package cn.refinex.common.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.exception.SystemException;
import cn.refinex.common.invoker.GenericQueryParamArgumentResolver;
import cn.refinex.common.json.utils.JsonUtils;
import cn.refinex.common.properties.HttpInterfaceClientProperties;
import cn.refinex.common.utils.Fn;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 全局 RestClient 配置
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(HttpInterfaceClientProperties.class)
public class RestClientConfig {

    /**
     * 全局默认 RestClient 构建器
     *
     * @return RestClient.Builder
     */
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    /**
     * 全局默认 RestClient 配置
     *
     * @param builder    RestClient 构建器
     * @param properties Http 接口客户端配置属性
     * @return RestClient
     */
    @Bean
    public RestClient restClient(RestClient.Builder builder, HttpInterfaceClientProperties properties) {
        return builder
                // 设置基础 URL(注意，这里只配置到服务名，不包含 URL 路径前缀)
                .baseUrl(properties.getBaseUrl())
                // 设置默认请求头 Accept: application/json
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                // 配置 4xx 客户端错误状态处理器
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                    String responseBody = extractResponseBody(response);
                    ApiResult<?> apiResult = parseErrorResponse(responseBody);

                    log.error("客户端请求错误，状态码：{}，响应体：{}", response.getStatusCode(), responseBody);

                    // 根据具体状态码抛出不同异常
                    switch (response.getStatusCode().value()) {
                        case 400:
                            throw new BusinessException(apiResult.code(), "请求参数错误: " + apiResult.message());
                        case 401:
                            throw new BusinessException(cn.refinex.common.enums.HttpStatusCode.UNAUTHORIZED);
                        case 403:
                            throw new BusinessException(cn.refinex.common.enums.HttpStatusCode.FORBIDDEN);
                        case 404:
                            throw new BusinessException(cn.refinex.common.enums.HttpStatusCode.NOT_FOUND);
                        case 409:
                            throw new BusinessException(cn.refinex.common.enums.HttpStatusCode.CONFLICT);
                        default:
                            throw new BusinessException(apiResult.code(), apiResult.message());
                    }
                })
                // 配置 5xx 服务器错误状态处理器
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (request, response) -> {
                    String responseBody = extractResponseBody(response);
                    ApiResult<?> apiResult = parseErrorResponse(responseBody);

                    log.error("服务器端错误，状态码：{}，响应体：{}", response.getStatusCode(), responseBody);
                    throw new SystemException(apiResult.code(), apiResult.message());
                })
                // 配置默认状态处理器(错误处理)
                .defaultStatusHandler(statusCode -> !statusCode.is2xxSuccessful()
                        || statusCode.is4xxClientError()
                        || statusCode.is5xxServerError(),
                        (request, response) -> {
                    String responseBody = extractResponseBody(response);
                    ApiResult<?> apiResult = parseErrorResponse(responseBody);

                    log.error("未知错误，状态码：{}，响应体：{}", response.getStatusCode(), responseBody);
                    throw new SystemException(apiResult.code(), apiResult.message());
                })
                // 添加请求拦截器
                .requestInterceptor((request, body, execution) -> {
                    // 记录请求日志
                    log.info("发送请求: {} {}", request.getMethod(), request.getURI());

                    // (网关调用)添加认证令牌
                    String tokenValue = StpUtil.getTokenValue();
                    if (StringUtils.isNotBlank(tokenValue)) {
                        request.getHeaders().setBearerAuth(tokenValue);
                    }

                    // (服务间调用)检查是否开启了相同 Token 校验(解决同源系统互相调用时的身份认证校验)
                    Boolean checkSameToken = SaManager.getConfig().getCheckSameToken();
                    if (Boolean.TRUE.equals(checkSameToken)) {
                        request.getHeaders().add(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());
                    }

                    // 添加请求ID追踪
                    request.getHeaders().add("X-Request-ID", Fn.getUuid32());

                    // 继续执行请求
                    ClientHttpResponse response = execution.execute(request, body);

                    // 记录响应日志
                    log.info("收到响应: {} {}", response.getStatusCode(), response.getBody());
                    return response;
                })
                .build();
    }

    /**
     * 共享 RestClient 代理工厂(用于创建共享的 HTTP 服务代理)
     *
     * @param restClient 全局默认 RestClient 实例
     * @return HttpServiceProxyFactory
     */
    @Bean
    public HttpServiceProxyFactory sharedProxyFactory(@Qualifier("restClient") RestClient restClient) {
        // 创建适配器
        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        // 构建代理工厂
        return HttpServiceProxyFactory
                .builderFor(adapter)
                // 配置自定义参数解析器
                .customArgumentResolver(new GenericQueryParamArgumentResolver())
                // 配置嵌入值解析器(用于解析 ${...} 占位符)
                .embeddedValueResolver(new StandardEnvironment()::resolvePlaceholders)
                .build();
    }

    /**
     * 自定义 ClientHttpRequestFactory 配置, 用于底层 HTTP 客户端的精细化控制(基于 Apache HttpClient 5)
     *
     * @return ClientHttpRequestFactory
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        // 连接池管理配置
        HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                // 最大连接数
                .setMaxConnTotal(100)
                // 每个路由的最大连接数
                .setMaxConnPerRoute(20)
                .build();

        // 构建 Apache HttpClient 实例
        HttpClient apacheHttpClient = HttpClients.custom()
                // 设置连接池管理器
                .setConnectionManager(connectionManager)
                // 设置默认请求配置
                .setDefaultRequestConfig(RequestConfig.custom()
                        // 连接请求超时时间(从连接池获取连接的超时时间)
                        .setConnectionRequestTimeout(Timeout.ofSeconds(5))
                        // 响应超时时间(从服务器读取响应的超时时间)
                        .setResponseTimeout(Timeout.ofSeconds(30))
                        .build())
                .build();

        // 返回基于 Apache HttpClient 的请求工厂
        return new HttpComponentsClientHttpRequestFactory(apacheHttpClient);
    }

    /**
     * 从 ClientHttpResponse 中提取响应体内容(默认使用 UTF-8 编码)
     *
     * @param response ClientHttpResponse 实例
     * @return 响应体内容字符串(如果读取失败，返回空字符串)
     */
    private String extractResponseBody(ClientHttpResponse response) {
        try {
            return new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * 解析 ErrorResponse 响应体内容
     *
     * @param body 响应体内容字符串
     * @return ErrorResponse 实例
     */
    private ApiResult<?> parseErrorResponse(String body) {
        return JsonUtils.fromJson(body, ApiResult.class);
    }
}
