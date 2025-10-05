package cn.refinex.api.platform.domain.dto.file;

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
public class FileDownloadUrlRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件 GUID
     */
    private String fileGuid;

    /**
     * URL 有效期（秒，默认 3600）
     */
    private Long expiresIn;
}

