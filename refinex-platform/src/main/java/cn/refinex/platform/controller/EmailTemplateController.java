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
 * 邮件模板管理控制器（管理后台）
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/email/template")
@RequiredArgsConstructor
@Tag(name = "邮件模板管理", description = "邮件模板管理接口")
public class EmailTemplateController {

    private final EmailService emailService;

    /**
     * 创建模板
     *
     * @param templateDTO 模板 DTO
     * @return 模板 ID
     */
    @PostMapping
    @Operation(summary = "创建邮件模板", description = "创建新的邮件模板")
    public ApiResult<Long> createTemplate(@Valid @RequestBody EmailTemplateDTO templateDTO) {
        Long templateId = emailService.createTemplate(templateDTO);
        return ApiResult.success(templateId);
    }

    /**
     * 更新模板
     *
     * @param id          模板 ID
     * @param templateDTO 模板 DTO
     * @return 是否成功
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新邮件模板", description = "更新已有的邮件模板")
    public ApiResult<Boolean> updateTemplate(
            @Parameter(description = "模板 ID") @PathVariable Long id,
            @Valid @RequestBody EmailTemplateDTO templateDTO) {
        templateDTO.setId(id);
        boolean success = emailService.updateTemplate(templateDTO);
        return ApiResult.success(success);
    }

    /**
     * 删除模板
     *
     * @param id 模板 ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除邮件模板", description = "删除指定的邮件模板（系统模板不可删除）")
    public ApiResult<Boolean> deleteTemplate(
            @Parameter(description = "模板 ID") @PathVariable Long id) {
        boolean success = emailService.deleteTemplate(id);
        return ApiResult.success(success);
    }

    /**
     * 根据 ID 获取模板
     *
     * @param id 模板 ID
     * @return 模板信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取邮件模板详情", description = "根据模板 ID 获取模板详细信息")
    public ApiResult<EmailTemplate> getTemplateById(
            @Parameter(description = "模板 ID") @PathVariable Long id) {
        EmailTemplate template = emailService.getTemplateById(id);
        return ApiResult.success(template);
    }

    /**
     * 根据模板编码获取模板
     *
     * @param code 模板编码
     * @return 模板信息
     */
    @GetMapping("/code/{code}")
    @Operation(summary = "根据编码获取模板", description = "根据模板编码获取模板详细信息")
    public ApiResult<EmailTemplate> getTemplateByCode(
            @Parameter(description = "模板编码") @PathVariable String code) {
        EmailTemplate template = emailService.getTemplateByCode(code);
        return ApiResult.success(template);
    }

    /**
     * 查询所有可用模板
     *
     * @return 模板列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有可用模板", description = "查询所有启用状态的邮件模板")
    public ApiResult<List<EmailTemplate>> getAllAvailableTemplates() {
        List<EmailTemplate> templates = emailService.getAllAvailableTemplates();
        return ApiResult.success(templates);
    }

    /**
     * 根据分类查询模板
     *
     * @param category 分类
     * @return 模板列表
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "根据分类查询模板", description = "根据模板分类查询邮件模板列表")
    public ApiResult<List<EmailTemplate>> getTemplatesByCategory(
            @Parameter(description = "模板分类") @PathVariable String category) {
        List<EmailTemplate> templates = emailService.getTemplatesByCategory(category);
        return ApiResult.success(templates);
    }

    /**
     * 预览模板
     *
     * @param code      模板编码
     * @param variables 模板变量
     * @return 渲染后的内容
     */
    @PostMapping("/preview")
    @Operation(summary = "预览邮件模板", description = "使用测试数据预览模板渲染效果")
    public ApiResult<String> previewTemplate(
            @Parameter(description = "模板编码") @RequestParam String code,
            @Parameter(description = "模板变量") @RequestBody Map<String, Object> variables) {
        String content = emailService.previewTemplate(code, variables);
        return ApiResult.success(content);
    }

    /**
     * 更新模板状态
     *
     * @param id     模板 ID
     * @param status 状态（1-启用，0-停用）
     * @return 是否成功
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新模板状态", description = "启用或停用邮件模板")
    public ApiResult<Boolean> updateTemplateStatus(
            @Parameter(description = "模板 ID") @PathVariable Long id,
            @Parameter(description = "状态（1-启用，0-停用）") @RequestParam Integer status) {
        boolean success = emailService.updateTemplateStatus(id, status);
        return ApiResult.success(success);
    }
}

