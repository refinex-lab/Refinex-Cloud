package cn.refinex.api.platform.client.file;

import cn.refinex.api.platform.client.file.dto.request.FileConfirmUploadRequestDTO;
import cn.refinex.api.platform.client.file.dto.request.FileInfoDTO;
import cn.refinex.api.platform.client.file.dto.request.FileUploadUrlRequestDTO;
import cn.refinex.api.platform.client.file.dto.response.FileUploadUrlResponseDTO;
import cn.refinex.common.constants.SystemFeignConstants;
import cn.refinex.common.domain.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 文件服务 OpenFeign 接口契约
 *
 * @author Refinex
 * @since 1.0.0
 */
@FeignClient(name = SystemFeignConstants.PLATFORM_SERVICE, contextId = "fileServiceClient")
@Tag(name = "文件服务 OpenFeign 接口契约", description = "定义文件服务相关的 OpenFeign 接口契约")
public interface FileRemoteService {

    @PostMapping("/file/upload-urls")
    @Operation(summary = "生成文件上传 URL", description = "生成预签名上传 URL，用于客户端直传文件")
    @Parameter(name = "request", description = "文件上传 URL 请求", required = true)
    ApiResult<FileUploadUrlResponseDTO> generateUploadUrl(@RequestBody FileUploadUrlRequestDTO request);

    @PostMapping("/file/upload-confirmations")
    @Operation(summary = "确认文件上传完成", description = "客户端上传完成后确认，更新文件状态")
    @Parameter(name = "request", description = "文件确认上传请求", required = true)
    ApiResult<FileInfoDTO> confirmUpload(@RequestBody FileConfirmUploadRequestDTO request);

    @GetMapping("/file/{fileGuid}/download-url")
    @Operation(summary = "生成文件下载 URL", description = "生成预签名下载 URL")
    @Parameter(name = "fileGuid", description = "文件 GUID", required = true)
    @Parameter(name = "expiresIn", description = "URL 有效期（秒）", required = false)
    ApiResult<String> generateDownloadUrl(
            @PathVariable("fileGuid") String fileGuid,
            @RequestParam(value = "expiresIn", required = false, defaultValue = "3600") Long expiresIn
    );

    @GetMapping("/file/{fileGuid}")
    @Operation(summary = "获取文件信息", description = "根据文件 GUID 查询文件详细信息")
    @Parameter(name = "fileGuid", description = "文件 GUID", required = true)
    ApiResult<FileInfoDTO> getFileInfo(@PathVariable("fileGuid") String fileGuid);

    @DeleteMapping("/file/{fileGuid}")
    @Operation(summary = "删除文件", description = "根据文件 GUID 删除文件")
    @Parameter(name = "fileGuid", description = "文件 GUID", required = true)
    ApiResult<Void> deleteFile(@PathVariable("fileGuid") String fileGuid);
}

