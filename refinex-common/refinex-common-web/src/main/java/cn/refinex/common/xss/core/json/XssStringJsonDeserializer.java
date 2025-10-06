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
     *
     * @param p    用于读取 JSON 内容的解析器
     * @param ctxt 用于访问反序列化活动信息的上下文
     * @return 过滤后的字符串
     * @throws IOException 如果在读取 JSON 内容时发生 I/O 错误
     */
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // 1. 放行 Xss 白名单
        HttpServletRequest request = ServletUtils.getRequest();
        if (Objects.nonNull(request)) {
            String uri = ServletUtils.getRequestUri(request);
            List<String> excludeUrls = xssProperties.getExcludeUrls();
            if (CollectionUtils.isNotEmpty(excludeUrls)) {
                boolean matchResult = excludeUrls.stream().anyMatch(excludeUrl -> pathMatcher.match(excludeUrl, uri));
                if (matchResult) {
                    // 命中白名单, 放行
                    return p.getText();
                }
            }
        }

        // 2. 非白名单, 进行 XSS 过滤
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return xssCleaner.clean(p.getText());
        }

        JsonToken currentToken = p.currentToken();

        // [databind#381]
        if (JsonToken.START_ARRAY.equals(currentToken)) {
            return _deserializeFromArray(p, ctxt);
        }

        // need to gracefully handle byte[] data, as base64
        if (JsonToken.VALUE_EMBEDDED_OBJECT.equals(currentToken)) {
            Object embeddedObject = p.getEmbeddedObject();
            if (Objects.isNull(embeddedObject)) {
                return null;
            }

            if (embeddedObject instanceof byte[] byteArray) {
                return ctxt.getBase64Variant().encode(byteArray, false);
            }

            // otherwise, try conversion using toString()...
            return embeddedObject.toString();
        }

        // 29-Jun-2020, tatu: New! "Scalar from Object" (mostly for XML)
        if (JsonToken.START_OBJECT.equals(currentToken)) {
            return ctxt.extractScalarFromObject(p, this, _valueClass);
        }

        if (currentToken.isScalarValue()) {
            String text = p.getValueAsString();
            return xssCleaner.clean(text);
        }
        return (String) ctxt.handleUnexpectedToken(_valueClass, p);
    }
}
