package cn.refinex.platform.client.mail;

import cn.refinex.common.constants.FeignConstants;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.mail.domain.dto.VerifyCodeRequest;
import cn.refinex.common.mail.domain.dto.VerifyCodeResult;
import cn.refinex.common.mail.domain.dto.VerifyCodeValidateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 邮箱验证码 Feign 客户端
 *
 * @author Refinex
 * @since 1.0.0
 */
@FeignClient(
        name = FeignConstants.PLATFORM_SERVICE,
        path = FeignConstants.PLATFORM_API_PREFIX
)
@Tag(name = "邮箱验证码 Feign API")
public interface EmailVerifyCodeClient {

    @PostMapping("/mail/send")
    @Operation(summary = "发送验证码", description = "向指定邮箱发送验证码")
    @Parameter(name = "request", description = "验证码请求", required = true)
    ApiResult<VerifyCodeResult> sendVerifyCode(@RequestBody VerifyCodeRequest request);

    @PostMapping("/mail/verify")
    @Operation(summary = "验证验证码", description = "验证指定邮箱的验证码是否正确")
    @Parameter(name = "request", description = "验证码验证请求", required = true)
    ApiResult<Boolean> verifyCode(@RequestBody VerifyCodeValidateRequest request);
}

