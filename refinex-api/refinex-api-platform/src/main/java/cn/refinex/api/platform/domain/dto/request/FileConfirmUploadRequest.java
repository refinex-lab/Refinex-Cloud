package cn.refinex.api.platform.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件上传确认请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Feign API - 文件上传确认请求")
public class FileConfirmUploadRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "文件 GUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String fileGuid;

    @Schema(description = "实际上传的文件大小（字节）", example = "102400")
    private Long actualFileSize;

    @Schema(description = "实际上传的文件 MD5", example = "1234567890abcdef1234567890abcdef")
    private String actualFileMd5;
}

