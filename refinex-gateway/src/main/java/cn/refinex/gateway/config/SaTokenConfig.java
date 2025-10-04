package cn.refinex.gateway.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.refinex.gateway.properties.GatewaySecurityProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Sa-Token 网关配置
 * <p>
 * 说明：
 * 1. 配置全局过滤器 SaReactorFilter
 * 2. 配置白名单路径（不需要认证）
 * 3. 配置路由级别的权限控制
 * 4. 配置跨域响应头
 * 5. 配置异常处理
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SaTokenConfig {

    private final GatewaySecurityProperties securityProperties;

    /**
     * 注册 Sa-Token 全局过滤器
     * <p>
     * 说明：
     * 1. 拦截所有路径
     * 2. 白名单路径不需要认证
     * 3. 其他路径需要登录
     * 4. 特定路径需要权限或角色
     * </p>
     *
     * @return SaReactorFilter 实例
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        log.info("配置 Sa-Token 网关过滤器");

        // 拦截所有路径
        SaReactorFilter filter = new SaReactorFilter().addInclude("/**");

        // 添加白名单路径
        List<String> whitelistPaths = securityProperties.getWhitelistPaths();
        if (CollectionUtils.isNotEmpty(whitelistPaths)) {
            whitelistPaths.forEach(path -> {
                filter.addExclude(path);
                log.info("添加白名单路径：{}", path);
            });
        }

        // 认证函数：每次请求执行
        filter.setAuth(obj -> {
            if (Boolean.TRUE.equals(securityProperties.getLogEnabled())) {
                log.debug("Sa-Token 网关鉴权：{}", SaHolder.getRequest().getUrl());
            }

            // 使用 SaRouter 进行路由级别的权限控制
            SaRouter.match("/**")
                    .notMatch(whitelistPaths)
                    .check(r -> StpUtil.checkLogin());

            // 管理员接口权限控制
            SaRouter.match("/platform/admin/**")
                    .check(r -> StpUtil.checkRole("ROLE_ADMIN"));

            // 用户接口权限控制
            SaRouter.match("/platform/user/**")
                    .check(r -> StpUtil.checkPermission("user:view"));
        });

        // 异常处理函数：认证失败时执行
        filter.setError(e -> {
            log.error("Sa-Token 网关鉴权异常：{}", e.getMessage());
            return SaResult.error(e.getMessage());
        });

        // 前置函数：在认证函数之前执行
        filter.setBeforeAuth(obj -> {
            // 设置跨域响应头
            SaHolder.getResponse()
                    .setHeader("Access-Control-Allow-Origin", "*")
                    .setHeader("Access-Control-Allow-Methods", "*")
                    .setHeader("Access-Control-Allow-Headers", "*")
                    .setHeader("Access-Control-Max-Age", "3600");

            // OPTIONS 预检请求直接返回
            if ("OPTIONS".equals(SaHolder.getRequest().getMethod())) {
                SaRouter.back();
            }
        });

        log.info("Sa-Token 网关过滤器配置完成");
        return filter;
    }
}

