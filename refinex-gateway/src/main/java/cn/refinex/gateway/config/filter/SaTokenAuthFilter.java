package cn.refinex.gateway.config.filter;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.common.utils.spring.SpringUtils;
import cn.refinex.gateway.config.propertirs.IgnoreWhiteProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.List;

/**
 * Sa-Token 权限认证拦截器
 *
 * @author Lion Li
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class SaTokenAuthFilter {

    /**
     * 注册 Sa-Token 全局过滤器进行鉴权操作
     *
     * @return SaReactorFilter 实例
     */
    @Bean
    public SaReactorFilter getSaReactorFilter(IgnoreWhiteProperties ignoreWhiteProperties) {
        // 拦截所有路径
        SaReactorFilter filter = new SaReactorFilter().addInclude("/**");

        // 添加白名单路径
        List<String> whitelistPaths = ignoreWhiteProperties.getWhites();
        if (CollectionUtils.isNotEmpty(whitelistPaths)) {
            log.info("Sa-Token 网关白名单路径：{}", whitelistPaths);
            whitelistPaths.forEach(filter::addExclude);
        }

        // 认证函数：每次请求执行
        filter.setAuth(obj -> {
            log.info("Sa-Token 网关鉴权 URL：{}", SaHolder.getRequest().getUrl());

            // 使用 SaRouter 进行路由级别的权限控制, 不在白名单路径中的请求均需要登录
            SaRouter.match("/**")
                    .notMatch(whitelistPaths)
                    .check(r -> {
                        // 检查登录状态
                        StpUtil.checkLogin();

                        // 获取当前请求对象
                        ServerHttpRequest request = SaReactorSyncHolder.getExchange().getRequest();

                        // 检查 Header 和 Param 里的 clientid 与 Token 里的是否一致
                        String headerCid = request.getHeaders().getFirst(LoginHelper.CLIENT_KEY);
                        String paramCid = request.getQueryParams().getFirst(LoginHelper.CLIENT_KEY);
                        String clientId = StpUtil.getExtra(LoginHelper.CLIENT_KEY).toString();

                        // 如果三者任意一个 clientid 不匹配则 Token 无效
                        if (!StringUtils.equalsAny(clientId, headerCid, paramCid)) {
                            throw NotLoginException.newInstance(
                                    StpUtil.getLoginType(),
                                    "-100",
                                    "客户端ID与Token不匹配",
                                    StpUtil.getTokenValue()
                            );
                        }
                    });
        });

        // 异常处理函数：每次 setAuth 函数出现异常时进入
        filter.setError(e -> {
            log.error("Sa-Token 网关鉴权异常：{}", e.getMessage());

            // 如果是未登录异常, 则返回未登录错信息
            if (e instanceof NotLoginException) {
                return SaResult.error(e.getMessage()).setCode(HttpStatus.UNAUTHORIZED.value());
            }
            // 其他异常, 返回通用错误信息
            return SaResult.error("认证失败，无法访问系统资源").setCode(HttpStatus.UNAUTHORIZED.value());
        });

        return filter;
    }

    /**
     * 注册 Sa-Token 全局过滤器进行 actuator 路径的鉴权操作
     *
     * @return SaReactorFilter 实例
     */
    @Bean
    public SaReactorFilter actuatorFilter() {
        // 从 Spring 环境中获取 actuator 用户名和密码, 用户名和密码通过元数据存储在 nacos 中
        String username = SpringUtils.getProperty("spring.cloud.nacos.discovery.metadata.username");
        String password = SpringUtils.getProperty("spring.cloud.nacos.discovery.metadata.userpassword");

        return new SaReactorFilter()
                // 过滤 actuator 路径
                .addInclude("/actuator", "/actuator/**")
                // 自定义认证规则：检查 HttpBasic 认证是否正确
                .setAuth(obj -> SaHttpBasicUtil.check(username + ":" + password))
                // 自定义错误规则
                .setError(e -> SaResult.error(e.getMessage()).setCode(HttpStatus.UNAUTHORIZED.value()));
    }
}

