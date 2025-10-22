package cn.refinex.platform.controller;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.domain.dto.request.SysConfigCreateRequest;
import cn.refinex.platform.domain.dto.request.SysConfigQueryRequest;
import cn.refinex.platform.domain.dto.request.SysConfigUpdateRequest;
import cn.refinex.platform.domain.dto.response.SysConfigResponse;
import cn.refinex.platform.domain.entity.sys.SysConfig;
import cn.refinex.platform.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/configs")
@Tag(name = "系统配置管理", description = "系统配置的增删改查等接口")
public class SysConfigController {

    private final SysConfigService sysConfigService;

    @PostMapping
    @Operation(summary = "创建配置", description = "创建新的系统配置")
    @Parameter(name = "request", description = "配置创建请求", required = true)
    public ApiResult<Long> createConfig(@Valid @RequestBody SysConfigCreateRequest request) {
        Long configId = sysConfigService.create(request, LoginHelper.getUserId());
        return ApiResult.success(HttpStatusCode.CREATED, configId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新配置", description = "更新指定配置的信息")
    @Parameter(name = "id", description = "配置 ID", required = true)
    @Parameter(name = "request", description = "配置更新请求", required = true)
    public ApiResult<Boolean> updateConfig(@PathVariable Long id, @Valid @RequestBody SysConfigUpdateRequest request) {
        Boolean result = sysConfigService.update(id, request, LoginHelper.getUserId());
        return ApiResult.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除配置", description = "删除指定的系统配置")
    @Parameter(name = "id", description = "配置 ID", required = true)
    public ApiResult<Void> deleteConfig(@PathVariable Long id) {
        sysConfigService.delete(id, LoginHelper.getUserId());
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取配置详情", description = "根据 ID 获取配置详细信息")
    @Parameter(name = "id", description = "配置 ID", required = true)
    public ApiResult<SysConfigResponse> getConfig(@PathVariable Long id) {
        SysConfig entity = sysConfigService.getById(id);
        return ApiResult.success(BeanConverter.toBean(entity, SysConfigResponse.class));
    }

    @GetMapping("/by-key/{configKey}")
    @Operation(summary = "根据 Key 获取配置", description = "根据配置键获取配置信息")
    @Parameter(name = "configKey", description = "配置键", required = true)
    public ApiResult<SysConfigResponse> getConfigByKey(@PathVariable String configKey) {
        SysConfig entity = sysConfigService.getByKey(configKey);
        return ApiResult.success(BeanConverter.toBean(entity, SysConfigResponse.class));
    }

    @GetMapping("/by-group/{group}")
    @Operation(summary = "按分组获取配置列表", description = "根据分组获取配置列表")
    @Parameter(name = "group", description = "配置分组", required = true)
    public ApiResult<List<SysConfigResponse>> listConfigsByGroup(@PathVariable("group") String group) {
        List<SysConfig> list = sysConfigService.listByGroup(group);
        return ApiResult.success(BeanConverter.copyToList(list, SysConfigResponse.class));
    }

    @PostMapping("/search")
    @Operation(summary = "分页查询配置", description = "根据条件分页查询系统配置")
    @Parameter(name = "query", description = "查询条件", required = true)
    @Parameter(name = "pageNum", description = "页码", required = true, example = "1")
    @Parameter(name = "pageSize", description = "每页数量", required = true, example = "10")
    public ApiResult<PageResult<SysConfigResponse>> searchConfigs(@RequestBody SysConfigQueryRequest query, int pageNum, int pageSize) {
        PageResult<SysConfig> result = sysConfigService.page(query, new PageRequest(pageNum, pageSize));
        PageResult<SysConfigResponse> mapped = new PageResult<>(
                BeanConverter.copyToList(result.getRecords(), SysConfigResponse.class),
                result.getTotal(),
                result.getPageNum(),
                result.getPageSize()
        );
        return ApiResult.success(mapped);
    }

    @PatchMapping("/{id}/frontend-visibility")
    @Operation(summary = "修改前端可见性", description = "设置配置是否对前端可见")
    @Parameter(name = "id", description = "配置 ID", required = true)
    @Parameter(name = "visible", description = "可见性（1-可见，0-不可见）", required = true)
    public ApiResult<Boolean> updateFrontendVisibility(@PathVariable Long id, @RequestParam Integer visible) {
        Boolean result = sysConfigService.updateFrontendVisible(id, visible, LoginHelper.getUserId());
        return ApiResult.success(result);
    }
}


