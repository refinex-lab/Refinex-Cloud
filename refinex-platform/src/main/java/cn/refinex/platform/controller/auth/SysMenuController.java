package cn.refinex.platform.controller.auth;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.controller.auth.dto.request.SysMenuCreateRequestDTO;
import cn.refinex.platform.controller.auth.dto.request.SysMenuQueryRequestDTO;
import cn.refinex.platform.controller.auth.dto.request.SysMenuUpdateRequestDTO;
import cn.refinex.platform.controller.auth.dto.response.SysMenuResponseDTO;
import cn.refinex.platform.entity.sys.SysMenu;
import cn.refinex.platform.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统菜单管理控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/menus")
@Tag(name = "系统菜单管理", description = "系统菜单的增删改查、状态管理等接口")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @PostMapping
    @Operation(summary = "创建菜单", description = "创建新的系统菜单")
    @Parameter(name = "request", description = "菜单创建请求", required = true)
    public ApiResult<Long> createMenu(@Valid @RequestBody SysMenuCreateRequestDTO request) {
        Long menuId = sysMenuService.create(request, LoginHelper.getUserId());
        return ApiResult.success(HttpStatusCode.CREATED, menuId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新菜单", description = "更新指定菜单的信息")
    @Parameter(name = "id", description = "菜单 ID", required = true)
    @Parameter(name = "request", description = "菜单更新请求", required = true)
    public ApiResult<Boolean> updateMenu(@PathVariable Long id, @Valid @RequestBody SysMenuUpdateRequestDTO request) {
        Boolean result = sysMenuService.update(id, request, LoginHelper.getUserId());
        return ApiResult.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单", description = "删除指定的系统菜单")
    @Parameter(name = "id", description = "菜单 ID", required = true)
    public ApiResult<Void> deleteMenu(@PathVariable Long id) {
        sysMenuService.delete(id, LoginHelper.getUserId());
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "修改菜单状态", description = "启用或禁用指定菜单")
    @Parameter(name = "id", description = "菜单 ID", required = true)
    @Parameter(name = "status", description = "状态值", required = true)
    public ApiResult<Boolean> updateMenuStatus(@PathVariable Long id, @RequestParam Integer status) {
        Boolean result = sysMenuService.updateStatus(id, status, LoginHelper.getUserId());
        return ApiResult.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取菜单详情", description = "根据 ID 获取菜单详细信息")
    @Parameter(name = "id", description = "菜单 ID", required = true)
    public ApiResult<SysMenuResponseDTO> getMenu(@PathVariable Long id) {
        SysMenu entity = sysMenuService.getById(id);
        return ApiResult.success(BeanConverter.toBean(entity, SysMenuResponseDTO.class));
    }

    @GetMapping
    @Operation(summary = "获取所有菜单列表", description = "获取系统中所有菜单（树形结构）")
    public ApiResult<List<SysMenuResponseDTO>> listMenus() {
        List<SysMenu> list = sysMenuService.listAll();
        return ApiResult.success(BeanConverter.copyToList(list, SysMenuResponseDTO.class));
    }

    @PostMapping("/search")
    @Operation(summary = "分页查询菜单", description = "根据条件分页查询系统菜单")
    @Parameter(name = "query", description = "查询条件", required = true)
    @Parameter(name = "pageNum", description = "页码", required = true)
    @Parameter(name = "pageSize", description = "每页数量", required = true)
    public ApiResult<PageResult<SysMenuResponseDTO>> searchMenus(
            @RequestBody SysMenuQueryRequestDTO query,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<SysMenu> result = sysMenuService.page(query, new PageRequest(pageNum, pageSize));
        PageResult<SysMenuResponseDTO> mapped = new PageResult<>(
                BeanConverter.copyToList(result.getRecords(), SysMenuResponseDTO.class),
                result.getTotal(),
                result.getPageNum(),
                result.getPageSize()
        );
        return ApiResult.success(mapped);
    }
}


