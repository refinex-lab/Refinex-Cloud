package cn.refinex.common.protection.signature.config;

import cn.refinex.common.protection.signature.core.aop.ApiSignatureAspect;
import cn.refinex.common.protection.signature.core.redis.ApiSignatureRedisService;
import cn.refinex.common.redis.RedisService;
import cn.refinex.common.redis.config.RefinexRedisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 接口签名自动配置类
 *
 * @author Zhougang
 * @author Refinex
 * @since 1.0.0
 */
@AutoConfiguration(after = RefinexRedisAutoConfiguration.class)
public class ApiSignatureAutoConfiguration {

    /**
     * 创建 ApiSignatureAspect Bean，用于接口签名验证
     *
     * @param apiSignatureRedisService 接口签名 Redis 服务
     * @return ApiSignatureAspect 实例
     */
    @Bean
    public ApiSignatureAspect signatureAspect(ApiSignatureRedisService apiSignatureRedisService) {
        return new ApiSignatureAspect(apiSignatureRedisService);
    }

    /**
     * 创建 ApiSignatureRedisService Bean，用于操作接口签名相关的 Redis 数据
     *
     * @param redisService Redis 服务
     * @return ApiSignatureRedisService 实例
     */
    @Bean
    public ApiSignatureRedisService signatureRedisDAO(RedisService redisService) {
        return new ApiSignatureRedisService(redisService);
    }
}
