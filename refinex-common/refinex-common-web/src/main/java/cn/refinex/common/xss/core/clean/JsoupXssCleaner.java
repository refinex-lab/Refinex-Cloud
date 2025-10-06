package cn.refinex.common.xss.core.clean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

/**
 * 使用 Jsoup 库实现的 XSS 清理器
 *
 * @author 芋道源码
 * @since 1.0.0
 */
public class JsoupXssCleaner implements XssCleaner {

    /**
     * 内部定定义了 HTML（元素和属性）的白名单，不在白名单中的元素和属性，都会被清理掉
     */
    private final Safelist safelist;

    /**
     * 当 HTML 中的 scr 属性是相对路径时，强制转换为绝对路径
     */
    private final String baseUri;

    /**
     * 构造函数，初始化 Safelist 白名单和 baseUri
     */
    public JsoupXssCleaner() {
        this.safelist = buildSafelist();
        this.baseUri = null;
    }

    /**
     * 清理 HTML 字符串中的 XSS 攻击
     *
     * @param inputHtml 原始 HTML 字符串
     * @return 清理后的 HTML 字符串
     */
    @Override
    public String clean(String inputHtml) {
        return Jsoup.clean(inputHtml, baseUri, safelist, new Document.OutputSettings().prettyPrint(false));
    }

    /**
     * 构建 Jsoup 的 Safelist（白名单），不在白名单中的元素和属性，都会被清理掉
     *
     * @return Safelist 对象
     */
    private Safelist buildSafelist() {
        // 包含 Jsoup 提供的 relaxed 白名单
        Safelist relaxedSafelist = Safelist.relaxed();

        // 补充：当使用富文本编辑器时，一些样式时通过 style 实现，例如字体色 style="color:red;", 因此需要给所有元素添加 style 属性
        // 注意：style 属性会存在注入风险，例如 <img STYLE="background-image:url(javascript:alert('XSS'))">
        relaxedSafelist.addAttributes(":all", "style", "class");

        // 补充：保留 a 标签的 target 属性
        relaxedSafelist.addAttributes("a", "target");

        // 补充：支持 img 标签为 base64
        relaxedSafelist.addProtocols("img", "src", "data");

        return relaxedSafelist;
    }
}
