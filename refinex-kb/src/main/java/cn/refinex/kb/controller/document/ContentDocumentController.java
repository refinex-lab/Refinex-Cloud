package cn.refinex.kb.controller.document;

import cn.refinex.common.apilog.core.annotation.LogOperation;
import cn.refinex.common.apilog.core.enums.OperateTypeEnum;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.kb.controller.document.dto.request.ContentDocumentCreateRequestDTO;
import cn.refinex.kb.controller.document.dto.request.ContentDocumentQueryRequestDTO;
import cn.refinex.kb.controller.document.dto.request.ContentDocumentSaveContentRequestDTO;
import cn.refinex.kb.controller.document.dto.request.ContentDocumentUpdateRequestDTO;
import cn.refinex.kb.controller.document.dto.response.ContentDocumentDetailResponseDTO;
import cn.refinex.kb.controller.document.dto.response.ContentDocumentResponseDTO;
import cn.refinex.kb.service.ContentDocumentService;
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
 * 文档管理控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Tag(name = "知识库文档管理", description = "文档的增删改查、发布、标签管理等")
public class ContentDocumentController {

    private final ContentDocumentService documentService;

    @PostMapping
    @LogOperation(operateDesc = "创建文档", operationType = OperateTypeEnum.CREATE)
    @Operation(summary = "创建文档", description = "创建新文档，默认状态为草稿")
    public ApiResult<Long> create(@Valid @RequestBody ContentDocumentCreateRequestDTO request) {
        Long operatorId = LoginHelper.getUserId();
        Long documentId = documentService.create(request, operatorId);
        return ApiResult.success(documentId);
    }

    @PutMapping("/{id}")
    @LogOperation(operateDesc = "更新文档", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "更新文档", description = "更新文档的基本信息（不包括内容）")
    public ApiResult<Boolean> update(
            @Parameter(description = "文档ID", required = true) @PathVariable Long id,
            @Valid @RequestBody ContentDocumentUpdateRequestDTO request) {
        
        Long operatorId = LoginHelper.getUserId();
        boolean success = documentService.update(id, request, operatorId);
        return ApiResult.success(success);
    }

    @PostMapping("/{id}/content")
    @LogOperation(operateDesc = "保存文档内容（MDXEditor 集成）", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "保存文档内容（MDXEditor 集成）", description = "保存文档内容，自动创建版本记录（MDXEditor集成核心接口）")
    public ApiResult<Map<String, Object>> saveContent(
            @Parameter(description = "文档ID", required = true) @PathVariable Long id,
            @Valid @RequestBody ContentDocumentSaveContentRequestDTO request) {
        
        Long operatorId = LoginHelper.getUserId();
        Integer newVersionNumber = documentService.saveContent(id, request, operatorId);
        
        // 返回版本号
        return ApiResult.success(Map.of(
            "versionNumber", newVersionNumber,
            "message", "保存成功"
        ));
    }

