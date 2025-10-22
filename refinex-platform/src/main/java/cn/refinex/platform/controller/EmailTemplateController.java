package cn.refinex.platform.controller;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.mail.domain.dto.EmailTemplateDTO;
import cn.refinex.common.mail.domain.entity.EmailTemplate;
import cn.refinex.platform.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 邮件模板管理控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/email/templates")
@RequiredArgsConstructor
@Tag(name = "邮件模板管理", description = "邮件模板的增删改查、预览等接口")
public class EmailTemplateController {

    private final EmailService emailService;

    @PostMapping
    @Operation(summary = "创建邮件模板", description = "创建新的邮件模板")
    @Parameter(name = "templateDTO", description = "邮件模板 DTO", required = true)
    public ApiResult<Long> createTemplate(@Valid @RequestBody EmailTemplateDTO templateDTO) {
        Long templateId = emailService.createTemplate(templateDTO);
        return ApiResult.success(cn.refinex.common.enums.HttpStatusCode.CREATED, templateId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新邮件模板", description = "更新已有的邮件模板")
    @Parameter(name = "id", description = "模板 ID", required = true)
    @Parameter(name = "templateDTO", description = "邮件模板 DTO", required = true)
    public ApiResult<Boolean> updateTemplate(@PathVariable Long id, @Valid @RequestBody EmailTemplateDTO templateDTO) {
        templateDTO.setId(id);
        boolean success = emailService.updateTemplate(templateDTO);
        return ApiResult.success(success);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除邮件模板", description = "删除指定的邮件模板（系统模板不可删除）")
    @Parameter(name = "id", description = "模板 ID", required = true)
    public ApiResult<Boolean> deleteTemplate(@PathVariable Long id) {
        boolean success = emailService.deleteTemplate(id);
        return ApiResult.success(success);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取邮件模板详情", description = "根据模板 ID 获取模板详细信息")
    @Parameter(name = "id", description = "模板 ID", required = true)
    public ApiResult<EmailTemplate> getTemplateById(@PathVariable Long id) {
        EmailTemplate template = emailService.getTemplateById(id);
        return ApiResult.success(template);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "根据编码获取模板", description = "根据模板编码获取模板详细信息")
    @Parameter(name = "code", description = "模板编码", required = true)
    public ApiResult<EmailTemplate> getTemplateByCode(@PathVariable String code) {
        EmailTemplate template = emailService.getTemplateByCode(code);
        return ApiResult.success(template);
    }

    @GetMapping
    @Operation(summary = "查询所有可用模板", description = "查询所有启用状态的邮件模板")
    public ApiResult<List<EmailTemplate>> getAllAvailableTemplates() {
        List<EmailTemplate> templates = emailService.getAllAvailableTemplates();
        return ApiResult.success(templates);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "根据分类查询模板", description = "根据模板分类查询邮件模板列表")
    @Parameter(name = "category", description = "模板分类", required = true)
    public ApiResult<List<EmailTemplate>> getTemplatesByCategory(@PathVariable String category) {
        List<EmailTemplate> templates = emailService.getTemplatesByCategory(category);
        return ApiResult.success(templates);
    }

    @PostMapping("/preview")
    @Operation(summary = "预览邮件模板", description = "使用测试数据预览模板渲染效果")
    @Parameter(name = "code", description = "模板编码", required = true)
    @Parameter(name = "variables", description = "模板变量", required = true)
    public ApiResult<String> previewTemplate(@RequestParam String code, @RequestBody Map<String, Object> variables) {
        String content = emailService.previewTemplate(code, variables);
        return ApiResult.success(content);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新模板状态", description = "启用或停用邮件模板")
    @Parameter(name = "id", description = "模板 ID", required = true)
    @Parameter(name = "status", description = "状态（1-启用，0-停用）", required = true)
    public ApiResult<Boolean> updateTemplateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = emailService.updateTemplateStatus(id, status);
        return ApiResult.success(success);
    }
}

