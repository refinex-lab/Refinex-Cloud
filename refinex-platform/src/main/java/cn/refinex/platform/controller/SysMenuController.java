package cn.refinex.platform.controller;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.platform.domain.dto.request.SysMenuCreateRequest;
import cn.refinex.platform.domain.dto.request.SysMenuQueryRequest;
import cn.refinex.platform.domain.dto.request.SysMenuUpdateRequest;
import cn.refinex.platform.domain.entity.sys.SysMenu;
import cn.refinex.platform.domain.dto.response.SysMenuResponse;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "系统菜单管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/menu")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @Operation(summary = "创建菜单")
    @PostMapping("/create")
    public ApiResult<Long> create(@Valid @RequestBody SysMenuCreateRequest request) {
        return ApiResult.success(sysMenuService.create(request, LoginHelper.getUserId()));
    }

    @Operation(summary = "更新菜单")
    @PostMapping("/update")
    public ApiResult<Boolean> update(@Valid @RequestBody SysMenuUpdateRequest request) {
        return ApiResult.success(sysMenuService.update(request, LoginHelper.getUserId()));
    }

    @Operation(summary = "删除菜单")
    @PostMapping("/delete/{id}")
    public ApiResult<Boolean> delete(@PathVariable Long id) {
        return ApiResult.success(sysMenuService.delete(id, LoginHelper.getUserId()));
    }

    @Operation(summary = "修改状态")
    @PostMapping("/status/{id}/{status}")
    public ApiResult<Boolean> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        return ApiResult.success(sysMenuService.updateStatus(id, status, LoginHelper.getUserId()));
    }

    @Operation(summary = "菜单详情")
    @GetMapping("/detail/{id}")
    public ApiResult<SysMenuResponse> detail(@PathVariable Long id) {
        SysMenu entity = sysMenuService.getById(id);
        return ApiResult.success(BeanConverter.toBean(entity, SysMenuResponse.class));
    }

    @Operation(summary = "全部菜单列表")
    @GetMapping("/list")
    public ApiResult<List<SysMenuResponse>> listAll() {
        List<SysMenu> list = sysMenuService.listAll();
        return ApiResult.success(BeanConverter.copyToList(list, SysMenuResponse.class));
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public ApiResult<PageResult<SysMenuResponse>> page(@RequestBody SysMenuQueryRequest query,
                                               @RequestParam(defaultValue = "1") int pageNum,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<SysMenu> result = sysMenuService.page(query, new PageRequest(pageNum, pageSize));
        PageResult<SysMenuResponse> mapped = new PageResult<>(
                BeanConverter.copyToList(result.getRecords(), SysMenuResponse.class),
                result.getTotal(),
                result.getPageNum(),
                result.getPageSize()
        );
        return ApiResult.success(mapped);
    }
}


