package cn.refinex.platform.controller;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.platform.domain.dto.request.AssignRolePermissionsRequest;
import cn.refinex.platform.service.SysRolePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统权限管理控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Tag(name = "角色权限管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/role-permission")
public class SysRolePermissionController {

    private final SysRolePermissionService sysRolePermissionService;

    @Operation(summary = "分配角色权限")
    @PostMapping("/assign/{roleId}")
    public ApiResult<Boolean> assignPermissions(@PathVariable Long roleId, @Valid @RequestBody AssignRolePermissionsRequest request) {
        return ApiResult.success(sysRolePermissionService.assignPermissions(roleId, request.getPermissionIds(), LoginHelper.getUserId()));
    }

    @Operation(summary = "查询角色权限ID列表")
    @GetMapping("/list/{roleId}")
    public ApiResult<List<Long>> listPermissions(@PathVariable Long roleId) {
        return ApiResult.success(sysRolePermissionService.listPermissionIds(roleId));
    }
}


