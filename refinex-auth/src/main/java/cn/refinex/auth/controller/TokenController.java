package cn.refinex.auth.controller;

import cn.refinex.auth.domain.dto.request.LoginRequest;
import cn.refinex.auth.domain.vo.LoginVo;
import cn.refinex.auth.enums.EmailVerifyCodeType;
import cn.refinex.auth.service.AuthService;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.domain.model.LoginUser;
import cn.refinex.common.mail.domain.dto.VerifyCodeRequest;
import cn.refinex.common.mail.domain.dto.VerifyCodeResult;
import cn.refinex.common.mail.domain.dto.VerifyCodeValidateRequest;
import cn.refinex.common.protection.ratelimiter.core.annotation.RateLimiter;
import cn.refinex.common.protection.ratelimiter.core.keyresolver.impl.ClientIpRateLimiterKeyResolver;
import cn.refinex.common.utils.servlet.ServletUtils;
import cn.refinex.platform.api.EmailFeignClient;
import cn.refinex.platform.api.UserFeignClient;
import cn.refinex.platform.domain.dto.request.ResetPasswordRequest;
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
    private final UserFeignClient userFeignClient;
    private final EmailFeignClient emailFeignClient;

    @PostMapping("/register")
    @Operation(summary = "注册用户", description = "根据创建用户请求参数注册用户")
    @Parameter(name = "request", description = "创建用户请求参数", required = true)
    @RateLimiter(time = 10, timeUnit = TimeUnit.MINUTES, count = 5, keyResolver = ClientIpRateLimiterKeyResolver.class)
    ApiResult<Boolean> registerUser(UserCreateRequest request) {
        return userFeignClient.registerUser(request);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录，返回 JWT Token")
    @Parameter(name = "request", description = "登录请求参数", required = true)
    public ApiResult<LoginVo> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String clientIp = ServletUtils.getClientIp(httpRequest);
        return ApiResult.success(authService.login(request, clientIp, httpRequest));
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出登录，清除 Token")
    public ApiResult<Void> logout() {
        authService.logout();
        return ApiResult.success(null);
    }

    @PostMapping("/sendEmailVerifyCode")
    @Operation(summary = "发送邮箱验证码", description = "根据邮箱发送验证码")
    @Parameter(name = "request", description = "发送验证码请求参数", required = true)
    public ApiResult<VerifyCodeResult> sendEmailVerifyCode(@Valid @RequestBody VerifyCodeRequest request, HttpServletRequest httpRequest) {
        // 验证用户存在性
        ApiResult<LoginUser> checkUserResult = userFeignClient.getLoginUserByEmail(request.getEmail());
        if (!checkUserResult.isSuccess()) {
            return ApiResult.failure(checkUserResult.getCode(), checkUserResult.getMessage());
        }

        // 发送验证码
        request.setClientIp(ServletUtils.getClientIp(httpRequest));
        return emailFeignClient.sendVerifyCode(request);
    }

    @PostMapping("/resetPassword")
    @Operation(summary = "重置密码", description = "根据邮箱验证码重置密码")
    @Parameter(name = "request", description = "重置密码请求参数", required = true)
    public ApiResult<Boolean> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        // 验证用户存在性
        ApiResult<LoginUser> checkUserResult = userFeignClient.getLoginUserByEmail(request.getEmail());
        if (!checkUserResult.isSuccess()) {
            return ApiResult.failure(checkUserResult.getCode(), checkUserResult.getMessage());
        }

        // 验证验证码
        VerifyCodeValidateRequest verifyCodeValidateRequest = VerifyCodeValidateRequest.builder()
                .email(request.getEmail())
                .verifyCode(request.getEmailCode())
                .codeType(EmailVerifyCodeType.RESET_PASSWORD.getCode())
                .build();
        ApiResult<Boolean> verifyCodeResult = emailFeignClient.verifyCode(verifyCodeValidateRequest);
        if (verifyCodeResult.isSuccess() && Boolean.TRUE.equals(!verifyCodeResult.getData())) {
            // 重置密码
            return userFeignClient.resetPassword(request);
        }

        return ApiResult.failure(verifyCodeResult.getCode(), verifyCodeResult.getMessage());
    }
}

