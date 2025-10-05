package cn.refinex.common.file.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件分类枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum FileCategory {

    /**
     * 图片
     */
    IMAGE("IMAGE", "图片"),

    /**
     * 视频
     */
    VIDEO("VIDEO", "视频"),

    /**
     * 音频
     */
    AUDIO("AUDIO", "音频"),

    /**
     * 文档
     */
    DOCUMENT("DOCUMENT", "文档"),

    /**
     * 压缩包
     */
    ARCHIVE("ARCHIVE", "压缩包"),

    /**
     * 其他
     */
    OTHER("OTHER", "其他");

    /**
     * 分类代码
     */
    private final String code;

    /**
     * 分类描述
     */
    private final String description;

    /**
     * 根据 MIME 类型判断文件分类
     *
     * @param mimeType MIME 类型
     * @return 文件分类
     */
    public static FileCategory fromMimeType(String mimeType) {
        if (mimeType == null) {
            return OTHER;
        }
        if (mimeType.startsWith("image/")) {
            return IMAGE;
        }
        if (mimeType.startsWith("video/")) {
            return VIDEO;
        }
        if (mimeType.startsWith("audio/")) {
            return AUDIO;
        }
        if (mimeType.contains("pdf") || mimeType.contains("word") || mimeType.contains("excel")
                || mimeType.contains("powerpoint") || mimeType.contains("text")) {
            return DOCUMENT;
        }
        if (mimeType.contains("zip") || mimeType.contains("rar") || mimeType.contains("7z")
                || mimeType.contains("tar") || mimeType.contains("gzip")) {
            return ARCHIVE;
        }
        return OTHER;
    }
}

