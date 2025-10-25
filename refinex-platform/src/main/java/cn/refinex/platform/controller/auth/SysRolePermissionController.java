package cn.refinex.platform.controller.auth;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.platform.controller.auth.dto.request.AssignRolePermissionsRequestDTO;
import cn.refinex.platform.service.SysRolePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色权限管理控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/roles")
@Tag(name = "角色权限管理", description = "角色权限分配和查询等接口")
public class SysRolePermissionController {

    private final SysRolePermissionService sysRolePermissionService;

    @PutMapping("/{roleId}/permissions")
    @Operation(summary = "分配角色权限", description = "为指定角色分配权限列表")
    @Parameter(name = "roleId", description = "角色 ID", required = true)
    @Parameter(name = "request", description = "权限 ID 列表", required = true)
    public ApiResult<Boolean> assignRolePermissions(
            @PathVariable Long roleId,
            @Valid @RequestBody AssignRolePermissionsRequestDTO request) {
        Boolean result = sysRolePermissionService.assignPermissions(
                roleId,
                request.getPermissionIds(),
                LoginHelper.getUserId()
        );
        return ApiResult.success(result);
    }

    @GetMapping("/{roleId}/permissions")
    @Operation(summary = "获取角色权限列表", description = "查询指定角色拥有的所有权限 ID")
    @Parameter(name = "roleId", description = "角色 ID", required = true)
    public ApiResult<List<Long>> getRolePermissions(@PathVariable Long roleId) {
        List<Long> permissionIds = sysRolePermissionService.listPermissionIds(roleId);
        return ApiResult.success(permissionIds);
    }
}


