package cn.refinex.api.platform.domain.dto.file;

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
public class FileConfirmUploadRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件 GUID
     */
    private String fileGuid;

    /**
     * 实际上传的文件大小（字节）
     */
    private Long actualFileSize;

    /**
     * 实际上传的文件 MD5
     */
    private String actualFileMd5;
}

