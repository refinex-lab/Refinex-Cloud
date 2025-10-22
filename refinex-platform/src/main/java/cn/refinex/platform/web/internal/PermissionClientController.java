package cn.refinex.platform.web.internal;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.refinex.common.api.PermissionFeignClient;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 权限服务内部接口实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@SaIgnore // 内部接口，不需要用户令牌
@RestController
@RequiredArgsConstructor
public class PermissionClientController implements PermissionFeignClient {

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
     * 获取用户菜单权限列表
     *
     * @param userId 用户ID
     * @return 用户菜单权限列表
     */
    @Override
    public ApiResult<Set<String>> getUserMenuPermissions(Long userId) {
        return ApiResult.success(permissionService.getUserMenuPermissions(userId));
    }
}
