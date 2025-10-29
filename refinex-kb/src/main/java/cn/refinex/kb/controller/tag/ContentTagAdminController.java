package cn.refinex.kb.controller.tag;

import cn.refinex.common.apilog.core.annotation.LogOperation;
import cn.refinex.common.apilog.core.enums.OperateTypeEnum;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.kb.controller.tag.dto.request.ContentTagCreateRequestDTO;
import cn.refinex.kb.controller.tag.dto.request.ContentTagUpdateRequestDTO;
import cn.refinex.kb.controller.tag.dto.response.ContentTagResponseDTO;
import cn.refinex.kb.service.ContentTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内容标签管理控制器（管理端）
 * <p>
 * 管理员可以查看和管理所有用户的标签
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/tags")
@Tag(name = "知识库标签管理（管理端）", description = "管理员管理所有用户的标签")
public class ContentTagAdminController {

    private final ContentTagService contentTagService;

    @PostMapping
    @LogOperation(operateDesc = "创建标签（管理端）", operationType = OperateTypeEnum.CREATE)
    @Operation(summary = "创建标签", description = "创建新的内容标签（管理员）")
    @Parameter(name = "request", description = "标签创建请求", required = true)
    public ApiResult<Long> createTag(@Valid @RequestBody ContentTagCreateRequestDTO request) {
        Long tagId = contentTagService.create(request, LoginHelper.getUserId());
        return ApiResult.success(HttpStatusCode.CREATED, tagId);
    }

    @PutMapping("/{id}")
    @LogOperation(operateDesc = "更新标签（管理端）", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "更新标签", description = "更新指定标签的信息（管理员）")
    @Parameter(name = "id", description = "标签 ID", required = true)
    @Parameter(name = "request", description = "标签更新请求", required = true)
    public ApiResult<Boolean> updateTag(@PathVariable Long id, @Valid @RequestBody ContentTagUpdateRequestDTO request) {
        Boolean result = contentTagService.update(id, request, LoginHelper.getUserId());
        return ApiResult.success(result);
    }

    @DeleteMapping("/{id}")
    @LogOperation(operateDesc = "删除标签（管理端）", operationType = OperateTypeEnum.DELETE)
    @Operation(summary = "删除标签", description = "删除指定的标签（管理员）")
    @Parameter(name = "id", description = "标签 ID", required = true)
    public ApiResult<Void> deleteTag(@PathVariable Long id) {
        contentTagService.delete(id, LoginHelper.getUserId());
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @DeleteMapping("/batch")
    @LogOperation(operateDesc = "批量删除标签（管理端）", operationType = OperateTypeEnum.DELETE)
    @Operation(summary = "批量删除标签", description = "批量删除标签（管理员）")
    @Parameter(name = "ids", description = "标签 ID 列表", required = true)
    public ApiResult<Void> batchDeleteTags(@RequestBody List<Long> ids) {
        contentTagService.batchDelete(ids, LoginHelper.getUserId());
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取标签详情", description = "根据 ID 获取标签详细信息")
    @Parameter(name = "id", description = "标签 ID", required = true)
    public ApiResult<ContentTagResponseDTO> getTag(@PathVariable Long id) {
        ContentTagResponseDTO tag = contentTagService.getById(id);
        return ApiResult.success(tag);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询所有标签", description = "分页查询所有用户的标签（管理端）")
    @Parameter(name = "tagName", description = "标签名称（可选）")
    @Parameter(name = "tagType", description = "标签类型（可选）：0系统标签,1用户自定义标签")
    @Parameter(name = "creatorId", description = "创建者ID（可选）")
    @Parameter(name = "pageNum", description = "页码", required = true, example = "1")
    @Parameter(name = "pageSize", description = "每页数量", required = true, example = "10")
    public ApiResult<PageResult<ContentTagResponseDTO>> pageAllTags(
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) Integer tagType,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<ContentTagResponseDTO> result = contentTagService.pageAll(
                tagName, tagType, creatorId, new PageRequest(pageNum, pageSize));
        return ApiResult.success(result);
    }
}

