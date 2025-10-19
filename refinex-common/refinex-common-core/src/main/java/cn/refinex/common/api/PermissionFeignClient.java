package cn.refinex.common.api;

import cn.refinex.common.constants.SystemFeignConstants;
import cn.refinex.common.domain.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

/**
 * 权限服务 Feign 客户端
 *
 * @author Refinex
 * @since 1.0.0
 */
@Tag(name = "权限服务 Feign API")
@FeignClient(name = SystemFeignConstants.PLATFORM_SERVICE)
public interface PermissionFeignClient {

    @PostMapping("/permission/getUserRolePermissions")
    @Operation(summary = "获取用户角色权限列表", description = "根据用户ID获取用户拥有的所有角色权限")
    @Parameter(name = "userId", description = "用户ID", required = true)
    ApiResult<Set<String>> getUserRolePermissions(@RequestParam("userId") Long userId);

    @PostMapping("/permission/getUserMenuPermissions")
    @Operation(summary = "获取用户菜单权限列表", description = "根据用户ID获取用户拥有的所有菜单权限")
    @Parameter(name = "userId", description = "用户ID", required = true)
    ApiResult<Set<String>> getUserMenuPermissions(@RequestParam("userId") Long userId);
}
