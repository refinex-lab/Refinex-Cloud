package cn.refinex.common.file.exception;

import cn.refinex.common.exception.code.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件模块错误码
 * <p>
 * 错误码格式：FILE-NNNN
 * <ul>
 * <li>业务异常：1000-1999</li>
 * <li>系统异常：2000-2999</li>
 * </ul>
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum FileErrorCode implements ErrorCode {

    // ==================== 业务异常（1000-1999） ====================

    /**
     * 文件不存在
     */
    FILE_NOT_FOUND("FILE-1001", "文件不存在"),

    /**
     * 文件上传失败
     */
    FILE_UPLOAD_FAILED("FILE-1002", "文件上传失败"),

    /**
     * 文件下载失败
     */
    FILE_DOWNLOAD_FAILED("FILE-1003", "文件下载失败"),

    /**
     * 文件删除失败
     */
    FILE_DELETE_FAILED("FILE-1004", "文件删除失败"),

    /**
     * 文件大小超过限制
     */
    FILE_SIZE_EXCEEDED("FILE-1005", "文件大小超过限制"),

    /**
     * 文件类型不允许
     */
    FILE_TYPE_NOT_ALLOWED("FILE-1006", "文件类型不允许"),

    /**
     * 文件 MD5 校验失败
     */
    FILE_MD5_MISMATCH("FILE-1007", "文件 MD5 校验失败"),

    /**
     * 分片上传失败
     */
    FILE_CHUNK_UPLOAD_FAILED("FILE-1008", "分片上传失败"),

    /**
     * 分片合并失败
     */
    FILE_CHUNK_MERGE_FAILED("FILE-1009", "分片合并失败"),

    /**
     * 文件引用计数不为零，无法删除
     */
    FILE_REF_COUNT_NOT_ZERO("FILE-1010", "文件引用计数不为零，无法删除"),

    // ==================== 系统异常（2000-2999） ====================

    /**
     * 存储配置不存在
     */
    STORAGE_CONFIG_NOT_FOUND("FILE-2001", "存储配置不存在"),

    /**
     * 存储初始化失败
     */
    STORAGE_INIT_FAILED("FILE-2002", "存储初始化失败"),

    /**
     * 存储操作失败
     */
    STORAGE_OPERATION_FAILED("FILE-2003", "存储操作失败"),

    /**
     * 缩略图生成失败
     */
    THUMBNAIL_GENERATE_FAILED("FILE-2004", "缩略图生成失败");

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误信息
     */
    private final String message;
}

