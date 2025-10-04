package cn.refinex.common.security.config;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token JWT Simple 模式配置
 * <p>
 * JWT Simple 模式特点：
 * 1. Token 使用 JWT 格式（无状态，可携带少量数据）
 * 2. Session 数据存储在 Redis（有状态，支持完整的 Session 管理）
 * 3. 支持完整的 Session 管理能力（踢人下线、权限刷新等）
 * 4. 兼顾 JWT 的便捷性和 Session 的灵活性
 * </p>
 * <p>
 * 与 JWT Stateless 模式的区别：
 * - Simple 模式：Token 是 JWT，但 Session 在 Redis，支持踢人下线
 * - Stateless 模式：Token 是 JWT，Session 也在 JWT 中，完全无状态，不支持踢人下线
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class SaTokenJwtConfiguration {

    /**
     * 配置 Sa-Token 的 JWT Simple 模式
     * <p>
     * 使用 StpLogicJwtForSimple 替代默认的 StpLogic，
     * 使 Token 以 JWT 格式生成，同时保留 Redis Session 的完整功能。
     * </p>
     *
     * @return StpLogic 实例
     */
    @Bean
    public StpLogic stpLogic() {
        log.info("配置 Sa-Token JWT Simple 模式");
        return new StpLogicJwtForSimple();
    }
}

