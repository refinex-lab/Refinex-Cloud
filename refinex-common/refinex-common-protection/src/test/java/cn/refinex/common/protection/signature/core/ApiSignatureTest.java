package cn.refinex.common.protection.signature.core;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.refinex.common.protection.signature.core.annotation.ApiSignature;
import cn.refinex.common.protection.signature.core.aop.ApiSignatureAspect;
import cn.refinex.common.protection.signature.core.redis.ApiSignatureRedisService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 接口签名测试类, {@link ApiSignatureTest} 的单元测试
 *
 * @author Zhougang
 * @author Refinex
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ApiSignatureTest {

    @InjectMocks
    private ApiSignatureAspect apiSignatureAspect;

    @Mock
    private ApiSignatureRedisService apiSignatureRedisService;

    @Test
    void testSignatureGet() throws IOException {
        // 创建签名
        Long timestamp = System.currentTimeMillis();
        String nonce = IdUtil.randomUUID();
        String appId = "xxxxxx";
        String appSecret = "yyyyyy";
        String signString = "k1=v1&v1=k1testappId=xxxxxx&nonce=" + nonce + "&timestamp=" + timestamp + "yyyyyy";
        String sign = DigestUtil.sha256Hex(signString);

        // 准备参数
        ApiSignature apiSignature = mock(ApiSignature.class);
        when(apiSignature.appId()).thenReturn("appId");
        when(apiSignature.timestamp()).thenReturn("timestamp");
        when(apiSignature.nonce()).thenReturn("nonce");
        when(apiSignature.sign()).thenReturn("sign");
        when(apiSignature.timeout()).thenReturn(60);
        when(apiSignature.timeUnit()).thenReturn(TimeUnit.SECONDS);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("appId")).thenReturn(appId);
        when(request.getHeader("timestamp")).thenReturn(String.valueOf(timestamp));
        when(request.getHeader("nonce")).thenReturn(nonce);
        when(request.getHeader("sign")).thenReturn(sign);
        when(request.getParameterMap()).thenReturn(MapUtil.<String, String[]>builder().put("v1", new String[]{"k1"}).put("k1", new String[]{"v1"}).build());
        when(request.getContentType()).thenReturn("application/json");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("test")));

        // mock 方法
        when(apiSignatureRedisService.getAppSecret(appId)).thenReturn(appSecret);
        when(apiSignatureRedisService.setNonce(appId, nonce, 120, TimeUnit.SECONDS)).thenReturn(true);

        // 验证
        boolean result = apiSignatureAspect.verifySignature(apiSignature, request);
        assertTrue(result);
    }
}
