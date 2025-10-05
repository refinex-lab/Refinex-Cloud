package cn.refinex.platform.api;

import cn.dev33.satoken.stp.StpUtil;
import cn.refinex.api.platform.constants.FileConstants;
import cn.refinex.api.platform.domain.dto.file.*;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Feign - 文件服务 API 控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping(FileConstants.FILE_API_PREFIX)
@RequiredArgsConstructor
@Tag(name = "文件服务 API", description = "提供给其他微服务的远程调用接口")
public class FileApiController {

    private final FileService fileService;

    /**
     * 生成文件上传 URL
     *
     * @param request 上传请求
     * @return 上传 URL 结果
     */
    @PostMapping("/upload-url")
    @Operation(summary = "生成文件上传 URL", description = "生成预签名上传 URL，支持 MD5 秒传检测")
    public ApiResult<FileUploadUrlResult> generateUploadUrl(@RequestBody FileUploadUrlRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        FileUploadUrlResult result = fileService.generateUploadUrl(request, userId);
        return ApiResult.success(result);
    }

    /**
     * 确认文件上传完成
     *
     * @param request 确认请求
     * @return 文件信息
     */
    @PostMapping("/confirm-upload")
    @Operation(summary = "确认文件上传完成", description = "客户端上传完成后调用此接口确认")
    public ApiResult<FileInfoDTO> confirmUpload(@RequestBody FileConfirmUploadRequest request) {
        FileInfoDTO fileInfo = fileService.confirmUpload(request);
        return ApiResult.success(fileInfo);
    }

    /**
     * 生成文件下载 URL
     *
     * @param fileGuid  文件 GUID
     * @param expiresIn URL 有效期（秒）
     * @return 下载 URL
     */
    @GetMapping("/download-url/{fileGuid}")
    @Operation(summary = "生成文件下载 URL", description = "生成预签名下载 URL")
    public ApiResult<String> generateDownloadUrl(
            @PathVariable("fileGuid") String fileGuid,
            @RequestParam(value = "expiresIn", required = false, defaultValue = "3600") Long expiresIn) {
        String downloadUrl = fileService.generateDownloadUrl(fileGuid, expiresIn);
        return ApiResult.success(downloadUrl);
    }

    /**
     * 获取文件信息
     *
     * @param fileGuid 文件 GUID
     * @return 文件信息
     */
    @GetMapping("/{fileGuid}")
    @Operation(summary = "获取文件信息", description = "根据文件 GUID 获取文件详细信息")
    public ApiResult<FileInfoDTO> getFileInfo(@PathVariable("fileGuid") String fileGuid) {
        FileInfoDTO fileInfo = fileService.getFileInfo(fileGuid);
        return ApiResult.success(fileInfo);
    }

    /**
     * 删除文件
     *
     * @param fileGuid 文件 GUID
     * @return 删除结果
     */
    @DeleteMapping("/{fileGuid}")
    @Operation(summary = "删除文件", description = "逻辑删除文件（检查引用计数）")
    public ApiResult<Void> deleteFile(@PathVariable("fileGuid") String fileGuid) {
        fileService.deleteFile(fileGuid);
        return ApiResult.success(null);
    }
}

