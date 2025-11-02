package cn.refinex.ai.controller.model;

import cn.refinex.ai.controller.model.dto.request.ModelConfigCreateRequestDTO;
import cn.refinex.ai.controller.model.dto.request.ModelConfigQueryRequestDTO;
import cn.refinex.ai.controller.model.dto.request.ModelConfigUpdateRequestDTO;
import cn.refinex.ai.controller.model.dto.response.ModelConfigResponseDTO;
import cn.refinex.ai.service.AiModelConfigService;
import cn.refinex.common.apilog.core.annotation.LogOperation;
import cn.refinex.common.apilog.core.enums.OperateTypeEnum;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI 模型配置管理控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/model-configs")
@RequiredArgsConstructor
@Tag(name = "AI 模型配置管理", description = "AI 模型配置的增删改查等接口")
public class AiModelConfigController {

    private final AiModelConfigService modelConfigService;

    @PostMapping
    @LogOperation(operateDesc = "创建模型配置", operationType = OperateTypeEnum.CREATE)
    @Operation(summary = "创建模型配置", description = "创建新的 AI 模型配置")
    public ApiResult<Long> createModelConfig(@Valid @RequestBody ModelConfigCreateRequestDTO request) {
        Long userId = LoginHelper.getUserId();
        Long id = modelConfigService.createModelConfig(request, userId);
        return ApiResult.success(HttpStatusCode.CREATED, id);
    }

    @PutMapping("/{id}")
    @LogOperation(operateDesc = "更新模型配置", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "更新模型配置", description = "更新指定模型配置的信息")
    @Parameter(name = "id", description = "模型配置 ID", required = true)
    public ApiResult<Boolean> updateModelConfig(
            @PathVariable("id") Long id,
            @Valid @RequestBody ModelConfigUpdateRequestDTO request) {
        Long userId = LoginHelper.getUserId();
        boolean success = modelConfigService.updateModelConfig(id, request, userId);
        return ApiResult.success(success);
    }

    @DeleteMapping("/{id}")
    @LogOperation(operateDesc = "删除模型配置", operationType = OperateTypeEnum.DELETE)
    @Operation(summary = "删除模型配置", description = "删除指定的模型配置（软删除）")
    @Parameter(name = "id", description = "模型配置 ID", required = true)
    public ApiResult<Void> deleteModelConfig(@PathVariable("id") Long id) {
        Long userId = LoginHelper.getUserId();
        modelConfigService.deleteModelConfig(id, userId);
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取模型配置详情", description = "根据 ID 获取模型配置详细信息")
    @Parameter(name = "id", description = "模型配置 ID", required = true)
    public ApiResult<ModelConfigResponseDTO> getModelConfig(@PathVariable("id") Long id) {
        ModelConfigResponseDTO response = modelConfigService.getModelConfigById(id);
        return ApiResult.success(response);
    }

    @GetMapping
    @Operation(summary = "分页查询模型配置", description = "根据条件分页查询模型配置")
    @Parameter(name = "provider", description = "供应商")
    @Parameter(name = "modelType", description = "模型类型")
    @Parameter(name = "status", description = "状态：1正常,0停用")
    @Parameter(name = "keyword", description = "关键词（搜索模型名称和编码）")
    @Parameter(name = "orderBy", description = "排序字段")
    @Parameter(name = "orderDirection", description = "排序方向：ASC 或 DESC")
    @Parameter(name = "pageNum", description = "页码，从1开始")
    @Parameter(name = "pageSize", description = "每页数量")
    public ApiResult<PageResult<ModelConfigResponseDTO>> pageQueryModelConfigs(
            @RequestParam(value = "provider", required = false) String provider,
            @RequestParam(value = "modelType", required = false) String modelType,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "orderDirection", required = false) String orderDirection,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") int pageSize) {
        
        ModelConfigQueryRequestDTO query = new ModelConfigQueryRequestDTO();
        query.setProvider(provider);
        query.setModelType(modelType);
        query.setStatus(status);
        query.setKeyword(keyword);

        PageRequest pageRequest = new PageRequest(pageNum, pageSize, orderBy, orderDirection);
        PageResult<ModelConfigResponseDTO> pageResult = modelConfigService.pageQueryModelConfigs(query, pageRequest);
        
        return ApiResult.success(pageResult);
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有模型配置", description = "获取所有模型配置列表（用于下拉选择）")
    public ApiResult<List<ModelConfigResponseDTO>> listAllModelConfigs() {
        List<ModelConfigResponseDTO> list = modelConfigService.listAllModelConfigs();
        return ApiResult.success(list);
    }

    @PutMapping("/{id}/status")
    @LogOperation(operateDesc = "切换模型状态", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "切换模型状态", description = "启用或停用指定的模型配置")
    @Parameter(name = "id", description = "模型配置 ID", required = true)
    @Parameter(name = "status", description = "状态：1正常,0停用", required = true)
    public ApiResult<Boolean> toggleModelStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") Integer status) {
        Long userId = LoginHelper.getUserId();
        boolean success = modelConfigService.toggleModelStatus(id, status, userId);
        return ApiResult.success(success);
    }
}

