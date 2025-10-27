package cn.refinex.kb.constants;

/**
 * 文档模块常量
 *
 * @author Refinex
 * @since 1.0.0
 */
public final class DocumentConstants {

    /**
     * 文档摘要最大长度
     */
    public static final int SUMMARY_MAX_LENGTH = 200;

    /**
     * 文档标题最大长度
     */
    public static final int TITLE_MAX_LENGTH = 200;

    /**
     * 文档内容最大长度（字符数）
     * 100万字符，约50万汉字
     */
    public static final int CONTENT_MAX_LENGTH = 1_000_000;

    /**
     * SEO 关键词最大长度
     */
    public static final int SEO_KEYWORDS_MAX_LENGTH = 200;

    /**
     * SEO 描述最大长度
     */
    public static final int SEO_DESCRIPTION_MAX_LENGTH = 500;

    /**
     * 文档默认版本号
     */
    public static final int DEFAULT_VERSION_NUMBER = 1;

    /**
     * 版本历史默认保留数量
     */
    public static final int DEFAULT_VERSION_KEEP_COUNT = 10;

    /**
     * 版本历史最大保留数量
     */
    public static final int MAX_VERSION_KEEP_COUNT = 100;

    /**
     * 文档标签最大数量
     */
    public static final int MAX_TAG_COUNT = 20;

    /**
     * 变更说明最大长度
     */
    public static final int CHANGE_SUMMARY_MAX_LENGTH = 500;

    /**
     * 文档 GUID 前缀
     */
    public static final String DOC_GUID_PREFIX = "DOC_";

    private DocumentConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

