package cn.refinex.common.satoken.config;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import cn.refinex.common.redis.config.RefinexRedisAutoConfiguration;
import cn.refinex.common.satoken.core.handler.SaTokenExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Sa-Token 自动配置类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration(after = RefinexRedisAutoConfiguration.class)
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
     * 配置 Sa-Token 异常处理器 Bean，用于处理认证异常
     *
     * @return SaTokenExceptionHandler 实例
     */
    @Bean
    public SaTokenExceptionHandler saTokenExceptionHandler() {
        return new SaTokenExceptionHandler();
    }
}

