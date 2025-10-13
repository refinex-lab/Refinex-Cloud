package cn.refinex.common.file.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * 文件模块错误信息
 *
 * @author Refinex
 * @since 1.0.0
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileErrorMessageConstants {

    /**
     * 文件不存在
     */
    public static final String FILE_NOT_FOUND = "文件不存在";

    /**
     * 文件上传失败
     */
    public static final String FILE_UPLOAD_FAILED = "文件上传失败";

    /**
     * 文件下载失败
     */
    public static final String FILE_DOWNLOAD_FAILED = "文件下载失败";

    /**
     * 文件删除失败
     */
    public static final String FILE_DELETE_FAILED = "文件删除失败";

    /**
     * 文件大小超过限制
     */
    public static final String FILE_SIZE_EXCEEDED = "文件大小超过限制";

    /**
     * 文件类型不允许
     */
    public static final String FILE_TYPE_NOT_ALLOWED = "文件类型不允许";

    /**
     * 文件 MD5 校验失败
     */
    public static final String FILE_MD5_MISMATCH = "文件 MD5 校验失败";

    /**
     * 分片上传失败
     */
    public static final String FILE_CHUNK_UPLOAD_FAILED = "分片上传失败";

    /**
     * 分片合并失败
     */
    public static final String FILE_CHUNK_MERGE_FAILED = "分片合并失败";

    /**
     * 文件引用计数不为零，无法删除
     */
    public static final String FILE_REF_COUNT_NOT_ZERO = "文件引用计数不为零，无法删除";

    /**
     * 存储配置不存在
     */
    public static final String STORAGE_CONFIG_NOT_FOUND = "存储配置不存在";

    /**
     * 存储初始化失败
     */
    public static final String STORAGE_INIT_FAILED = "存储初始化失败";

    /**
     * 存储操作失败
     */
    public static final String STORAGE_OPERATION_FAILED = "存储操作失败";

    /**
     * 缩略图生成失败
     */
    public static final String THUMBNAIL_GENERATE_FAILED = "缩略图生成失败";

}

