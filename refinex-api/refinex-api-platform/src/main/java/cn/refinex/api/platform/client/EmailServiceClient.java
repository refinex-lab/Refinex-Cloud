package cn.refinex.api.platform.client;

import cn.refinex.common.constants.SystemFeignConstants;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.mail.domain.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 邮件发送 Feign 客户端
 *
 * @author Refinex
 * @since 1.0.0
 */
@FeignClient(
        name = SystemFeignConstants.PLATFORM_SERVICE,
        contextId = "emailServiceClient",
        path = "/internal/emails"
)
@Tag(name = "邮件发送 Feign 客户端", description = "提供邮件发送相关的内部 Feign 接口")
public interface EmailServiceClient {

    @PostMapping("/simple")
    @Operation(summary = "发送简单邮件", description = "发送纯文本或 HTML 邮件")
    @Parameter(name = "request", description = "简单邮件发送请求", required = true)
    ApiResult<EmailSendResult> sendSimple(@RequestBody EmailSendRequest request);

    @PostMapping("/template")
    @Operation(summary = "使用模板发送邮件", description = "使用预定义模板发送邮件")
    @Parameter(name = "request", description = "模板邮件发送请求", required = true)
    ApiResult<EmailSendResult> sendWithTemplate(@RequestBody EmailSendRequest request);

    @PostMapping("/async")
    @Operation(summary = "异步发送邮件", description = "异步发送邮件，立即返回")
    @Parameter(name = "request", description = "异步邮件发送请求", required = true)
    ApiResult<EmailSendResult> sendAsync(@RequestBody EmailSendRequest request);

    @PostMapping("/verify-codes")
    @Operation(summary = "发送验证码", description = "向指定邮箱发送验证码")
    @Parameter(name = "request", description = "验证码发送请求", required = true)
    ApiResult<VerifyCodeResult> sendVerifyCode(@RequestBody VerifyCodeRequest request);

    @PostMapping("/verify-codes/validations")
    @Operation(summary = "验证验证码", description = "验证指定邮箱的验证码是否正确")
    @Parameter(name = "request", description = "验证码验证请求", required = true)
    ApiResult<Boolean> verifyCode(@RequestBody VerifyCodeValidateRequest request);
}

