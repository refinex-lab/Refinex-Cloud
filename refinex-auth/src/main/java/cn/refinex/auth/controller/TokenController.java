package cn.refinex.auth.controller;

import cn.refinex.auth.domain.dto.request.LoginRequest;
import cn.refinex.auth.domain.vo.LoginVo;
import cn.refinex.auth.service.AuthService;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.protection.ratelimiter.core.annotation.RateLimiter;
import cn.refinex.common.protection.ratelimiter.core.keyresolver.impl.ClientIpRateLimiterKeyResolver;
import cn.refinex.common.utils.servlet.ServletUtils;
import cn.refinex.platform.api.UserFeignClient;
import cn.refinex.platform.domain.dto.request.UserCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 认证控制器
 * <p>
 * 提供用户登录、登出、Token 管理等接口
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录、登出、Token 管理")
public class TokenController {

    private final AuthService authService;
    private final UserFeignClient userClient;

    @PostMapping("/register")
    @Operation(summary = "注册用户", description = "根据创建用户请求参数注册用户")
    @Parameter(name = "request", description = "创建用户请求参数", required = true)
    @RateLimiter(time = 10, timeUnit = TimeUnit.MINUTES, count = 5, keyResolver = ClientIpRateLimiterKeyResolver.class)
    ApiResult<Boolean> registerUser(UserCreateRequest request) {
        return userClient.registerUser(request);
    }

    /**
     * 用户登录
     *
     * @param request     登录请求
     * @param httpRequest HTTP 请求对象
     * @return 登录响应
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录，返回 JWT Token")
    public ApiResult<LoginVo> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String clientIp = ServletUtils.getClientIp(httpRequest);
        return ApiResult.success(authService.login(request, clientIp, httpRequest));
    }

    /**
     * 用户登出
     *
     * @return 成功响应
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出登录，清除 Token")
    public ApiResult<Void> logout() {
        authService.logout();
        return ApiResult.success(null);
    }
}

