package cn.refinex.common.apilog.core.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.refinex.common.apilog.core.annotation.LogOperation;
import cn.refinex.common.apilog.core.enums.OperateTypeEnum;
import cn.refinex.common.constants.GlobalAttributeKeyConstants;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.exception.code.ResultCode;
import cn.refinex.common.json.utils.JsonUtils;
import cn.refinex.common.utils.Fn;
import cn.refinex.common.utils.device.DeviceUtils;
import cn.refinex.common.utils.servlet.ServletUtils;
import cn.refinex.common.web.config.properties.WebProperties;
import cn.refinex.common.web.core.filter.ApiRequestFilter;
import cn.refinex.platform.client.logger.LogOperationClient;
import cn.refinex.platform.client.logger.dto.request.LogOperationCreateRequest;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.yauaa.shaded.org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static cn.refinex.common.apilog.core.interceptor.LogOperationInterceptor.ATTRIBUTE_HANDLER_METHOD;

/**
 * 日志操作过滤器，记录操作日志
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
public class LogOperationFilter extends ApiRequestFilter {

    /**
     * 敏感字段，需要脱敏
     */
    private static final String[] SANITIZE_KEYS = new String[]{"password", "token", "accessToken", "refreshToken"};

    private final String applicationName;
    private final LogOperationClient logOperationClient;

    /**
     * 构造函数
     *
     * @param webProperties      Web 配置属性
     * @param applicationName    应用名称
     * @param logOperationClient 操作日志客户端
     */
    public LogOperationFilter(WebProperties webProperties, String applicationName, LogOperationClient logOperationClient) {
        super(webProperties);
        this.applicationName = applicationName;
        this.logOperationClient = logOperationClient;
    }

    /**
     * 过滤请求，记录操作日志
     *
     * @param request     当前请求
     * @param response    当前响应
     * @param filterChain 过滤器链
     * @throws ServletException 异常
     * @throws IOException      异常
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        LocalDateTime beginTime = LocalDateTime.now();

        // 提前拿到参数，避免被 Xss 过滤处理
        Map<String, String> parameterMap = ServletUtils.getParameterMapFlat(request);
        String requestBody = ServletUtils.getRequestBody(request);

        try {
            filterChain.doFilter(request, response);

            // 成功记录日志
            saveLogOperation(request, beginTime, parameterMap, requestBody, null);
        } catch (Exception ex) {
            // 失败记录日志
            saveLogOperation(request, beginTime, parameterMap, requestBody, ex);
            throw ex;
        }
    }

    /**
     * 保存操作日志
     *
     * @param request      当前请求
     * @param beginTime    开始时间
     * @param parameterMap 参数映射
     * @param requestBody  请求体
     * @param ex           异常
     */
    private void saveLogOperation(HttpServletRequest request, LocalDateTime beginTime, Map<String, String> parameterMap, String requestBody, Exception ex) {
        LogOperationCreateRequest logOperationRequest = new LogOperationCreateRequest();
        try {
            boolean isSuccess = buildLogOperation(logOperationRequest, request, beginTime, parameterMap, requestBody, ex);
            if (!isSuccess) {
                return;
            }

            logOperationClient.saveLogOperationAsync(logOperationRequest);
        } catch (Exception e) {
            log.error("记录操作日志失败, url: {}, logInfo: {}", request.getRequestURI(), logOperationRequest, e);
        }
    }

    /**
     * 构建操作日志创建请求
     *
     * @param logOperationRequest 操作日志创建请求
     * @param request             当前请求
     * @param beginTime           开始时间
     * @param parameterMap        参数映射
     * @param requestBody         请求体
     * @param ex                  异常
     * @return 是否构建成功
     */
    private boolean buildLogOperation(LogOperationCreateRequest logOperationRequest, HttpServletRequest request, LocalDateTime beginTime, Map<String, String> parameterMap, String requestBody, Exception ex) {
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute(ATTRIBUTE_HANDLER_METHOD);
        LogOperation logOperationAnnotation = null;
        if (Objects.nonNull(handlerMethod)) {
            // 只有方法上使用了 @LogOperation 注解，并且开启了记录日志，才记录日志
            logOperationAnnotation = handlerMethod.getMethodAnnotation(LogOperation.class);
            if (Objects.nonNull(logOperationAnnotation) && BooleanUtil.isFalse(logOperationAnnotation.enabled())) {
                return false;
            }
        }

        // 设置应用名称
        logOperationRequest.setApplicationName(applicationName);

        // 处理用户信息(看下直接使用 StpUtil.getLoginId() 获取是否有问题？)
        Long userId = Long.valueOf(StpUtil.getLoginId().toString());
        String username = StpUtil.getSession().get("username").toString();
        logOperationRequest.setUserId(userId);
        logOperationRequest.setUsername(username);

        // 提取请求方式
        String requestMethod = request.getMethod();
        logOperationRequest.setRequestMethod(requestMethod);

        // 设置请求路径
        String requestUrl = request.getRequestURI();
        logOperationRequest.setRequestUrl(requestUrl);

        // 设置请求参数，parameterMap 和 requestBody
        if (logOperationAnnotation != null && logOperationAnnotation.recordRequestParams()) {
            String[] sensitiveParams = logOperationAnnotation.sensitiveParams();
            if (sensitiveParams.length > 0) {
                logOperationRequest.setRequestParams(sensitiveParamMask(parameterMap, sensitiveParams));
                logOperationRequest.setRequestBody(sensitiveJsonMask(requestBody, sensitiveParams));
            } else {
                logOperationRequest.setRequestParams(parameterMap.toString());
                logOperationRequest.setRequestBody(requestBody);
            }
        }

        // 设置响应结果或者异常信息
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        ApiResult<?> apiResult = (ApiResult<?>) servletRequest.getAttribute(GlobalAttributeKeyConstants.API_RESULT_KEY);
        if (Objects.nonNull(apiResult)) {
            if (logOperationAnnotation != null && logOperationAnnotation.recordResponseBody()) {
                logOperationRequest.setResponseResult(apiResult.toString());
            }
        } else if (Objects.nonNull(ex)) {
            String exceptionMsg = ExceptionUtil.getRootCause(ex).toString();
            logOperationRequest.setErrorMessage(exceptionMsg);
            logOperationRequest.setResponseResult(ApiResult.failure(
                    ResultCode.INTERNAL_ERROR.getCode(),
                    ExceptionUtil.getRootCauseMessage(ex)).toString()
            );
        }

        // 设置操作IP
        logOperationRequest.setOperationIp(ServletUtils.getClientIp(request));

        // 设置操作位置
        logOperationRequest.setOperationLocation(request.getHeader("Referer"));

        // 设置浏览器，通过 User-Agent 提取
        String userAgent = ServletUtils.getUserAgent(request);
        String browserName = DeviceUtils.getBrowserName(userAgent);
        logOperationRequest.setBrowser(browserName);

        // 设置操作系统
        String osName = DeviceUtils.getOperatingSystemName(userAgent);
        logOperationRequest.setOs(osName);

        // 设置操作状态(0成功,1失败)
        logOperationRequest.setOperationStatus(Objects.nonNull(ex) ? 1 : 0);

        // 设置执行时间(毫秒)
        long executeTime = Duration.between(beginTime, LocalDateTime.now()).toMillis();
        logOperationRequest.setExecutionTime(Fn.getInt(executeTime, null));

        // 设置创建时间
        logOperationRequest.setCreateTime(LocalDateTime.now());

        // 解析操作模块、操作类型、操作描述
        if (Objects.nonNull(handlerMethod)) {
            // 提取 Swagger 注解
            Tag tagAnnotation = handlerMethod.getBeanType().getAnnotation(Tag.class);
            Operation operationAnnotation = handlerMethod.getMethodAnnotation(Operation.class);

            String operateModule = null;
            if (logOperationAnnotation != null && StrUtil.isNotBlank(logOperationAnnotation.operateModule())) {
                operateModule = logOperationAnnotation.operateModule();
            } else {
                operateModule = tagAnnotation != null ? StrUtil.nullToDefault(tagAnnotation.name(), tagAnnotation.description()) : null;
            }
            logOperationRequest.setOperationModule(operateModule);

            String operateDesc = null;
            if (logOperationAnnotation != null && StrUtil.isNotBlank(logOperationAnnotation.operateDesc())) {
                operateDesc = logOperationAnnotation.operateDesc();
            } else {
                operateDesc = operationAnnotation != null ? operationAnnotation.summary() : null;
            }
            logOperationRequest.setOperationDesc(operateDesc);

            String operateType = null;
            if (logOperationAnnotation != null && logOperationAnnotation.operationType().length > 0) {
                operateType = logOperationAnnotation.operationType()[0].name();
            } else {
                operateType = parseOperateLogType(request).getValue();
            }
            logOperationRequest.setOperationType(operateType);
        }

        return true;
    }

    /**
     * 解析操作类型
     *
     * @param request 请求对象
     * @return 操作类型枚举
     */
    private static OperateTypeEnum parseOperateLogType(HttpServletRequest request) {
        RequestMethod requestMethod = RequestMethod.resolve(request.getMethod());
        if (Objects.isNull(requestMethod)) {
            return OperateTypeEnum.OTHER;
        }

        return switch (requestMethod) {
            case GET -> OperateTypeEnum.GET;
            case POST -> OperateTypeEnum.CREATE;
            case PUT -> OperateTypeEnum.UPDATE;
            case DELETE -> OperateTypeEnum.DELETE;
            default -> OperateTypeEnum.OTHER;
        };
    }

    /**
     * 对请求参数中的敏感信息进行掩码处理
     *
     * @param requestParams 请求参数
     * @param sensitiveParams 敏感参数数组
     * @return 处理后的请求参数 JSON 字符串
     */
    private static String sensitiveParamMask(Map<String, ?> requestParams, String[] sensitiveParams) {
        if (MapUtils.isEmpty(requestParams)) {
            return null;
        }
        if (ArrayUtils.isNotEmpty(sensitiveParams)) {
            MapUtil.removeAny(requestParams, sensitiveParams);
        }

        MapUtil.removeAny(requestParams, SANITIZE_KEYS);
        return JsonUtils.toJson(requestParams);
    }

    /**
     * 对 JSON 字符串中的敏感信息进行掩码处理
     *
     * @param jsonStr JSON 字符串
     * @param sensitiveParams 敏感参数数组
     * @return 处理后的 JSON 字符串
     */
    private static String sensitiveJsonMask(String jsonStr, String[] sensitiveParams) {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }

        try {
            JsonNode jsonNode = JsonUtils.toTree(jsonStr);
            // 只处理 data 字段，不处理 code、message 等字段，避免错误被脱敏掉
            sensitiveJson(jsonNode.get("data"), sensitiveParams);
            return JsonUtils.toJson(jsonNode);
        } catch (Exception e) {
            // 脱敏失败返回原始字符串
            log.error("脱敏 JSON 字符串失败, jsonStr={}", jsonStr, e);
            return jsonStr;
        }
    }

    private static void sensitiveJson(JsonNode node, String[] sensitiveParams) {
        if (node.isArray()) {
            node.forEach(child -> sensitiveJson(child, sensitiveParams));
        } else if (node.isObject()) {
            Set<Map.Entry<String, JsonNode>> properties = node.properties();
            properties.forEach(entry -> sensitiveJson(entry.getValue(), sensitiveParams));
        }
    }
}
