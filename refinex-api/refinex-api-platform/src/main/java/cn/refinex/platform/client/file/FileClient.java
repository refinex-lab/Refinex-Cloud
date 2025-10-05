package cn.refinex.platform.client.file;

import cn.refinex.common.constants.FeignConstants;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.client.file.dto.FileConfirmUploadRequest;
import cn.refinex.platform.client.file.dto.FileInfoDTO;
import cn.refinex.platform.client.file.dto.FileUploadUrlRequest;
import cn.refinex.platform.client.file.dto.response.FileUploadUrlResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 文件服务 Feign 客户端
 *
 * @author Refinex
 * @since 1.0.0
 */
@FeignClient(
        name = FeignConstants.PLATFORM_SERVICE,
        path = FeignConstants.PLATFORM_API_PREFIX
)
@Tag(name = "文件服务 Feign API")
public interface FileClient {

    @PostMapping("/files/upload-url")
    @Operation(summary = "生成文件上传 URL")
    @Parameter(name = "request", description = "上传请求", required = true)
    ApiResult<FileUploadUrlResponse> generateUploadUrl(@RequestBody FileUploadUrlRequest request);

    @PostMapping("/files/confirm-upload")
    @Operation(summary = "确认文件上传完成")
    @Parameter(name = "request", description = "确认请求", required = true)
    ApiResult<FileInfoDTO> confirmUpload(@RequestBody FileConfirmUploadRequest request);

    @GetMapping("/files/download-url/{fileGuid}")
    @Operation(summary = "生成文件下载 URL")
    @Parameter(name = "fileGuid", description = "文件 GUID", required = true)
    @Parameter(name = "expiresIn", description = "URL 有效期（秒）", required = false)
    ApiResult<String> generateDownloadUrl(
            @PathVariable("fileGuid") String fileGuid,
            @RequestParam(value = "expiresIn", required = false, defaultValue = "3600") Long expiresIn
    );

    @GetMapping("/files/{fileGuid}")
    @Operation(summary = "获取文件信息")
    @Parameter(name = "fileGuid", description = "文件 GUID", required = true)
    ApiResult<FileInfoDTO> getFileInfo(@PathVariable("fileGuid") String fileGuid);

    @DeleteMapping("/files/{fileGuid}")
    @Operation(summary = "删除文件")
    @Parameter(name = "fileGuid", description = "文件 GUID", required = true)
    ApiResult<Void> deleteFile(@PathVariable("fileGuid") String fileGuid);
}

