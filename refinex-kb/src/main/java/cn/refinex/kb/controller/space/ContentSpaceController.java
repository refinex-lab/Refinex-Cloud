package cn.refinex.kb.controller.space;

import cn.refinex.common.apilog.core.annotation.LogOperation;
import cn.refinex.common.apilog.core.enums.OperateTypeEnum;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.kb.controller.space.dto.request.ContentSpaceCreateRequestDTO;
import cn.refinex.kb.controller.space.dto.request.ContentSpacePublishRequestDTO;
import cn.refinex.kb.controller.space.dto.request.ContentSpaceQueryRequestDTO;
import cn.refinex.kb.controller.space.dto.request.ContentSpaceUpdateRequestDTO;
import cn.refinex.kb.controller.space.dto.response.ContentSpaceDetailResponseDTO;
import cn.refinex.kb.controller.space.dto.response.ContentSpaceResponseDTO;
import cn.refinex.kb.service.ContentSpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内容空间控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/spaces")
@RequiredArgsConstructor
@Tag(name = "知识库空间管理", description = "内容空间管理相关接口")
public class ContentSpaceController {

    private final ContentSpaceService contentSpaceService;

    @PostMapping
    @LogOperation(operateDesc = "创建内容空间", operationType = OperateTypeEnum.CREATE)
    @Operation(summary = "创建内容空间", description = "创建新的内容空间")
    @Parameter(name = "request", description = "空间创建请求", required = true)
    public ApiResult<Long> create(@Valid @RequestBody ContentSpaceCreateRequestDTO request) {
        Long userId = LoginHelper.getUserId();
        Long spaceId = contentSpaceService.create(request, userId);
        return ApiResult.success(HttpStatusCode.CREATED, spaceId);
    }

    @PutMapping("/{spaceId}")
    @LogOperation(operateDesc = "更新内容空间", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "更新内容空间", description = "更新指定空间的信息")
    @Parameter(name = "spaceId", description = "空间ID", required = true)
    @Parameter(name = "request", description = "空间更新请求", required = true)
    public ApiResult<Boolean> update(
            @PathVariable("spaceId") @NotNull(message = "空间ID不能为空") Long spaceId,
            @Valid @RequestBody ContentSpaceUpdateRequestDTO request
    ) {
        Long userId = LoginHelper.getUserId();
        boolean success = contentSpaceService.update(spaceId, request, userId);
        return ApiResult.success(success);
    }

    @PatchMapping("/{spaceId}/publish")
    @LogOperation(operateDesc = "发布/取消发布空间", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "发布/取消发布空间", description = "发布或取消发布指定的空间")
    @Parameter(name = "spaceId", description = "空间ID", required = true)
    @Parameter(name = "request", description = "发布请求", required = true)
    public ApiResult<Boolean> publish(
            @PathVariable("spaceId") @NotNull(message = "空间ID不能为空") Long spaceId,
            @Valid @RequestBody ContentSpacePublishRequestDTO request
    ) {
        Long userId = LoginHelper.getUserId();
        boolean success = contentSpaceService.publish(spaceId, request, userId);
        return ApiResult.success(success);
    }

    @DeleteMapping("/{spaceId}")
    @LogOperation(operateDesc = "删除内容空间", operationType = OperateTypeEnum.DELETE)
    @Operation(summary = "删除内容空间", description = "删除指定的内容空间")
    @Parameter(name = "spaceId", description = "空间ID", required = true)
    public ApiResult<Void> delete(@PathVariable("spaceId") @NotNull(message = "空间ID不能为空") Long spaceId) {
        Long userId = LoginHelper.getUserId();
        contentSpaceService.delete(spaceId, userId);
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @GetMapping("/{spaceId}")
    @Operation(summary = "获取空间详情", description = "根据空间ID获取空间详细信息")
    @Parameter(name = "spaceId", description = "空间ID", required = true)
    public ApiResult<ContentSpaceDetailResponseDTO> getDetail(@PathVariable("spaceId") @NotNull(message = "空间ID不能为空") Long spaceId) {
        ContentSpaceDetailResponseDTO detail = contentSpaceService.getDetail(spaceId);
        
        // 增加浏览次数（异步处理更好，这里先简化处理）
        try {
            contentSpaceService.incrementViewCount(spaceId);
        } catch (Exception e) {
            log.warn("增加空间浏览次数失败，spaceId: {}", spaceId, e);
        }
        
        return ApiResult.success(detail);
    }

    @GetMapping("/code/{spaceCode}")
    @Operation(summary = "根据编码获取空间详情", description = "根据空间编码获取空间详细信息")
    @Parameter(name = "spaceCode", description = "空间编码", required = true)
    public ApiResult<ContentSpaceDetailResponseDTO> getDetailByCode(@PathVariable("spaceCode") @NotBlank(message = "空间编码不能为空") String spaceCode) {
        ContentSpaceDetailResponseDTO detail = contentSpaceService.getDetailByCode(spaceCode);
        
        // 增加浏览次数（异步处理更好，这里先简化处理）
        try {
            contentSpaceService.incrementViewCount(detail.getId());
        } catch (Exception e) {
            log.warn("增加空间浏览次数失败，spaceCode: {}", spaceCode, e);
        }
        
        return ApiResult.success(detail);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的空间列表", description = "获取当前用户拥有的所有空间")
    public ApiResult<List<ContentSpaceResponseDTO>> getMySpaces() {
        Long userId = LoginHelper.getUserId();
        List<ContentSpaceResponseDTO> spaces = contentSpaceService.listByOwnerId(userId);
        return ApiResult.success(spaces);
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询空间列表", description = "分页查询空间列表，支持多条件筛选")
    @Parameter(name = "queryDTO", description = "查询条件")
    @Parameter(name = "pageNum", description = "页码", required = true)
    @Parameter(name = "pageSize", description = "每页数量", required = true)
    public ApiResult<PageResult<ContentSpaceResponseDTO>> page(
            ContentSpaceQueryRequestDTO queryDTO,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        PageRequest pageRequest = new PageRequest(pageNum, pageSize);
        PageResult<ContentSpaceResponseDTO> result = contentSpaceService.page(queryDTO, pageRequest);
        return ApiResult.success(result);
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "根据拥有者获取空间列表", description = "获取指定用户拥有的所有空间")
    @Parameter(name = "ownerId", description = "拥有者用户ID", required = true)
    public ApiResult<List<ContentSpaceResponseDTO>> getSpacesByOwner(@PathVariable("ownerId") @NotNull(message = "拥有者ID不能为空") Long ownerId) {
        List<ContentSpaceResponseDTO> spaces = contentSpaceService.listByOwnerId(ownerId);
        return ApiResult.success(spaces);
    }

    @PostMapping("/{spaceId}/validate-access")
    @Operation(summary = "校验空间访问权限", description = "校验用户是否有权限访问指定空间")
    @Parameter(name = "spaceId", description = "空间ID", required = true)
    @Parameter(name = "password", description = "访问密码（密码访问时必填）")
    public ApiResult<Boolean> validateAccess(
            @PathVariable("spaceId") @NotNull(message = "空间ID不能为空") Long spaceId,
            @RequestParam(required = false) String password) {
        Long userId = LoginHelper.getUserId();
        boolean hasAccess = contentSpaceService.validateAccess(spaceId, userId, password);
        return ApiResult.success(hasAccess);
    }
}

