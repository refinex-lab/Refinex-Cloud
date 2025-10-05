package cn.refinex.api.platform.domain.dto.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件信息 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件 GUID
     */
    private String fileGuid;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件扩展名
     */
    private String fileExtension;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型（MIME Type）
     */
    private String fileType;

    /**
     * 存储策略
     */
    private String storageStrategy;

    /**
     * 访问 URL
     */
    private String accessUrl;

    /**
     * 缩略图 URL
     */
    private String thumbnailUrl;

    /**
     * 文件 MD5
     */
    private String fileMd5;

    /**
     * 上传者 ID
     */
    private Long uploaderId;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务 ID
     */
    private Long bizId;

    /**
     * 是否公开
     */
    private Integer isPublic;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

