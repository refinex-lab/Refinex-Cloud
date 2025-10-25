package cn.refinex.platform.controller.file;

import cn.dev33.satoken.stp.StpUtil;
import cn.refinex.platform.controller.file.dto.request.FileConfirmUploadRequestDTO;
import cn.refinex.platform.controller.file.dto.request.FileInfoDTO;
import cn.refinex.platform.controller.file.dto.request.FileUploadUrlRequestDTO;
import cn.refinex.platform.controller.file.dto.response.FileUploadUrlResponseDTO;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 文件服务控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "文件服务控制器", description = "提供文件上传、确认、下载、查询、删除等功能")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload-urls")
    @Operation(summary = "生成文件上传 URL", description = "生成预签名上传 URL，用于客户端直传文件")
    @Parameter(name = "request", description = "文件上传 URL 请求", required = true)
    public ApiResult<FileUploadUrlResponseDTO> generateUploadUrl(@RequestBody FileUploadUrlRequestDTO request) {
        Long userId = StpUtil.getLoginIdAsLong();
        FileUploadUrlResponseDTO result = fileService.generateUploadUrl(request, userId);
        return ApiResult.success(result);
    }

    @PostMapping("/upload-confirmations")
    @Operation(summary = "确认文件上传完成", description = "客户端上传完成后确认，更新文件状态")
    @Parameter(name = "request", description = "文件确认上传请求", required = true)
    public ApiResult<FileInfoDTO> confirmUpload(@RequestBody FileConfirmUploadRequestDTO request) {
        FileInfoDTO fileInfo = fileService.confirmUpload(request);
        return ApiResult.success(fileInfo);
    }

    @GetMapping("/{fileGuid}/download-url")
    @Operation(summary = "生成文件下载 URL", description = "生成预签名下载 URL")
    @Parameter(name = "fileGuid", description = "文件 GUID", required = true)
    @Parameter(name = "expiresIn", description = "URL 有效期（秒）", required = false)
    public ApiResult<String> generateDownloadUrl(
            @PathVariable("fileGuid") String fileGuid,
            @RequestParam(value = "expiresIn", required = false, defaultValue = "3600") Long expiresIn
    ) {
        String downloadUrl = fileService.generateDownloadUrl(fileGuid, expiresIn);
        return ApiResult.success(downloadUrl);
    }

    @GetMapping("/{fileGuid}")
    @Operation(summary = "获取文件信息", description = "根据文件 GUID 查询文件详细信息")
    @Parameter(name = "fileGuid", description = "文件 GUID", required = true)
    public ApiResult<FileInfoDTO> getFileInfo(@PathVariable("fileGuid") String fileGuid) {
        FileInfoDTO fileInfo = fileService.getFileInfo(fileGuid);
        return ApiResult.success(fileInfo);
    }

    @DeleteMapping("/{fileGuid}")
    @Operation(summary = "删除文件", description = "根据文件 GUID 删除文件")
    @Parameter(name = "fileGuid", description = "文件 GUID", required = true)
    public ApiResult<Void> deleteFile(@PathVariable("fileGuid") String fileGuid) {
        fileService.deleteFile(fileGuid);
        return ApiResult.success(cn.refinex.common.enums.HttpStatusCode.NO_CONTENT, null);
    }
}

