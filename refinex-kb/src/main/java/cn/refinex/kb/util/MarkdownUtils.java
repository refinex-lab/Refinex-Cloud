package cn.refinex.kb.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown 工具类
 * <p>
 * 提供 Markdown 内容的解析、统计和处理功能
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class MarkdownUtils {

    /**
     * 平均阅读速度（字/分钟）
     * 中文约为 300-500 字/分钟，这里取 400
     */
    private static final int AVERAGE_READING_SPEED = 400;

    /**
     * Markdown 图片语法正则
     */
    private static final Pattern IMAGE_PATTERN = Pattern.compile("!\\[.*?]\\(.*?\\)");

    /**
     * Markdown 链接语法正则
     */
    private static final Pattern LINK_PATTERN = Pattern.compile("\\[.*?]\\(.*?\\)");

    /**
     * Markdown 代码块正则（```...```）
     */
    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("```[\\s\\S]*?```");

    /**
     * Markdown 行内代码正则（`...`）
     */
    private static final Pattern INLINE_CODE_PATTERN = Pattern.compile("`[^`]+`");

    /**
     * Markdown 标题正则（# ... ######）
     */
    private static final Pattern HEADING_PATTERN = Pattern.compile("^#{1,6}\\s+(.*)$", Pattern.MULTILINE);

    /**
     * HTML 标签正则
     */
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");

    /**
     * 统计 Markdown 内容的字数
     * <p>
     * 规则：
     * 1. 移除 Markdown 语法标记
     * 2. 移除 HTML 标签
     * 3. 统计中文字符和英文单词数量
     * </p>
     *
     * @param markdown Markdown 内容
     * @return 字数统计
     */
    public static int countWords(String markdown) {
        if (!StringUtils.hasText(markdown)) {
            return 0;
        }

        try {
            // 1. 移除代码块（代码块内容不计入字数）
            String content = CODE_BLOCK_PATTERN.matcher(markdown).replaceAll("");

            // 2. 移除行内代码
            content = INLINE_CODE_PATTERN.matcher(content).replaceAll("");

            // 3. 移除图片语法
            content = IMAGE_PATTERN.matcher(content).replaceAll("");

            // 4. 移除链接语法，保留链接文本
            content = LINK_PATTERN.matcher(content).replaceAll(matchResult -> {
                String linkText = matchResult.group();
                int start = linkText.indexOf('[') + 1;
                int end = linkText.indexOf(']');
                return end > start ? linkText.substring(start, end) : "";
            });

            // 5. 移除标题标记，保留标题文本
            content = HEADING_PATTERN.matcher(content).replaceAll("$1");

            // 6. 移除 HTML 标签
            content = HTML_TAG_PATTERN.matcher(content).replaceAll("");

            // 7. 移除 Markdown 其他格式标记（粗体、斜体等）
            content = content.replaceAll("[*_~`#>\\-+]", "");

            // 8. 统计字数
            return countWordsInPlainText(content);
        } catch (Exception e) {
            log.error("统计 Markdown 字数失败", e);
            return markdown.length();
        }
    }

    /**
     * 统计纯文本的字数
     * <p>
     * 规则：
     * - 中文字符每个算1个字
     * - 英文单词按空格分割统计
     * </p>
     *
     * @param text 纯文本
     * @return 字数
     */
    private static int countWordsInPlainText(String text) {
        if (!StringUtils.hasText(text)) {
            return 0;
        }

        int chineseCount = 0;
        int englishWordCount = 0;

        // 统计中文字符
        for (char c : text.toCharArray()) {
            if (isChinese(c)) {
                chineseCount++;
            }
        }

        // 统计英文单词（移除中文字符后按空格分割）
        String englishText = text.replaceAll("[\\u4e00-\\u9fa5]", " ");
        String[] words = englishText.split("\\s+");
        for (String word : words) {
            if (word.matches("[a-zA-Z]+")) {
                englishWordCount++;
            }
        }

        return chineseCount + englishWordCount;
    }

    /**
     * 判断字符是否为中文
     *
     * @param c 字符
     * @return 是否为中文
     */
    private static boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;
    }

    /**
     * 计算预计阅读时长（单位：分钟）
     * <p>
     * 基于字数和平均阅读速度计算，最小为1分钟
     * </p>
     *
     * @param wordCount 字数
     * @return 预计阅读时长（分钟）
     */
    public static int calculateReadDuration(int wordCount) {
        if (wordCount <= 0) {
            return 0;
        }

        int minutes = (int) Math.ceil((double) wordCount / AVERAGE_READING_SPEED);
        return Math.max(1, minutes);
    }

    /**
     * 计算预计阅读时长（根据 Markdown 内容）
     *
     * @param markdown Markdown 内容
     * @return 预计阅读时长（分钟）
     */
    public static int calculateReadDuration(String markdown) {
        int wordCount = countWords(markdown);
        return calculateReadDuration(wordCount);
    }

    /**
     * 提取 Markdown 内容的摘要
     * <p>
     * 规则：
     * 1. 移除 Markdown 语法
     * 2. 取前 N 个字符
     * 3. 避免截断中间的单词
     * </p>
     *
     * @param markdown  Markdown 内容
     * @param maxLength 最大长度
     * @return 摘要文本
     */
    public static String extractSummary(String markdown, int maxLength) {
        if (!StringUtils.hasText(markdown)) {
            return "";
        }

        try {
            // 移除 Markdown 语法，获取纯文本
            String plainText = toPlainText(markdown);

            if (plainText.length() <= maxLength) {
                return plainText;
            }

            // 截取指定长度
            String summary = plainText.substring(0, maxLength);

            // 避免在单词中间截断，回退到最近的空格
            int lastSpace = summary.lastIndexOf(' ');
            if (lastSpace > maxLength * 0.8) { // 如果空格位置不是太靠前
                summary = summary.substring(0, lastSpace);
            }

            return summary + "...";
        } catch (Exception e) {
            log.error("提取 Markdown 摘要失败", e);
            return markdown.substring(0, Math.min(maxLength, markdown.length())) + "...";
        }
    }

    /**
     * 将 Markdown 转换为纯文本
     * <p>
     * 移除所有 Markdown 语法标记
     * </p>
     *
     * @param markdown Markdown 内容
     * @return 纯文本
     */
    public static String toPlainText(String markdown) {
        if (!StringUtils.hasText(markdown)) {
            return "";
        }

        try {
            String text = markdown;

            // 移除代码块
            text = CODE_BLOCK_PATTERN.matcher(text).replaceAll("");

            // 移除行内代码
            text = INLINE_CODE_PATTERN.matcher(text).replaceAll("");

            // 移除图片
            text = IMAGE_PATTERN.matcher(text).replaceAll("");

            // 移除链接，保留文本
            text = LINK_PATTERN.matcher(text).replaceAll(matchResult -> {
                String linkText = matchResult.group();
                int start = linkText.indexOf('[') + 1;
                int end = linkText.indexOf(']');
                return end > start ? linkText.substring(start, end) : "";
            });

            // 移除标题标记
            text = HEADING_PATTERN.matcher(text).replaceAll("$1");

            // 移除 HTML 标签
            text = HTML_TAG_PATTERN.matcher(text).replaceAll("");

            // 移除其他格式标记
            text = text.replaceAll("[*_~`#>]", "");

            // 移除多余的空行
            text = text.replaceAll("\\n{3,}", "\n\n");

            // 移除首尾空白
            return text.trim();
        } catch (Exception e) {
            log.error("Markdown 转纯文本失败", e);
            return markdown;
        }
    }

    /**
     * 提取 Markdown 中的所有图片 URL
     *
     * @param markdown Markdown 内容
     * @return 图片 URL 列表
     */
    public static java.util.List<String> extractImageUrls(String markdown) {
        if (!StringUtils.hasText(markdown)) {
            return java.util.List.of();
        }

        java.util.List<String> imageUrls = new java.util.ArrayList<>();

        try {
            Matcher matcher = IMAGE_PATTERN.matcher(markdown);
            while (matcher.find()) {
                String imageMarkdown = matcher.group();
                int urlStart = imageMarkdown.indexOf('(') + 1;
                int urlEnd = imageMarkdown.indexOf(')');
                if (urlEnd > urlStart) {
                    String url = imageMarkdown.substring(urlStart, urlEnd).trim();
                    if (StringUtils.hasText(url)) {
                        imageUrls.add(url);
                    }
                }
            }
        } catch (Exception e) {
            log.error("提取 Markdown 图片 URL 失败", e);
        }

        return imageUrls;
    }

    /**
     * 提取 Markdown 中的第一个一级标题
     *
     * @param markdown Markdown 内容
     * @return 一级标题，如果没有则返回 null
     */
    public static String extractFirstHeading(String markdown) {
        if (!StringUtils.hasText(markdown)) {
            return null;
        }

        try {
            Pattern h1Pattern = Pattern.compile("^#\\s+(.*)$", Pattern.MULTILINE);
            Matcher matcher = h1Pattern.matcher(markdown);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        } catch (Exception e) {
            log.error("提取 Markdown 标题失败", e);
        }

        return null;
    }

    /**
     * 验证 Markdown 内容是否有效
     * <p>
     * 简单校验，确保内容不为空且有实际内容
     * </p>
     *
     * @param markdown Markdown 内容
     * @return 是否有效
     */
    public static boolean isValid(String markdown) {
        if (!StringUtils.hasText(markdown)) {
            return false;
        }

        String plainText = toPlainText(markdown);
        return StringUtils.hasText(plainText);
    }

    /**
     * 清理 Markdown 内容
     * <p>
     * 移除多余的空行、规范化格式
     * </p>
     *
     * @param markdown Markdown 内容
     * @return 清理后的内容
     */
    public static String clean(String markdown) {
        if (!StringUtils.hasText(markdown)) {
            return "";
        }

        try {
            String cleaned = markdown;

            // 移除多余的空行（3个以上连续换行变为2个）
            cleaned = cleaned.replaceAll("\\n{3,}", "\n\n");

            // 移除行尾空格
            cleaned = cleaned.replaceAll(" +\\n", "\n");

            // 移除首尾空白
            cleaned = cleaned.trim();

            return cleaned;
        } catch (Exception e) {
            log.error("清理 Markdown 内容失败", e);
            return markdown;
        }
    }
}

