package cn.refinex.platform.controller.file;

import cn.dev33.satoken.stp.StpUtil;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.client.file.FileClient;
import cn.refinex.platform.client.file.dto.FileConfirmUploadRequest;
import cn.refinex.platform.client.file.dto.FileInfoDTO;
import cn.refinex.platform.client.file.dto.FileUploadUrlRequest;
import cn.refinex.platform.client.file.dto.response.FileUploadUrlResponse;
import cn.refinex.platform.service.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 文件服务 Feign API 实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController implements FileClient {

    private final FileService fileService;

    /**
     * 生成文件上传 URL
     *
     * @param request 上传请求
     * @return 上传 URL 结果
     */
    @Override
    public ApiResult<FileUploadUrlResponse> generateUploadUrl(@RequestBody FileUploadUrlRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        FileUploadUrlResponse result = fileService.generateUploadUrl(request, userId);
        return ApiResult.success(result);
    }

    /**
     * 确认文件上传完成
     *
     * @param request 确认请求
     * @return 文件信息
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
    public ApiResult<Void> deleteFile(@PathVariable("fileGuid") String fileGuid) {
        fileService.deleteFile(fileGuid);
        return ApiResult.success(null);
    }
}

