package cn.refinex.common.xss.core.json;

import cn.refinex.common.utils.servlet.ServletUtils;
import cn.refinex.common.xss.config.properties.XssProperties;
import cn.refinex.common.xss.core.clean.XssCleaner;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.PathMatcher;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * XSS 字符串 JSON 反序列化器, 用于反序列化过程中对字符串进行 XSS 过滤
 *
 * @author Hccake
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@AllArgsConstructor
public class XssStringJsonDeserializer extends StringDeserializer {

    private final XssProperties xssProperties;
    private final PathMatcher pathMatcher;
    private final XssCleaner xssCleaner;

    /**
     * 该方法在反序列化字符串时被调用, 用于对字符串进行 XSS 过滤, 防止 XSS 攻击
     * <p>
     * 处理流程：
     * 1. 首先检查当前请求是否在 XSS 白名单中，如果在白名单中则直接返回原始字符串
     * 2. 如果不在白名单中，则根据不同的 JsonToken 类型进行相应的处理和 XSS 过滤
     *
     * @param p    用于读取 JSON 内容的解析器
     * @param ctxt 用于访问反序列化活动信息的上下文
     * @return 过滤后的字符串
     * @throws IOException 如果在读取 JSON 内容时发生 I/O 错误
     */
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // 1. 放行 Xss 白名单
        // 获取当前 HTTP 请求对象，用于检查请求 URI 是否在白名单中
        HttpServletRequest request = ServletUtils.getRequest();
        if (Objects.nonNull(request)) {
            String uri = ServletUtils.getRequestUri(request);
            List<String> excludeUrls = xssProperties.getExcludeUrls();
            if (CollectionUtils.isNotEmpty(excludeUrls)) {
                // 使用路径匹配器检查当前请求 URI 是否匹配白名单中的任一模式
                boolean matchResult = excludeUrls.stream().anyMatch(excludeUrl -> pathMatcher.match(excludeUrl, uri));
                if (matchResult) {
                    // 命中白名单, 直接返回原始文本，不进行 XSS 过滤
                    return p.getText();
                }
            }
        }

        // 2. 非白名单, 进行 XSS 过滤
        // 处理标准的字符串值 token，这是最常见的情况
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return xssCleaner.clean(p.getText());
        }

        JsonToken currentToken = p.currentToken();

        // 处理 Jackson databind#381 问题：
        // 当遇到 START_ARRAY token 时，可能是单元素数组的反序列化场景
        // 例如：将 ["string"] 反序列化为 String 类型而不是 List<String>
        if (JsonToken.START_ARRAY.equals(currentToken)) {
            return _deserializeFromArray(p, ctxt);
        }

        // 处理嵌入式对象，主要用于优雅地处理 byte[] 数据（通常是 base64 编码）
        // 这种情况常见于二进制数据的 JSON 传输场景
        if (JsonToken.VALUE_EMBEDDED_OBJECT.equals(currentToken)) {
            Object embeddedObject = p.getEmbeddedObject();
            if (Objects.isNull(embeddedObject)) {
                return null;
            }

            // 如果嵌入对象是字节数组，则使用 base64 编码转换为字符串
            if (embeddedObject instanceof byte[] byteArray) {
                return ctxt.getBase64Variant().encode(byteArray, false);
            }

            // 对于其他类型的嵌入对象，尝试使用 toString() 方法进行转换
            return embeddedObject.toString();
        }

        // 处理 "Scalar from Object" 场景（主要用于 XML 等格式）
        // 这是 Jackson 2.11+ 新增的功能，用于从对象结构中提取标量值
        // 例如：将 {"$": "value"} 这样的 XML 风格对象转换为字符串 "value"
        if (JsonToken.START_OBJECT.equals(currentToken)) {
            return ctxt.extractScalarFromObject(p, this, _valueClass);
        }

        // 处理其他标量值类型（如数字、布尔值等），将它们转换为字符串并进行 XSS 过滤
        if (currentToken.isScalarValue()) {
            String text = p.getValueAsString();
            return xssCleaner.clean(text);
        }

        // 如果遇到无法处理的 token 类型，则委托给上下文处理异常情况
        return (String) ctxt.handleUnexpectedToken(_valueClass, p);
    }
}
