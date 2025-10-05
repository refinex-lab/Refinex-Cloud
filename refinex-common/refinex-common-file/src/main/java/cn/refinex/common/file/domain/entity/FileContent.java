package cn.refinex.common.file.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件内容实体（数据库存储）
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
public class FileContent {

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 文件 ID
     */
    private Long fileId;

    /**
     * 文件二进制内容
     */
    private byte[] contentData;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

