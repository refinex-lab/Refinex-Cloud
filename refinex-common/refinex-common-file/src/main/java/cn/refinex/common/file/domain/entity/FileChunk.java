package cn.refinex.common.file.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件分片实体
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
public class FileChunk {

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 上传任务 ID
     */
    private String uploadId;

    /**
     * 文件 MD5
     */
    private String fileMd5;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件总大小
     */
    private Long fileSize;

    /**
     * 分片大小
     */
    private Integer chunkSize;

    /**
     * 总分片数
     */
    private Integer totalChunks;

    /**
     * 已上传分片号列表（JSON）
     */
    private List<Integer> uploadedChunks;

    /**
     * 合并状态（0上传中/1已合并/2合并失败）
     */
    private Integer mergeStatus;

    /**
     * 上传者 ID
     */
    private Long uploaderId;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 创建人 ID
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人 ID
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

