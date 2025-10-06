package cn.refinex.common.xss.core.clean;

/**
 * XSS 清理器接口, 用于清理 HTML 中 XSS 攻击的字符串
 *
 * @author 芋道源码
 * @since 1.0.0
 */
public interface XssCleaner {

    /**
     * 清理 HTML 字符串中的 XSS 攻击
     *
     * @param inputHtml 原始 HTML 字符串
     * @return 清理后的 HTML 字符串
     */
    String clean(String inputHtml);
}
