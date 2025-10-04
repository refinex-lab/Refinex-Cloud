package cn.refinex.auth.api;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.refinex.api.auth.domain.dto.UserInfoDTO;
import cn.refinex.auth.service.AuthService;
import cn.refinex.common.domain.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Feign - 认证服务 API 控制器
 *
 * @author Refinex
 * @since 2025-10-05
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证服务 API", description = "提供给其他微服务的远程调用接口")
public class AuthApiController {

    private final AuthService authService;

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/userinfo")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息（服务间调用）")
    @SaCheckLogin
    public ApiResult<UserInfoDTO> getUserInfo() {
        UserInfoDTO userInfo = authService.getCurrentUserInfo();
        return ApiResult.success(userInfo);
    }

    /**
     * 验证用户权限
     *
     * @param permission 权限码
     * @return 是否拥有权限
     */
    @GetMapping("/check-permission")
    @Operation(summary = "验证用户权限", description = "检查当前用户是否拥有指定权限（服务间调用）")
    @SaCheckLogin
    public ApiResult<Boolean> checkPermission(@RequestParam("permission") String permission) {
        boolean hasPermission = authService.checkPermission(permission);
        return ApiResult.success(hasPermission);
    }
}

