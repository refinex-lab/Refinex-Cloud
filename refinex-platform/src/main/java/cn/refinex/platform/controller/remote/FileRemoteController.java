package cn.refinex.platform.controller.remote;

import cn.dev33.satoken.stp.StpUtil;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.api.platform.client.file.dto.request.FileConfirmUploadRequestDTO;
import cn.refinex.api.platform.client.file.dto.request.FileUploadUrlRequestDTO;
import cn.refinex.api.platform.client.file.dto.response.FileUploadUrlResponseDTO;
import cn.refinex.api.platform.client.file.dto.request.FileInfoDTO;
import cn.refinex.api.platform.client.file.FileRemoteService;
import cn.refinex.platform.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件服务 OpenFeign 接口实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FileRemoteController implements FileRemoteService {

    private final FileService fileService;

    /**
     * 生成文件上传 URL
     *
     * @param request 上传请求
     * @return 上传 URL 结果
     */
    @Override
    public ApiResult<FileUploadUrlResponseDTO> generateUploadUrl(FileUploadUrlRequestDTO request) {
        Long userId = StpUtil.getLoginIdAsLong();
        FileUploadUrlResponseDTO result = fileService.generateUploadUrl(request, userId);
        return ApiResult.success(result);
    }

    /**
     * 确认文件上传完成
     *
     * @param request 确认请求
     * @return 文件信息
     */
    @Override
    public ApiResult<FileInfoDTO> confirmUpload(FileConfirmUploadRequestDTO request) {
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
    public ApiResult<String> generateDownloadUrl(String fileGuid, Long expiresIn) {
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
    public ApiResult<FileInfoDTO> getFileInfo(String fileGuid) {
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
    public ApiResult<Void> deleteFile(String fileGuid) {
        fileService.deleteFile(fileGuid);
        return ApiResult.success(cn.refinex.common.enums.HttpStatusCode.NO_CONTENT, null);
    }
}

