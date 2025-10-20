package cn.refinex.platform.controller;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.platform.domain.dto.request.SysConfigCreateRequest;
import cn.refinex.platform.domain.dto.request.SysConfigQueryRequest;
import cn.refinex.platform.domain.dto.request.SysConfigUpdateRequest;
import cn.refinex.platform.domain.entity.sys.SysConfig;
import cn.refinex.platform.domain.dto.response.SysConfigResponse;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置管理控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Tag(name = "系统配置管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/config")
public class SysConfigController {

    private final SysConfigService sysConfigService;

    @Operation(summary = "创建配置")
    @PostMapping("/create")
    public ApiResult<Long> create(@Valid @RequestBody SysConfigCreateRequest request) {
        return ApiResult.success(sysConfigService.create(request, LoginHelper.getUserId()));
    }

    @Operation(summary = "更新配置")
    @PostMapping("/update")
    public ApiResult<Boolean> update(@Valid @RequestBody SysConfigUpdateRequest request) {
        return ApiResult.success(sysConfigService.update(request, LoginHelper.getUserId()));
    }

    @Operation(summary = "删除配置")
    @PostMapping("/delete/{id}")
    public ApiResult<Boolean> delete(@PathVariable Long id) {
        return ApiResult.success(sysConfigService.delete(id, LoginHelper.getUserId()));
    }

    @Operation(summary = "配置详情")
    @GetMapping("/detail/{id}")
    public ApiResult<SysConfigResponse> detail(@PathVariable Long id) {
        SysConfig entity = sysConfigService.getById(id);
        return ApiResult.success(BeanConverter.toBean(entity, SysConfigResponse.class));
    }

    @Operation(summary = "根据Key获取配置")
    @GetMapping("/key/{configKey}")
    public ApiResult<SysConfigResponse> getByKey(@PathVariable String configKey) {
        SysConfig entity = sysConfigService.getByKey(configKey);
        return ApiResult.success(BeanConverter.toBean(entity, SysConfigResponse.class));
    }

    @Operation(summary = "按分组列出配置")
    @GetMapping("/group/{group}")
    public ApiResult<List<SysConfigResponse>> listByGroup(@PathVariable("group") String group) {
        List<SysConfig> list = sysConfigService.listByGroup(group);
        return ApiResult.success(BeanConverter.copyToList(list, SysConfigResponse.class));
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public ApiResult<PageResult<SysConfigResponse>> page(@RequestBody SysConfigQueryRequest query,
                                                 @RequestParam(defaultValue = "1") int pageNum,
                                                 @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<SysConfig> result = sysConfigService.page(query, new PageRequest(pageNum, pageSize));
        PageResult<SysConfigResponse> mapped = new PageResult<>(
                BeanConverter.copyToList(result.getRecords(), SysConfigResponse.class),
                result.getTotal(),
                result.getPageNum(),
                result.getPageSize()
        );
        return ApiResult.success(mapped);
    }

    @Operation(summary = "修改前端可见")
    @PostMapping("/frontend/{id}/{visible}")
    public ApiResult<Boolean> updateFrontendVisible(@PathVariable Long id, @PathVariable("visible") Integer visible) {
        return ApiResult.success(sysConfigService.updateFrontendVisible(id, visible, LoginHelper.getUserId()));
    }
}


