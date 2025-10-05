package cn.refinex.api.platform.domain.dto.file;

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
public class FileUploadUrlResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件 GUID（预分配）
     */
    private String fileGuid;

    /**
     * 上传 URL（预签名 URL）
     */
    private String uploadUrl;

    /**
     * URL 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 是否秒传（如果为 true，则无需上传，直接调用 confirmUpload）
     */
    private Boolean isInstantUpload;

    /**
     * 秒传时的文件信息（仅当 isInstantUpload=true 时有值）
     */
    private FileInfoDTO fileInfo;
}

