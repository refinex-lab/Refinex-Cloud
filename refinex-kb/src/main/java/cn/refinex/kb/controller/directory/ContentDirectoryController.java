package cn.refinex.kb.controller.directory;

import cn.refinex.common.apilog.core.annotation.LogOperation;
import cn.refinex.common.apilog.core.enums.OperateTypeEnum;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.kb.controller.directory.dto.request.ContentDirectoryBatchSortRequestDTO;
import cn.refinex.kb.controller.directory.dto.request.ContentDirectoryCreateRequestDTO;
import cn.refinex.kb.controller.directory.dto.request.ContentDirectoryMoveRequestDTO;
import cn.refinex.kb.controller.directory.dto.request.ContentDirectoryUpdateRequestDTO;
import cn.refinex.kb.controller.directory.dto.response.ContentDirectoryResponseDTO;
import cn.refinex.kb.controller.directory.dto.response.ContentDirectoryTreeResponseDTO;
import cn.refinex.kb.controller.directory.dto.response.ContentTreeNodeResponseDTO;
import cn.refinex.kb.service.ContentDirectoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内容目录控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/directory")
@RequiredArgsConstructor
@Tag(name = "知识库目录管理", description = "知识库目录的增删改查和树形管理")
public class ContentDirectoryController {

    private final ContentDirectoryService directoryService;

    @PostMapping("/create")
    @LogOperation(operateDesc = "创建目录", operationType = OperateTypeEnum.CREATE)
    @Operation(summary = "创建目录", description = "在指定空间和父目录下创建新目录")
    public ApiResult<Long> createDirectory(@Valid @RequestBody ContentDirectoryCreateRequestDTO requestDTO) {
        Long userId = LoginHelper.getUserId();
        Long directoryId = directoryService.createDirectory(requestDTO, userId);
        return ApiResult.success(directoryId);
    }

    @PutMapping("/update")
    @LogOperation(operateDesc = "更新目录", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "更新目录", description = "更新目录名称、排序等信息")
    public ApiResult<String> updateDirectory(@Valid @RequestBody ContentDirectoryUpdateRequestDTO requestDTO) {
        Long userId = LoginHelper.getUserId();
        directoryService.updateDirectory(requestDTO, userId);
        return ApiResult.success("更新目录成功");
    }

    @PutMapping("/move")
    @LogOperation(operateDesc = "移动目录", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "移动目录", description = "移动目录到新的父目录下，支持拖拽排序和层级迁移")
    public ApiResult<String> moveDirectory(@Valid @RequestBody ContentDirectoryMoveRequestDTO requestDTO) {
        Long userId = LoginHelper.getUserId();
        directoryService.moveDirectory(requestDTO, userId);
        return ApiResult.success("移动目录成功");
    }

    @PutMapping("/batch-sort")
    @LogOperation(operateDesc = "批量更新目录排序", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "批量更新目录排序", description = "批量更新同级目录的排序值")
    public ApiResult<String> batchUpdateSort(@Valid @RequestBody ContentDirectoryBatchSortRequestDTO requestDTO) {
        Long userId = LoginHelper.getUserId();
        directoryService.batchUpdateSort(requestDTO, userId);
        return ApiResult.success("批量更新排序成功");
    }

    @DeleteMapping("/{directoryId}")
    @LogOperation(operateDesc = "删除目录", operationType = OperateTypeEnum.DELETE)
    @Operation(summary = "删除目录", description = "删除目录及其所有子目录（级联删除）")
    public ApiResult<String> deleteDirectory(
            @Parameter(description = "目录ID", required = true, example = "1")
            @PathVariable Long directoryId) {
        Long userId = LoginHelper.getUserId();
        directoryService.deleteDirectory(directoryId, userId);
        return ApiResult.success("删除目录成功");
    }

    @GetMapping("/{directoryId}")
    @Operation(summary = "查询目录详情", description = "根据ID查询目录详细信息")
    public ApiResult<ContentDirectoryResponseDTO> getDirectoryById(
            @Parameter(description = "目录ID", required = true, example = "1")
            @PathVariable Long directoryId) {
        ContentDirectoryResponseDTO directory = directoryService.getDirectoryById(directoryId);
        return ApiResult.success(directory);
    }

    @GetMapping("/tree/{spaceId}")
    @Operation(summary = "查询目录树", description = "查询指定空间下的完整目录树形结构")
    public ApiResult<List<ContentDirectoryTreeResponseDTO>> getDirectoryTree(
            @Parameter(description = "空间ID", required = true, example = "100")
            @PathVariable Long spaceId) {
        List<ContentDirectoryTreeResponseDTO> tree = directoryService.getDirectoryTree(spaceId);
        return ApiResult.success(tree);
    }

    @GetMapping("/list/{spaceId}")
    @Operation(summary = "查询目录列表", description = "查询指定空间下的所有目录（平铺列表）")
    public ApiResult<List<ContentDirectoryResponseDTO>> getDirectoryList(
            @Parameter(description = "空间ID", required = true, example = "100")
            @PathVariable Long spaceId) {
        List<ContentDirectoryResponseDTO> list = directoryService.getDirectoryList(spaceId);
        return ApiResult.success(list);
    }

    @GetMapping("/children/{spaceId}/{parentId}")
    @Operation(summary = "查询子目录列表", description = "查询指定父目录下的直接子目录")
    public ApiResult<List<ContentDirectoryResponseDTO>> getChildDirectories(
            @Parameter(description = "空间ID", required = true, example = "100")
            @PathVariable Long spaceId,
            @Parameter(description = "父目录ID", required = true, example = "0")
            @PathVariable Long parentId) {
        List<ContentDirectoryResponseDTO> children = directoryService.getChildDirectories(spaceId, parentId);
        return ApiResult.success(children);
    }

    @GetMapping("/tree-with-docs/{spaceId}")
    @Operation(summary = "查询目录树（包含文档节点）", description = "返回统一的树结构，包含目录和文档节点，用于前端树形展示")
    public ApiResult<List<ContentTreeNodeResponseDTO>> getTreeWithDocuments(
            @Parameter(description = "空间ID", required = true, example = "100")
            @PathVariable Long spaceId) {

        Long userId = LoginHelper.getUserId();
        List<ContentTreeNodeResponseDTO> tree = directoryService.getTreeWithDocuments(spaceId, userId);
        return ApiResult.success(tree);
    }
}