    @DeleteMapping("/{id}")
    @LogOperation(operateDesc = "删除文档", operationType = OperateTypeEnum.DELETE)
    @Operation(summary = "删除文档", description = "逻辑删除文档")
    public ApiResult<Boolean> delete(@Parameter(description = "文档ID", required = true) @PathVariable Long id) {
        Long operatorId = LoginHelper.getUserId();
        boolean success = documentService.delete(id, operatorId);
        return ApiResult.success(success);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询文档详情", description = "根据ID查询文档详情，包含完整内容")
    public ApiResult<ContentDocumentDetailResponseDTO> getById(@Parameter(description = "文档ID", required = true) @PathVariable Long id) {
        Long operatorId = LoginHelper.getUserId();
        ContentDocumentDetailResponseDTO detail = documentService.getById(id, operatorId);
        return ApiResult.success(detail);
    }

    @GetMapping("/guid/{docGuid}")
    @Operation(summary = "根据GUID查询文档", description = "通过全局唯一标识查询文档详情")
    public ApiResult<ContentDocumentDetailResponseDTO> getByGuid(@Parameter(description = "文档GUID", required = true) @PathVariable String docGuid) {
        Long operatorId = LoginHelper.getUserId();
        ContentDocumentDetailResponseDTO detail = documentService.getByDocGuid(docGuid, operatorId);
        return ApiResult.success(detail);
    }

    @GetMapping
    @Operation(summary = "分页查询文档", description = "支持多条件查询文档列表")
    public ApiResult<PageResult<ContentDocumentResponseDTO>> page(
            @Parameter(description = "查询条件") @ModelAttribute ContentDocumentQueryRequestDTO request,
            @Parameter(description = "分页参数") @ModelAttribute PageRequest pageRequest) {
        
        Long operatorId = LoginHelper.getUserId();
        PageResult<ContentDocumentResponseDTO> pageResult = documentService.page(request, pageRequest, operatorId);
        return ApiResult.success(pageResult);
    }

    @GetMapping("/space/{spaceId}")
    @Operation(summary = "查询空间下的文档", description = "根据空间ID查询文档列表")
    public ApiResult<List<ContentDocumentResponseDTO>> listBySpace(@Parameter(description = "空间ID", required = true) @PathVariable Long spaceId) {
        Long operatorId = LoginHelper.getUserId();
        List<ContentDocumentResponseDTO> documents = documentService.listBySpaceId(spaceId, operatorId);
        return ApiResult.success(documents);
    }

    @GetMapping("/directory/{directoryId}")
    @Operation(summary = "查询目录下的文档", description = "根据目录ID查询文档列表")
    public ApiResult<List<ContentDocumentResponseDTO>> listByDirectory(@Parameter(description = "目录ID", required = true) @PathVariable Long directoryId) {
        Long operatorId = LoginHelper.getUserId();
        List<ContentDocumentResponseDTO> documents = documentService.listByDirectoryId(directoryId, operatorId);
        return ApiResult.success(documents);
    }

    @PostMapping("/{id}/publish")
    @LogOperation(operateDesc = "发布文档", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "发布文档", description = "将草稿或已下架的文档发布为公开状态")
    public ApiResult<Boolean> publish(@Parameter(description = "文档ID", required = true) @PathVariable Long id) {
        Long operatorId = LoginHelper.getUserId();
        boolean success = documentService.publish(id, operatorId);
        return ApiResult.success(success);
    }

    @PostMapping("/{id}/offline")
    @LogOperation(operateDesc = "下架文档", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "下架文档", description = "将已发布的文档下架")
    public ApiResult<Boolean> offline(@Parameter(description = "文档ID", required = true) @PathVariable Long id) {
        Long operatorId = LoginHelper.getUserId();
        boolean success = documentService.offline(id, operatorId);
        return ApiResult.success(success);
    }

    @PutMapping("/{id}/move")
    @LogOperation(operateDesc = "移动文档", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "移动文档", description = "将文档移动到其他目录")
    public ApiResult<Boolean> move(
            @Parameter(description = "文档ID", required = true) @PathVariable Long id,
            @Parameter(description = "目标目录ID（null表示根目录）") @RequestParam(required = false) Long directoryId) {
        Long operatorId = LoginHelper.getUserId();
        boolean success = documentService.moveToDirectory(id, directoryId, operatorId);
        return ApiResult.success(success);
    }

    @PostMapping("/{id}/copy")
    @LogOperation(operateDesc = "复制文档", operationType = OperateTypeEnum.CREATE)
    @Operation(summary = "复制文档", description = "复制文档到指定空间和目录")
    public ApiResult<Long> copy(
            @Parameter(description = "文档ID", required = true) @PathVariable Long id,
            @Parameter(description = "目标空间ID（null表示同一空间）") @RequestParam(required = false) Long spaceId,
            @Parameter(description = "目标目录ID（null表示根目录）") @RequestParam(required = false) Long directoryId) {

        Long operatorId = LoginHelper.getUserId();
        Long newDocumentId = documentService.copy(id, spaceId, directoryId, operatorId);
        return ApiResult.success(newDocumentId);
    }

    @PostMapping("/{id}/tags")
    @LogOperation(operateDesc = "绑定标签", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "绑定标签", description = "为文档绑定标签（会替换原有标签）")
    public ApiResult<Boolean> bindTags(
            @Parameter(description = "文档ID", required = true) @PathVariable Long id,
            @Parameter(description = "标签ID列表", required = true) @RequestBody List<Long> tagIds) {
        
        Long operatorId = LoginHelper.getUserId();
        boolean success = documentService.bindTags(id, tagIds, operatorId);
        return ApiResult.success(success);
    }

    @DeleteMapping("/{id}/tags/{tagId}")
    @LogOperation(operateDesc = "解绑标签", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "解绑标签", description = "从文档中移除指定标签")
    public ApiResult<Boolean> unbindTag(
            @Parameter(description = "文档ID", required = true) @PathVariable Long id,
            @Parameter(description = "标签ID", required = true) @PathVariable Long tagId) {
        
        Long operatorId = LoginHelper.getUserId();
        boolean success = documentService.unbindTag(id, tagId, operatorId);
        return ApiResult.success(success);
    }

    @PostMapping("/{id}/like")
    @LogOperation(operateDesc = "点赞文档", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "点赞文档", description = "为文档点赞")
    public ApiResult<Boolean> like(@Parameter(description = "文档ID", required = true) @PathVariable Long id) {
        Long operatorId = LoginHelper.getUserId();
        boolean success = documentService.like(id, operatorId);
        return ApiResult.success(success);
    }

    @DeleteMapping("/{id}/like")
    @LogOperation(operateDesc = "取消点赞", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "取消点赞", description = "取消对文档的点赞")
    public ApiResult<Boolean> unlike(@Parameter(description = "文档ID", required = true) @PathVariable Long id) {
        Long operatorId = LoginHelper.getUserId();
        boolean success = documentService.unlike(id, operatorId);
        return ApiResult.success(success);
    }

    @PostMapping("/{id}/collect")
    @LogOperation(operateDesc = "收藏文档", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "收藏文档", description = "将文档添加到收藏")
    public ApiResult<Boolean> collect(@Parameter(description = "文档ID", required = true) @PathVariable Long id) {
        Long operatorId = LoginHelper.getUserId();
        boolean success = documentService.collect(id, operatorId);
        return ApiResult.success(success);
    }

    @DeleteMapping("/{id}/collect")
    @LogOperation(operateDesc = "取消收藏", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "取消收藏", description = "从收藏中移除文档")
    public ApiResult<Boolean> unCollect(@Parameter(description = "文档ID", required = true) @PathVariable Long id) {
        Long operatorId = LoginHelper.getUserId();
        boolean success = documentService.uncollect(id, operatorId);
        return ApiResult.success(success);
    }

    @PostMapping("/{id}/view")
    @LogOperation(operateDesc = "记录浏览", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "记录浏览", description = "记录文档浏览，增加浏览计数")
    public ApiResult<Boolean> view(
            @Parameter(description = "文档ID", required = true) @PathVariable Long id,
            @Parameter(description = "浏览时长（秒）") @RequestParam(required = false) Integer duration) {

        Long operatorId = LoginHelper.getUserId();
        boolean success = documentService.recordView(id, operatorId, duration);
        return ApiResult.success(success);
    }
}

