package cn.refinex.auth.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.refinex.auth.domain.dto.request.LoginRequest;
import cn.refinex.auth.domain.dto.response.LoginResponse;
import cn.refinex.auth.service.AuthService;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.utils.servlet.ServletUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     *
     * @param request     登录请求
     * @param httpRequest HTTP 请求对象
     * @return 登录响应（包含 Token）
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录，返回 JWT Token")
    public ApiResult<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        String clientIp = ServletUtils.getClientIp(httpRequest);
        LoginResponse response = authService.login(request, clientIp, httpRequest);
        return ApiResult.success(response);
    }

    /**
     * 用户登出
     *
     * @return 成功响应
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出登录，清除 Token")
    @SaCheckLogin
    public ApiResult<Void> logout() {
        authService.logout();
        return ApiResult.success(null);
    }

    /**
     * 刷新 Token
     *
     * @return 新的 Token
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新 Token", description = "刷新访问令牌，延长有效期")
    @SaCheckLogin
    public ApiResult<String> refreshToken() {
        String newToken = authService.refreshToken();
        return ApiResult.success(newToken);
    }
}

