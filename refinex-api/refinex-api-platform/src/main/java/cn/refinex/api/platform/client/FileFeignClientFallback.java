package cn.refinex.api.platform.client;

import cn.refinex.api.platform.domain.dto.file.*;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.exception.code.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 文件服务 Feign 客户端降级工厂
 * <p>
 * 当文件服务调用失败时，提供降级响应
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
public class FileFeignClientFallback implements FallbackFactory<FileFeignClient> {

    @Override
    public FileFeignClient create(Throwable cause) {
        log.error("FileFeignClient 调用失败", cause);

        return new FileFeignClient() {
            @Override
            public ApiResult<FileUploadUrlResult> generateUploadUrl(FileUploadUrlRequest request) {
                log.error("生成上传 URL 失败，返回降级响应");
                return ApiResult.failure(ResultCode.INTERNAL_ERROR.getCode(), "文件服务暂时不可用");
            }

            @Override
            public ApiResult<FileInfoDTO> confirmUpload(FileConfirmUploadRequest request) {
                log.error("确认上传失败，返回降级响应");
                return ApiResult.failure(ResultCode.INTERNAL_ERROR.getCode(), "文件服务暂时不可用");
            }

            @Override
            public ApiResult<String> generateDownloadUrl(String fileGuid, Long expiresIn) {
                log.error("生成下载 URL 失败，返回降级响应");
                return ApiResult.failure(ResultCode.INTERNAL_ERROR.getCode(), "文件服务暂时不可用");
            }

            @Override
            public ApiResult<FileInfoDTO> getFileInfo(String fileGuid) {
                log.error("获取文件信息失败，返回降级响应");
                return ApiResult.failure(ResultCode.INTERNAL_ERROR.getCode(), "文件服务暂时不可用");
            }

            @Override
            public ApiResult<Void> deleteFile(String fileGuid) {
                log.error("删除文件失败，返回降级响应");
                return ApiResult.failure(ResultCode.INTERNAL_ERROR.getCode(), "文件服务暂时不可用");
            }
        };
    }
}

