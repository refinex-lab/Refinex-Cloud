package cn.refinex.platform.web;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.api.PermissionFeignClient;
import cn.refinex.platform.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 权限 Feign API 实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
public class PermissionFeignClientImpl implements PermissionFeignClient {

    private final PermissionService permissionService;

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 用户角色列表
     */
    @Override
    public ApiResult<Set<String>> getUserRolePermissions(Long userId) {
        return ApiResult.success(permissionService.getUserRolePermissions(userId));
    }

    /**
     * 获取用户权限列表
     *
     * @param userId 用户ID
     * @return 用户权限列表
     */
    @Override
    public ApiResult<Set<String>> getUserMenuPermissions(Long userId) {
        return ApiResult.success(permissionService.getUserMenuPermissions(userId));
    }
}
