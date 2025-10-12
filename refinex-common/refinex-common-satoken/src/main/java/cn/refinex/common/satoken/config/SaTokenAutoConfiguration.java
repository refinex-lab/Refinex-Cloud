package cn.refinex.common.satoken.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.refinex.common.factory.YmlPropertySourceFactory;
import cn.refinex.common.redis.RedisService;
import cn.refinex.common.redis.config.RefinexRedisAutoConfiguration;
import cn.refinex.common.satoken.core.dao.EnhancedSaTokenDao;
import cn.refinex.common.satoken.core.handler.SaTokenExceptionHandler;
import cn.refinex.common.satoken.core.service.StpInterfaceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

/**
 * Sa-Token 自动配置类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration(after = RefinexRedisAutoConfiguration.class)
@PropertySource(value = "classpath:common-satoken.yml", factory = YmlPropertySourceFactory.class) // 加载 Sa-Token 内置配置文件
public class SaTokenAutoConfiguration {

    /**
     * 配置 Sa-Token 集成 JWT 认证的 StpLogic Bean
     *
     * @return StpLogicJwtForSimple 实例
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

    /**
     * 配置 Sa-Token 自定义权限验证接口 Bean
     *
     * @return StpInterfaceImpl 实例
     */
    @Bean
    public StpInterface stpInterface() {
        return new StpInterfaceImpl();
    }

    /**
     * 配置 Sa-Token 自定义 DAO 实现 Bean，用于增强 Sa-Token 的 Redis 存储功能
     *
     * @param redisService Redis 服务 Bean
     * @return EnhancedSaTokenDao 实例
     */
    @Bean
    public SaTokenDao saTokenDao(RedisService redisService) {
        return new EnhancedSaTokenDao(redisService);
    }

    /**
     * 配置 Sa-Token 异常处理器 Bean，用于处理认证异常
     *
     * @return SaTokenExceptionHandler 实例
     */
    @Bean
    public SaTokenExceptionHandler saTokenExceptionHandler() {
        return new SaTokenExceptionHandler();
    }
}

