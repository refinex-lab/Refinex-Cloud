package cn.refinex.platform.api.domain.dto.response;

import cn.refinex.platform.api.domain.model.FileInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件上传 URL 结果
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Feign API - 文件上传 URL 结果")
public class FileUploadUrlResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "文件 GUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String fileGuid;

    @Schema(description = "上传 URL", example = "https://example.com/upload/a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uploadUrl;

    @Schema(description = "URL 过期时间（秒）", example = "3600")
    private Long expiresIn;

    @Schema(description = "是否秒传（如果为 true，则无需上传，直接调用 confirmUpload）", example = "false")
    private Boolean isInstantUpload;

    @Schema(description = "秒传时的文件信息（仅当 isInstantUpload=true 时有值）")
    private FileInfoDTO fileInfo;
}

