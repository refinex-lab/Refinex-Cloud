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
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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
     * 全局默认 RestClient 构建器（不支持负载均衡）
     *
     * @return RestClient.Builder
     */
    @Bean("restClientBuilder")
    @Primary // 使用 @Primary 标记为主要的 Builder，避免自动装配冲突
    @ConditionalOnMissingBean(name = "restClientBuilder")
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    /**
     * LoadBalancer 拦截器（用于处理 lb:// 协议）
     *
     * @param loadBalancerClient LoadBalancer 客户端
     * @return ClientHttpRequestInterceptor
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.cloud.client.loadbalancer.LoadBalancerClient")
    // 仅在 LoadBalancerClient 存在时才创建此 Bean
    @ConditionalOnMissingBean(name = "loadBalancerInterceptor")
    public ClientHttpRequestInterceptor loadBalancerInterceptor(ObjectProvider<LoadBalancerClient> loadBalancerClient) {
        return (request, body, execution) -> {
            // 提取原始 URI 和协议
            URI originalUri = request.getURI();
            String scheme = originalUri.getScheme();

            // 只处理 lb:// 协议
            if ("lb".equals(scheme)) {
                // 获取可用的 LoadBalancerClient 实例
                LoadBalancerClient client = loadBalancerClient.getIfAvailable();
                if (client == null) {
                    throw new IllegalStateException("LoadBalancerClient 未找到，无法处理 lb:// 协议");
                }

                // 提取服务名称（主机名）
                String serviceName = originalUri.getHost();
                log.debug("使用 LoadBalancer 解析服务: {}", serviceName);

                // 通过 LoadBalancer 选择服务实例
                ServiceInstance instance = client.choose(serviceName);
                if (instance == null) {
                    throw new IllegalStateException("无法找到服务实例: " + serviceName);
                }

                // 构建真实的 URI
                URI realUri = UriComponentsBuilder.fromUri(originalUri)
                        .scheme(instance.getScheme())
                        .host(instance.getHost())
                        .port(instance.getPort())
                        .build()
                        .toUri();

                log.debug("服务 {} 解析为: {}", serviceName, realUri);

                // 创建新的请求对象，代理原始请求但使用新的 URI
                HttpRequest newRequest = new HttpRequest() {

                    /**
                     * 获取 HTTP 方法（如 GET、POST 等）
                     * @return HttpMethod
                     */
                    @Override
                    public org.springframework.http.HttpMethod getMethod() {
                        return request.getMethod();
                    }

                    /**
                     * 获取请求的 URI
                     * @return URI
                     */
                    @Override
                    public URI getURI() {
                        return realUri;
                    }

                    /**
                     * 获取请求头（如 Content-Type、Authorization 等）
                     * @return HttpHeaders
                     */
                    @Override
                    public HttpHeaders getHeaders() {
                        return request.getHeaders();
                    }

                    /**
                     * 获取请求属性（如超时、连接池等）
                     * @return Map<String, Object>
                     */
                    @Override
                    public Map<String, Object> getAttributes() {
                        return request.getAttributes();
                    }
                };

                // 执行请求
                return execution.execute(newRequest, body);
            }

            // 非 lb:// 协议，直接执行
            return execution.execute(request, body);
        };
    }

    /**
     * 支持负载均衡的 RestClient 构建器（用于 lb:// 协议）
     * <p>
     * 1. 仅在 LoadBalancer 类存在时才创建此 Bean
     * 2. 使用自定义的 LoadBalancer 拦截器来处理 lb:// 协议
     *
     * @param loadBalancerInterceptor LoadBalancer 拦截器
     * @return RestClient.Builder
     */
    @Bean("loadBalancedRestClientBuilder")
    @ConditionalOnClass(name = "org.springframework.cloud.client.loadbalancer.LoadBalancerClient")
    @ConditionalOnMissingBean(name = "loadBalancedRestClientBuilder")
    public RestClient.Builder loadBalancedRestClientBuilder(ObjectProvider<ClientHttpRequestInterceptor> loadBalancerInterceptor) {
        RestClient.Builder builder = RestClient.builder()
                .requestFactory(clientHttpRequestFactory());

        // 添加 LoadBalancer 拦截器
        loadBalancerInterceptor.ifAvailable(builder::requestInterceptor);

        return builder;
    }

    /**
     * 全局默认 RestClient 配置 (使用默认 baseUrl，通常用于网关调用)
     *
     * @param builder    RestClient 构建器（自动注入 @Primary 的 restClientBuilder）
     * @param properties Http 接口客户端配置属性
     * @return RestClient
     */
    @Bean
    @Primary // 使用 @Primary 标记为主要的 RestClient
    @ConditionalOnMissingBean(name = "restClient")
    public RestClient restClient(RestClient.Builder builder, HttpInterfaceClientProperties properties) {
        return createRestClient(builder, properties.getBaseUrl());
    }

    /**
     * 创建 RestClient 的通用方法
     *
     * @param builder RestClient 构建器
     * @param baseUrl 基础 URL
     * @return 配置好的 RestClient 实例
     */
    private RestClient createRestClient(RestClient.Builder builder, String baseUrl) {
        return builder
                // 设置基础 URL
                .baseUrl(baseUrl)
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
     * 根据服务名创建 RestClient, 自动选择合适的 Builder（支持负载均衡或普通）
     *
     * @param serviceName                 服务名 (如 refinex-auth、refinex-platform)
     * @param normalBuilderProvider       普通 RestClient 构建器提供者
     * @param loadBalancedBuilderProvider 支持负载均衡的 RestClient 构建器提供者
     * @param properties                  Http 接口客户端配置属性
     * @return 针对特定服务的 RestClient 实例
     */
    public RestClient createRestClientForService(String serviceName,
                                                 ObjectProvider<RestClient.Builder> normalBuilderProvider,
                                                 ObjectProvider<RestClient.Builder> loadBalancedBuilderProvider,
                                                 HttpInterfaceClientProperties properties) {

        String serviceUrl = properties.getServiceUrl(serviceName);
        log.info("为服务 [{}] 创建 RestClient，目标地址：{}", serviceName, serviceUrl);

        // 如果使用 lb:// 协议，尝试使用支持负载均衡的 builder
        if (serviceUrl.startsWith("lb://")) {
            RestClient.Builder loadBalancedBuilder = loadBalancedBuilderProvider.getIfAvailable();
            if (loadBalancedBuilder != null) {
                log.info("检测到 lb:// 协议，使用负载均衡 RestClient");
                return createRestClient(loadBalancedBuilder, serviceUrl);
            } else {
                log.warn("配置了 lb:// 协议但未找到负载均衡 Builder，降级使用普通 Builder");
            }
        }

        // 使用普通 builder
        RestClient.Builder normalBuilder = normalBuilderProvider.getIfAvailable(RestClient::builder);
        return createRestClient(normalBuilder, serviceUrl);
    }

    /**
     * 共享 RestClient 代理工厂(用于创建共享的 HTTP 服务代理)
     * <p>
     * 注意：此工厂使用默认 baseUrl，适用于需要通过网关调用的场景
     *
     * @param restClient 全局默认 RestClient 实例（自动注入 @Primary 的 restClient）
     * @return HttpServiceProxyFactory
     */
    @Bean
    @Primary // 使用 @Primary 标记为主要的 HttpServiceProxyFactory，方便使用者直接注入
    @ConditionalOnMissingBean(HttpServiceProxyFactory.class)
    public HttpServiceProxyFactory httpServiceProxyFactory(RestClient restClient) {
        log.info("创建默认 HttpServiceProxyFactory，使用网关地址");
        return createProxyFactory(restClient);
    }

    /**
     * 创建 HttpServiceProxyFactory 的通用方法
     *
     * @param restClient RestClient 实例
     * @return 配置好的 HttpServiceProxyFactory
     */
    private HttpServiceProxyFactory createProxyFactory(RestClient restClient) {
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
     * 根据服务名创建 HttpServiceProxyFactory
     * 自动选择合适的 Builder（支持负载均衡或普通）
     *
     * @param serviceName                 服务名 (如 refinex-auth、refinex-platform)
     * @param normalBuilderProvider       普通 RestClient 构建器提供者
     * @param loadBalancedBuilderProvider 支持负载均衡的 RestClient 构建器提供者
     * @param properties                  Http 接口客户端配置属性
     * @return 针对特定服务的 HttpServiceProxyFactory
     */
    public HttpServiceProxyFactory createProxyFactoryForService(String serviceName,
                                                                ObjectProvider<RestClient.Builder> normalBuilderProvider,
                                                                ObjectProvider<RestClient.Builder> loadBalancedBuilderProvider,
                                                                HttpInterfaceClientProperties properties) {
        RestClient restClient = createRestClientForService(serviceName, normalBuilderProvider, loadBalancedBuilderProvider, properties);
        return createProxyFactory(restClient);
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
