package cn.refinex.common.protection.signature.core.aop;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.refinex.common.constants.ModuleConstants;
import cn.refinex.common.exception.SystemException;
import cn.refinex.common.exception.code.ResultCode;
import cn.refinex.common.protection.signature.core.annotation.ApiSignature;
import cn.refinex.common.protection.signature.core.redis.ApiSignatureRedisService;
import cn.refinex.common.utils.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * HTTP API 签名 AOP 切面, 拦截声明了 {@link ApiSignature} 注解的方法实现签名
 *
 * @author Zhougang
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Aspect
@AllArgsConstructor
public class ApiSignatureAspect {

    private final ApiSignatureRedisService apiSignatureRedisService;

    /**
     * 验证签名是否有效(在方法执行前进行校验)
     *
     * @param joinPoint 连接点
     * @param signature 签名注解
     */
    @Before("@annotation(signature)")
    public void beforePointCut(JoinPoint joinPoint, ApiSignature signature) {
        // 验签通过，直接结束
        if (verifySignature(signature, Objects.requireNonNull(ServletUtils.getRequest()))) {
            return;
        }

        // 验签失败，抛出异常
        log.error("[beforePointCut][方法{} 参数({}) 签名失败]", joinPoint.getSignature().toString(), joinPoint.getArgs());
        throw new SystemException(ModuleConstants.MODULE_COMMON, ResultCode.UNAUTHORIZED.getCode(), StrUtil.blankToDefault(signature.message(), ResultCode.UNAUTHORIZED.getMessage()));
    }

    /**
     * 验证签名是否有效
     * <p>
     * 判定规则：
     * 1. 校验请求头是否包含必要的签名参数
     * 2. 校验 appId 是否能获取到对应的 appSecret
     * 3. 校验签名是否有效
     * 3.1 校验随机数是否重复使用
     * 3.2 校验时间戳是否过期
     * 3.3 校验签名是否与计算结果一致
     *
     * @param signature 签名注解
     * @param request   HTTP 请求
     * @return 是否验证通过
     */
    public boolean verifySignature(ApiSignature signature, HttpServletRequest request) {
        // 校验 Header
        if (!verifyHeaders(signature, request)) {
            return false;
        }

        // 校验 appId 是否能获取到对应的 appSecret
        String appId = request.getHeader(signature.appId());
        String appSecret = apiSignatureRedisService.getAppSecret(appId);
        Assert.notNull(appSecret, "[appId({})] 找不到对应的 appSecret", appId);

        // 校验签名
        // 1. 客户端签名(从请求头中获取)
        String clientSignature = request.getHeader(signature.sign());
        // 2. 服务端签名(根据请求参数和 appSecret 计算出的签名)
        String serverSignatureString = buildSignatureString(signature, request, appSecret);
        String serverSignature = DigestUtil.sha256Hex(serverSignatureString);
        // 3. 校验客户端签名是否与服务端签名一致
        if (ObjUtil.notEqual(clientSignature, serverSignature)) {
            return false;
        }

        // 将 nonce 写入缓存，防止重复使用(此处需要将 ttl 设定为允许 timestamp 时间差的值 x 2)
        String nonce = request.getHeader(signature.nonce());
        if (BooleanUtil.isFalse(apiSignatureRedisService.setNonce(appId, nonce, signature.timeout() * 2, signature.timeUnit()))) {
            String timestamp = request.getHeader(signature.timestamp());
            log.info("[verifySignature][appId({}) timestamp({}) nonce({}) sign({}) 存在重复请求]", appId, timestamp, nonce, clientSignature);
            throw new SystemException(ModuleConstants.MODULE_COMMON, ResultCode.REPEATED_REQUESTS);
        }

        return true;
    }

    /**
     * 验证请求头是否包含必要的签名参数
     * <p>
     * 验证规则：
     * 1. appId 不能为空
     * 2. timestamp 不能为空， 请求是否已超时 (默认 10 min)
     * 3. nonce 不能为空，且长度必须大于等于 10 位，是否在规定时间内被使用过
     * 4. sign 不能为空
     *
     * @param signature 接口签名注解
     * @param request   HTTP 请求
     * @return 是否验证通过
     */
    private boolean verifyHeaders(ApiSignature signature, HttpServletRequest request) {
        // 非空校验，规则：appId、timestamp、nonce、sign 不能为空
        String appId = request.getHeader(signature.appId());
        if (StringUtils.isBlank(appId)) {
            return false;
        }
        String timestamp = request.getHeader(signature.timestamp());
        if (StringUtils.isBlank(timestamp)) {
            return false;
        }
        String nonce = request.getHeader(signature.nonce());
        if (StringUtils.length(nonce) < 10) {
            return false;
        }
        String sign = request.getHeader(signature.sign());
        if (StringUtils.isBlank(sign)) {
            return false;
        }

        //  检查 timestamp 是否超出允许的范围 (注意，此处需要取绝对值，避免时间差为负数)
        long expireTime = signature.timeUnit().toMillis(signature.timeout());
        long requestTimestamp = Long.parseLong(timestamp);
        long timestampDisparity = Math.abs(System.currentTimeMillis() - requestTimestamp);
        if (timestampDisparity > expireTime) {
            return false;
        }

        // 检查 nonce 是否存在，因为 nonce 有且仅能够使用一次
        return Objects.isNull(apiSignatureRedisService.getNonce(appId, nonce));
    }

    /**
     * 构建签名字符串
     * <p>
     * 格式：请求参数 + 请求体 + 请求头 + 应用密钥
     *
     * @param signature 接口签名注解
     * @param request   HTTP 请求
     * @param appSecret 应用密钥
     * @return 签名字符串
     */
    private String buildSignatureString(ApiSignature signature, HttpServletRequest request, String appSecret) {
        SortedMap<String, String> parameterMap = getRequestParameterMap(request);
        SortedMap<String, String> headerMap = getRequestHeaderMap(signature, request);
        String requestBody = StrUtil.nullToDefault(ServletUtils.getRequestBody(request), "");

        return MapUtil.join(parameterMap, "&", "=")
                + requestBody
                + MapUtil.join(headerMap, "&", "=")
                + appSecret;
    }

    /**
     * 获取请求头加签参数 Map
     *
     * @param signature 接口签名注解
     * @param request   HTTP 请求
     * @return 请求头加签参数 Map
     */
    private static SortedMap<String, String> getRequestHeaderMap(ApiSignature signature, HttpServletRequest request) {
        SortedMap<String, String> sortedMap = new TreeMap<>();
        sortedMap.put(signature.appId(), request.getHeader(signature.appId()));
        sortedMap.put(signature.timestamp(), request.getHeader(signature.timestamp()));
        sortedMap.put(signature.nonce(), request.getHeader(signature.nonce()));
        return sortedMap;
    }

    /**
     * 获取请求参数 Map
     *
     * @param request HTTP 请求
     * @return 请求参数 Map
     */
    private static SortedMap<String, String> getRequestParameterMap(HttpServletRequest request) {
        SortedMap<String, String> sortedMap = new TreeMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            sortedMap.put(entry.getKey(), entry.getValue()[0]);
        }
        return sortedMap;
    }
}
