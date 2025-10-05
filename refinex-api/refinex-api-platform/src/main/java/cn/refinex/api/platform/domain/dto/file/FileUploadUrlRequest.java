package cn.refinex.api.platform.domain.dto.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件上传 URL 请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadUrlRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件 MD5（用于秒传检测）
     */
    private String fileMd5;

    /**
     * 业务类型（如 AVATAR、DOCUMENT、IMAGE）
     */
    private String bizType;

    /**
     * 业务 ID
     */
    private Long bizId;

    /**
     * 是否公开访问（0否/1是）
     */
    private Integer isPublic;

    /**
     * 存储类型（可选，不指定则使用默认配置）
     */
    private String storageType;
}

