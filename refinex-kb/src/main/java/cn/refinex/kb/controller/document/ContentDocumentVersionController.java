package cn.refinex.kb.controller.document;

import cn.refinex.common.apilog.core.annotation.LogOperation;
import cn.refinex.common.apilog.core.enums.OperateTypeEnum;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.kb.controller.document.dto.response.ContentDocumentVersionDetailResponseDTO;
import cn.refinex.kb.controller.document.dto.response.ContentDocumentVersionResponseDTO;
import cn.refinex.kb.service.ContentDocumentVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 文档版本管理控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/documents/{documentId}/versions")
@RequiredArgsConstructor
@Tag(name = "知识库文档版本管理", description = "文档版本历史查询、对比、恢复等")
public class ContentDocumentVersionController {

    private final ContentDocumentVersionService versionService;

    @GetMapping
    @Operation(summary = "查询版本历史", description = "分页查询文档的版本历史记录")
    public ApiResult<PageResult<ContentDocumentVersionResponseDTO>> page(
            @Parameter(description = "文档ID", required = true) @PathVariable Long documentId,
            @Parameter(description = "分页参数") @ModelAttribute PageRequest pageRequest) {
        
        Long operatorId = LoginHelper.getUserId();
        PageResult<ContentDocumentVersionResponseDTO> pageResult = versionService.page(
            documentId, 
            pageRequest, 
            operatorId
        );
        return ApiResult.success(pageResult);
    }

    @GetMapping("/{versionNumber}")
    @Operation(summary = "查询版本详情", description = "查询指定版本的详细内容")
    public ApiResult<ContentDocumentVersionDetailResponseDTO> getVersion(
            @Parameter(description = "文档ID", required = true) @PathVariable Long documentId,
            @Parameter(description = "版本号", required = true) @PathVariable Integer versionNumber) {
        
        Long operatorId = LoginHelper.getUserId();
        ContentDocumentVersionDetailResponseDTO detail = versionService.getVersion(
            documentId, 
            versionNumber, 
            operatorId
        );
        return ApiResult.success(detail);
    }

    @PostMapping("/{versionNumber}/restore")
    @LogOperation(operateDesc = "恢复版本", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "恢复版本", description = "将文档恢复到指定历史版本，会创建新的版本记录")
    public ApiResult<Map<String, Object>> restore(
            @Parameter(description = "文档ID", required = true) @PathVariable Long documentId,
            @Parameter(description = "版本号", required = true) @PathVariable Integer versionNumber) {
        
        Long operatorId = LoginHelper.getUserId();
        Integer newVersionNumber = versionService.restoreVersion(documentId, versionNumber, operatorId);
        
        return ApiResult.success(Map.of(
            "newVersionNumber", newVersionNumber,
            "message", "已恢复到版本 #" + versionNumber
        ));
    }

    @DeleteMapping("/clean")
    @LogOperation(operateDesc = "清理旧版本", operationType = OperateTypeEnum.DELETE)
    @Operation(summary = "清理旧版本", description = "清理文档的旧版本，保留最近N个版本")
    public ApiResult<Map<String, Object>> clean(
            @Parameter(description = "文档ID", required = true) @PathVariable Long documentId,
            @Parameter(description = "保留版本数量") @RequestParam(defaultValue = "10") int keepCount) {
        
        Long operatorId = LoginHelper.getUserId();
        
        // 验证保留数量
        if (keepCount < 1 || keepCount > 100) {
            return ApiResult.failure(400, "保留版本数量必须在 1-100 之间");
        }

        int deletedCount = versionService.cleanOldVersions(documentId, keepCount, operatorId);
        
        return ApiResult.success(Map.of(
            "deletedCount", deletedCount,
            "keepCount", keepCount,
            "message", "已清理 " + deletedCount + " 个旧版本"
        ));
    }

    @DeleteMapping("/{versionNumber}")
    @LogOperation(operateDesc = "删除版本", operationType = OperateTypeEnum.DELETE)
    @Operation(summary = "删除版本", description = "删除指定版本（不能删除当前版本）")
    public ApiResult<Boolean> delete(
            @Parameter(description = "文档ID", required = true) @PathVariable Long documentId,
            @Parameter(description = "版本号", required = true) @PathVariable Integer versionNumber) {
        
        Long operatorId = LoginHelper.getUserId();
        boolean success = versionService.deleteVersion(documentId, versionNumber, operatorId);
        return ApiResult.success(success);
    }

    @GetMapping("/compare")
    @Operation(summary = "版本对比", description = "对比两个版本的差异")
    public ApiResult<Map<String, Object>> compare(
            @Parameter(description = "文档ID", required = true) @PathVariable Long documentId,
            @Parameter(description = "起始版本号", required = true) @RequestParam Integer fromVersion,
            @Parameter(description = "目标版本号", required = true) @RequestParam Integer toVersion) {
        
        Long operatorId = LoginHelper.getUserId();
        Map<String, Object> diffResult = versionService.compareVersions(documentId, fromVersion, toVersion, operatorId);
        return ApiResult.success(diffResult);
    }
}

