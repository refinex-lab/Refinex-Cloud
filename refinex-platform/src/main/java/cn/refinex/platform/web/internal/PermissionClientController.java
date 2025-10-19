package cn.refinex.platform.web.internal;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 权限 Feign API 实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@SaIgnore // 内部接口，不需要用户令牌
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionClientController {

    private final PermissionService permissionService;

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 用户角色列表
     */
    @PostMapping("/getUserRolePermissions")
    @Operation(summary = "获取用户角色权限列表", description = "根据用户ID获取用户拥有的所有角色权限")
    @Parameter(name = "userId", description = "用户ID", required = true)
    public ApiResult<Set<String>> getUserRolePermissions(@RequestParam("userId") Long userId) {
        return ApiResult.success(permissionService.getUserRolePermissions(userId));
    }

    /**
     * 获取用户权限列表
     *
     * @param userId 用户ID
     * @return 用户权限列表
     */
    @PostMapping("/getUserMenuPermissions")
    @Operation(summary = "获取用户菜单权限列表", description = "根据用户ID获取用户拥有的所有菜单权限")
    @Parameter(name = "userId", description = "用户ID", required = true)
    public ApiResult<Set<String>> getUserMenuPermissions(@RequestParam("userId") Long userId) {
        return ApiResult.success(permissionService.getUserMenuPermissions(userId));
    }
}
