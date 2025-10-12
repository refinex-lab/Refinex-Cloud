package cn.refinex.platform.service;

import cn.refinex.platform.domain.dto.request.FileConfirmUploadRequest;
import cn.refinex.platform.domain.model.FileInfoDTO;
import cn.refinex.platform.domain.dto.request.FileUploadUrlRequest;
import cn.refinex.platform.domain.dto.response.FileUploadUrlResponse;

/**
 * 文件服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface FileService {

    /**
     * 生成文件上传 URL
     *
     * @param request 上传请求
     * @param userId  用户 ID
     * @return 上传 URL 结果
     */
    FileUploadUrlResponse generateUploadUrl(FileUploadUrlRequest request, Long userId);

    /**
     * 确认文件上传完成
     *
     * @param request 确认请求
     * @return 文件信息
     */
    FileInfoDTO confirmUpload(FileConfirmUploadRequest request);

    /**
     * 生成文件下载 URL
     *
     * @param fileGuid  文件 GUID
     * @param expiresIn URL 有效期（秒）
     * @return 下载 URL
     */
    String generateDownloadUrl(String fileGuid, Long expiresIn);

    /**
     * 获取文件信息
     *
     * @param fileGuid 文件 GUID
     * @return 文件信息
     */
    FileInfoDTO getFileInfo(String fileGuid);

    /**
     * 删除文件
     *
     * @param fileGuid 文件 GUID
     */
    void deleteFile(String fileGuid);
}
