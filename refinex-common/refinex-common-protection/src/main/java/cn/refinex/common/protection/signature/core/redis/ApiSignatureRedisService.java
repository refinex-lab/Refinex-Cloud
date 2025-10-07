package cn.refinex.common.protection.signature.core.redis;

import cn.refinex.common.redis.RedisService;
import lombok.AllArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * 接口签名 Redis 服务
 *
 * @author Zhougang
 * @author Refinex
 * @since 1.0.0
 */
@AllArgsConstructor
public class ApiSignatureRedisService {

    private final RedisService redisService;

    /**
     * 验签随机数
     * <p>
     * KEY 格式：api_signature_nonce:%s:%s (参数为 appId 随机数)
     * VALUE 格式：String
     * EXPIRE 时间：不固定
     */
    private static final String SIGNATURE_NONCE = "api_signature_nonce:%s:%s";

    /**
     * 签名密钥
     * <p>
     * HASH 数据结构：
     * KEY 格式：%s (参数为 appId)
     * VALUE 格式：String
     * EXPIRE 时间：不固定 (预加载到 Redis)4
     */
    private static final String SIGNATURE_APPID = "api_signature_app";

    // ==================== 验签随机数 ====================

    /**
     * 获取随机数
     *
     * @param appId 应用ID
     * @param nonce 随机数
     * @return 随机数
     */
    public String getNonce(String appId, String nonce) {
        return redisService.string().get(String.format(SIGNATURE_NONCE, appId, nonce), String.class);
    }

    /**
     * 设置随机数
     *
     * @param appId    应用ID
     * @param nonce    随机数
     * @param time     过期时间
     * @param timeUnit 过期时间单位
     * @return 是否设置成功
     */
    public Boolean setNonce(String appId, String nonce, int time, TimeUnit timeUnit) {
        return redisService.string().setIfAbsent(String.format(SIGNATURE_NONCE, appId, nonce), "", time, timeUnit);
    }

    // ==================== 签名密钥 ====================

    /**
     * 获取应用密钥
     *
     * @param appId 应用ID
     * @return 应用密钥
     */
    public String getAppSecret(String appId) {
        return redisService.hash().get(SIGNATURE_APPID, appId, String.class);
    }

}
