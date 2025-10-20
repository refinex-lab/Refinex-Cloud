package cn.refinex.platform.controller;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.platform.domain.dto.request.SysPermissionCreateRequest;
import cn.refinex.platform.domain.dto.request.SysPermissionQueryRequest;
import cn.refinex.platform.domain.dto.request.SysPermissionUpdateRequest;
import cn.refinex.platform.domain.entity.sys.SysPermission;
import cn.refinex.platform.domain.dto.response.SysPermissionResponse;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.service.SysPermissionService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "系统权限管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/permission")
public class SysPermissionController {

    private final SysPermissionService sysPermissionService;

    @Operation(summary = "创建权限")
    @PostMapping("/create")
    public ApiResult<Long> create(@Valid @RequestBody SysPermissionCreateRequest request) {
        return ApiResult.success(sysPermissionService.create(request, LoginHelper.getUserId()));
    }

    @Operation(summary = "更新权限")
    @PostMapping("/update")
    public ApiResult<Boolean> update(@Valid @RequestBody SysPermissionUpdateRequest request) {
        return ApiResult.success(sysPermissionService.update(request, LoginHelper.getUserId()));
    }

    @Operation(summary = "删除权限")
    @PostMapping("/delete/{id}")
    public ApiResult<Boolean> delete(@PathVariable Long id) {
        return ApiResult.success(sysPermissionService.delete(id, LoginHelper.getUserId()));
    }

    @Operation(summary = "更新状态")
    @PostMapping("/status/{id}/{status}")
    public ApiResult<Boolean> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        return ApiResult.success(sysPermissionService.updateStatus(id, status, LoginHelper.getUserId()));
    }

    @Operation(summary = "权限详情")
    @GetMapping("/detail/{id}")
    public ApiResult<SysPermissionResponse> detail(@PathVariable Long id) {
        SysPermission entity = sysPermissionService.getById(id);
        return ApiResult.success(BeanConverter.toBean(entity, SysPermissionResponse.class));
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public ApiResult<PageResult<SysPermissionResponse>> page(@RequestBody SysPermissionQueryRequest query,
                                                          @RequestParam(defaultValue = "1") int pageNum,
                                                          @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<SysPermission> result = sysPermissionService.page(query, new PageRequest(pageNum, pageSize));
        PageResult<SysPermissionResponse> mapped = new PageResult<>(
                BeanConverter.copyToList(result.getRecords(), SysPermissionResponse.class),
                result.getTotal(),
                result.getPageNum(),
                result.getPageSize()
        );
        return ApiResult.success(mapped);
    }
}


