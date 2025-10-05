package cn.refinex.api.platform.client;

import cn.refinex.api.platform.constants.FileConstants;
import cn.refinex.api.platform.domain.dto.file.*;
import cn.refinex.common.constants.FeignClientConstants;
import cn.refinex.common.domain.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 文件服务 Feign 客户端
 *
 * @author Refinex
 * @since 1.0.0
 */
@FeignClient(
        name = FeignClientConstants.PLATFORM_SERVICE,
        path = FileConstants.FILE_API_PREFIX,
        fallbackFactory = FileFeignClientFallback.class
)
public interface FileFeignClient {

    /**
     * 生成文件上传 URL
     *
     * @param request 上传请求
     * @return 上传 URL 结果
     */
    @PostMapping("/upload-url")
    ApiResult<FileUploadUrlResult> generateUploadUrl(@RequestBody FileUploadUrlRequest request);

    /**
     * 确认文件上传完成
     *
     * @param request 确认请求
     * @return 文件信息
     */
    @PostMapping("/confirm-upload")
    ApiResult<FileInfoDTO> confirmUpload(@RequestBody FileConfirmUploadRequest request);

    /**
     * 生成文件下载 URL
     *
     * @param fileGuid  文件 GUID
     * @param expiresIn URL 有效期（秒）
     * @return 下载 URL
     */
    @GetMapping("/download-url/{fileGuid}")
    ApiResult<String> generateDownloadUrl(
            @PathVariable("fileGuid") String fileGuid,
            @RequestParam(value = "expiresIn", required = false, defaultValue = "3600") Long expiresIn
    );

    /**
     * 获取文件信息
     *
     * @param fileGuid 文件 GUID
     * @return 文件信息
     */
    @GetMapping("/{fileGuid}")
    ApiResult<FileInfoDTO> getFileInfo(@PathVariable("fileGuid") String fileGuid);

    /**
     * 删除文件
     *
     * @param fileGuid 文件 GUID
     * @return 删除结果
     */
    @DeleteMapping("/{fileGuid}")
    ApiResult<Void> deleteFile(@PathVariable("fileGuid") String fileGuid);
}

