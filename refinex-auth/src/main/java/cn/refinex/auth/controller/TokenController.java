package cn.refinex.auth.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.refinex.auth.client.PlatformEmailServiceClient;
import cn.refinex.auth.client.PlatformUserServiceClient;
import cn.refinex.auth.domain.dto.request.*;
import cn.refinex.auth.domain.dto.response.EmailVerifyCodeResponseDTO;
import cn.refinex.auth.domain.vo.LoginVo;
import cn.refinex.auth.enums.EmailVerifyCodeType;
import cn.refinex.auth.service.AuthService;
import cn.refinex.common.apilog.core.annotation.LogOperation;
import cn.refinex.common.apilog.core.enums.OperateTypeEnum;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.domain.model.LoginUser;
import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.protection.ratelimiter.core.annotation.RateLimiter;
import cn.refinex.common.protection.ratelimiter.core.keyresolver.impl.ClientIpRateLimiterKeyResolver;
import cn.refinex.common.utils.servlet.ServletUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * 认证控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@SaIgnore
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户注册、登录、登出、密码重置等认证接口")
public class TokenController {

    private final AuthService authService;
    private final PlatformUserServiceClient platformUserServiceClient;
    private final PlatformEmailServiceClient platformEmailServiceClient;

    @PostMapping("/register")
    @RateLimiter(
            time = 10,
            timeUnit = TimeUnit.MINUTES,
            count = 5,
            keyResolver = ClientIpRateLimiterKeyResolver.class,
            message = "注册频率过快，请稍后重试"
    )
    @LogOperation(sensitiveParams = {"password", "confirmPassword", "email", "mobile"}, operationType = OperateTypeEnum.CREATE)
    @Operation(summary = "用户注册", description = "创建新用户账号")
    @Parameter(name = "request", description = "用户注册请求参数", required = true)
    public ApiResult<Boolean> registerUser(@Valid @RequestBody UserCreateRequestDTO request) {
        return platformUserServiceClient.registerUser(request);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录，返回访问令牌")
    @Parameter(name = "request", description = "用户登录请求参数", required = true)
    public ApiResult<LoginVo> login(@Valid @RequestBody LoginRequest request) {
        LoginVo loginVo = authService.login(request);
        return ApiResult.success(loginVo);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出登录，清除访问令牌")
    public ApiResult<Void> logout() {
        authService.logout();
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @PostMapping("/verify-codes/email")
    @RateLimiter(
            time = 10,
            timeUnit = TimeUnit.MINUTES,
            count = 5,
            keyResolver = ClientIpRateLimiterKeyResolver.class,
            message = "发送验证码频率过快，请稍后重试"
    )
    @LogOperation(sensitiveParams = {"email"}, operationType = OperateTypeEnum.OTHER)
    @Operation(summary = "发送邮箱验证码", description = "向指定邮箱发送验证码")
    @Parameter(name = "request", description = "验证码请求参数", required = true)
    public ApiResult<EmailVerifyCodeResponseDTO> sendEmailVerifyCode(@Valid @RequestBody EmailVerifyCodeRequestDTO request) {
        request.setClientIp(ServletUtils.getClientIp());
        return platformEmailServiceClient.sendEmailVerifyCode(request);
    }

    @PutMapping("/password/reset")
    @LogOperation(sensitiveParams = {"email", "newPassword", "confirmPassword"}, operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "重置密码", description = "根据邮箱验证码重置用户密码")
    @Parameter(name = "request", description = "重置密码请求参数", required = true)
    public ApiResult<Boolean> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        // 验证用户存在性
        ApiResult<LoginUser> checkUserResult = platformUserServiceClient.getLoginUserByEmail(request.getEmail());
        if (!checkUserResult.isSuccess()) {
            throw new BusinessException(HttpStatusCode.NOT_FOUND, "用户不存在");
        }

        // 验证验证码
        EmailVerifyCodeValidateRequestDTO verifyCodeValidateRequest = EmailVerifyCodeValidateRequestDTO.builder()
                .email(request.getEmail())
                .verifyCode(request.getEmailCode())
                .codeType(EmailVerifyCodeType.RESET_PASSWORD.getCode())
                .build();
        ApiResult<Boolean> verifyCodeResult = platformEmailServiceClient.verifyEmailCode(verifyCodeValidateRequest);
        
        if (!verifyCodeResult.isSuccess() || !Boolean.TRUE.equals(verifyCodeResult.data())) {
            throw new BusinessException(HttpStatusCode.BAD_REQUEST, "验证码无效或已过期");
        }

        // 重置密码
        return platformUserServiceClient.resetUserPassword(request);
    }
}

