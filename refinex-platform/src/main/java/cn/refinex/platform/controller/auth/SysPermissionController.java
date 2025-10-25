package cn.refinex.platform.controller.auth;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.controller.auth.dto.request.SysPermissionCreateRequestDTO;
import cn.refinex.platform.controller.auth.dto.request.SysPermissionQueryRequestDTO;
import cn.refinex.platform.controller.auth.dto.request.SysPermissionUpdateRequestDTO;
import cn.refinex.platform.controller.auth.dto.response.SysPermissionResponseDTO;
import cn.refinex.platform.entity.sys.SysPermission;
import cn.refinex.platform.service.SysPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统权限管理控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/permissions")
@Tag(name = "系统权限管理", description = "系统权限的增删改查、状态管理等接口")
public class SysPermissionController {

    private final SysPermissionService sysPermissionService;

    @PostMapping
    @Operation(summary = "创建权限", description = "创建新的系统权限")
    @Parameter(name = "request", description = "权限创建请求", required = true)
    public ApiResult<Long> createPermission(@Valid @RequestBody SysPermissionCreateRequestDTO request) {
        Long permissionId = sysPermissionService.create(request, LoginHelper.getUserId());
        return ApiResult.success(HttpStatusCode.CREATED, permissionId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新权限", description = "更新指定权限的信息")
    @Parameter(name = "id", description = "权限 ID", required = true)
    @Parameter(name = "request", description = "权限更新请求", required = true)
    public ApiResult<Boolean> updatePermission(@PathVariable Long id, @Valid @RequestBody SysPermissionUpdateRequestDTO request) {
        Boolean result = sysPermissionService.update(id, request, LoginHelper.getUserId());
        return ApiResult.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限", description = "删除指定的系统权限")
    @Parameter(name = "id", description = "权限 ID", required = true)
    public ApiResult<Void> deletePermission(@PathVariable Long id) {
        sysPermissionService.delete(id, LoginHelper.getUserId());
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "更新权限状态", description = "启用或禁用指定权限")
    @Parameter(name = "id", description = "权限 ID", required = true)
    @Parameter(name = "status", description = "状态值", required = true)
    public ApiResult<Boolean> updatePermissionStatus(@PathVariable Long id, @RequestParam Integer status) {
        Boolean result = sysPermissionService.updateStatus(id, status, LoginHelper.getUserId());
        return ApiResult.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取权限详情", description = "根据 ID 获取权限详细信息")
    @Parameter(name = "id", description = "权限 ID", required = true)
    public ApiResult<SysPermissionResponseDTO> getPermission(@PathVariable Long id) {
        SysPermission entity = sysPermissionService.getById(id);
        return ApiResult.success(BeanConverter.toBean(entity, SysPermissionResponseDTO.class));
    }

    @PostMapping("/search")
    @Operation(summary = "分页查询权限", description = "根据条件分页查询系统权限")
    @Parameter(name = "query", description = "查询条件", required = true)
    @Parameter(name = "pageNum", description = "页码", required = false)
    @Parameter(name = "pageSize", description = "每页数量", required = false)
    public ApiResult<PageResult<SysPermissionResponseDTO>> searchPermissions(
            @RequestBody SysPermissionQueryRequestDTO query,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<SysPermission> result = sysPermissionService.page(query, new PageRequest(pageNum, pageSize));
        PageResult<SysPermissionResponseDTO> mapped = new PageResult<>(
                BeanConverter.copyToList(result.getRecords(), SysPermissionResponseDTO.class),
                result.getTotal(),
                result.getPageNum(),
                result.getPageSize()
        );
        return ApiResult.success(mapped);
    }
}


