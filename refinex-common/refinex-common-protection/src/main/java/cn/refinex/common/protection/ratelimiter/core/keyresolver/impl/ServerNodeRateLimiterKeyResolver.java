package cn.refinex.common.protection.ratelimiter.core.keyresolver.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.system.SystemUtil;
import cn.refinex.common.protection.ratelimiter.core.annotation.RateLimiter;
import cn.refinex.common.protection.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import cn.refinex.common.utils.security.CryptoUtils;
import org.aspectj.lang.JoinPoint;

/**
 * 服务器节点限流键解析器
 * <p>
 * 键名：方法名 + 方法参数 + IP
 * 说明：为了避免 Key 过长，使用 MD5 对键名进行压缩
 *
 * @author Refinex
 * @since 1.0.0
 */
public class ServerNodeRateLimiterKeyResolver implements RateLimiterKeyResolver {

    /**
     * 解析限流键
     *
     * @param joinPoint   连接点
     * @param rateLimiter 限流注解
     * @return 限流键
     */
    @Override
    public String resolver(JoinPoint joinPoint, RateLimiter rateLimiter) {
        String methodName = joinPoint.getSignature().toString();
        String argsStr = StrUtil.join(",", joinPoint.getArgs());
        String serverNode = String.format("%s@%d", SystemUtil.getHostInfo().getAddress(), SystemUtil.getCurrentPID());
        return CryptoUtils.md5Hex(methodName + argsStr + serverNode);
    }
}
