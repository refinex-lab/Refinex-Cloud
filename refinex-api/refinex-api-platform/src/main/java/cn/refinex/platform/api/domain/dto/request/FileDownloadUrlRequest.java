package cn.refinex.platform.api.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件下载 URL 请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Feign API - 文件下载 URL 请求")
public class FileDownloadUrlRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "文件 GUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String fileGuid;

    @Schema(description = "URL 有效期（秒，默认 3600）", example = "3600")
    private Long expiresIn;
}

