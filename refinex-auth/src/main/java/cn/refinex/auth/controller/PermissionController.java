package cn.refinex.auth.controller;

import cn.refinex.auth.client.permission.PermissionClient;
import cn.refinex.auth.service.AuthService;
import cn.refinex.common.domain.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证服务 Feign API 实现
 *
 * @author Refinex
 * @since 2025-10-05
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class PermissionController implements PermissionClient {

    private final AuthService authService;

    /**
     * 验证用户权限
     *
     * @param permission 权限码
     * @return 是否拥有权限
     */
    @Override
    public ApiResult<Boolean> checkPermission(String permission) {
        boolean hasPermission = authService.checkPermission(permission);
        return ApiResult.success(hasPermission);
    }
}

