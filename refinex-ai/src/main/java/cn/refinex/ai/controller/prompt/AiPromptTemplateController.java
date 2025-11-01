package cn.refinex.ai.controller.prompt;

import cn.refinex.ai.controller.prompt.dto.request.PromptTemplateCreateRequestDTO;
import cn.refinex.ai.controller.prompt.dto.request.PromptTemplateQueryRequestDTO;
import cn.refinex.ai.controller.prompt.dto.request.PromptTemplateUpdateRequestDTO;
import cn.refinex.ai.controller.prompt.dto.response.PromptTemplateResponseDTO;
import cn.refinex.ai.service.AiPromptTemplateService;
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
 * AI 提示词模板管理控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/prompt-templates")
@RequiredArgsConstructor
@Tag(name = "AI 提示词模板管理", description = "AI 提示词模板的增删改查等接口")
public class AiPromptTemplateController {

    private final AiPromptTemplateService templateService;

    @PostMapping
    @LogOperation(operateDesc = "创建提示词模板", operationType = OperateTypeEnum.CREATE)
    @Operation(summary = "创建提示词模板", description = "创建新的提示词模板")
    public ApiResult<Long> createTemplate(@Valid @RequestBody PromptTemplateCreateRequestDTO request) {
        Long userId = LoginHelper.getUserId();
        Long id = templateService.createTemplate(request, userId);
        return ApiResult.success(HttpStatusCode.CREATED, id);
    }

    @PutMapping("/{id}")
    @LogOperation(operateDesc = "更新提示词模板", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "更新提示词模板", description = "更新指定提示词模板的信息")
    @Parameter(name = "id", description = "模板 ID", required = true)
    public ApiResult<Boolean> updateTemplate(
            @PathVariable("id") Long id,
            @Valid @RequestBody PromptTemplateUpdateRequestDTO request) {
        Long userId = LoginHelper.getUserId();
        boolean success = templateService.updateTemplate(id, request, userId);
        return ApiResult.success(success);
    }

    @DeleteMapping("/{id}")
    @LogOperation(operateDesc = "删除提示词模板", operationType = OperateTypeEnum.DELETE)
    @Operation(summary = "删除提示词模板", description = "删除指定的提示词模板（软删除）")
    @Parameter(name = "id", description = "模板 ID", required = true)
    public ApiResult<Void> deleteTemplate(@PathVariable("id") Long id) {
        Long userId = LoginHelper.getUserId();
        templateService.deleteTemplate(id, userId);
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取提示词模板详情", description = "根据 ID 获取提示词模板详细信息")
    @Parameter(name = "id", description = "模板 ID", required = true)
    public ApiResult<PromptTemplateResponseDTO> getTemplate(@PathVariable("id") Long id) {
        PromptTemplateResponseDTO response = templateService.getTemplateById(id);
        return ApiResult.success(response);
    }

    @GetMapping("/by-code/{code}")
    @Operation(summary = "根据编码获取模板", description = "根据模板编码获取提示词模板")
    @Parameter(name = "code", description = "模板编码", required = true)
    public ApiResult<PromptTemplateResponseDTO> getTemplateByCode(@PathVariable("code") String code) {
        PromptTemplateResponseDTO response = templateService.getTemplateByCode(code);
        return ApiResult.success(response);
    }

    @GetMapping
    @Operation(summary = "分页查询提示词模板", description = "根据条件分页查询提示词模板")
    @Parameter(name = "category", description = "模板分类")
    @Parameter(name = "type", description = "模板类型：SYSTEM,USER")
    @Parameter(name = "isPublic", description = "是否公开：0否,1是")
    @Parameter(name = "status", description = "状态：0正常,1停用")
    @Parameter(name = "keyword", description = "关键词（搜索模板名称和编码）")
    @Parameter(name = "orderBy", description = "排序字段")
    @Parameter(name = "orderDirection", description = "排序方向：ASC 或 DESC")
    @Parameter(name = "pageNum", description = "页码，从1开始")
    @Parameter(name = "pageSize", description = "每页数量")
    public ApiResult<PageResult<PromptTemplateResponseDTO>> pageQueryTemplates(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "isPublic", required = false) Integer isPublic,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "orderDirection", required = false) String orderDirection,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") int pageSize) {
        
        PromptTemplateQueryRequestDTO query = new PromptTemplateQueryRequestDTO();
        query.setCategory(category);
        query.setType(type);
        query.setIsPublic(isPublic);
        query.setStatus(status);
        query.setKeyword(keyword);

        PageRequest pageRequest = new PageRequest(pageNum, pageSize, orderBy, orderDirection);
        PageResult<PromptTemplateResponseDTO> pageResult = templateService.pageQueryTemplates(query, pageRequest);
        
        return ApiResult.success(pageResult);
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有提示词模板", description = "获取所有提示词模板列表（用于下拉选择）")
    public ApiResult<List<PromptTemplateResponseDTO>> listAllTemplates() {
        List<PromptTemplateResponseDTO> list = templateService.listAllTemplates();
        return ApiResult.success(list);
    }

    @PutMapping("/{id}/status")
    @LogOperation(operateDesc = "切换模板状态", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "切换模板状态", description = "启用或停用指定的提示词模板")
    @Parameter(name = "id", description = "模板 ID", required = true)
    @Parameter(name = "status", description = "状态：0正常,1停用", required = true)
    public ApiResult<Boolean> toggleTemplateStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") Integer status) {
        Long userId = LoginHelper.getUserId();
        boolean success = templateService.toggleTemplateStatus(id, status, userId);
        return ApiResult.success(success);
    }
}

