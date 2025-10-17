package cn.refinex.platform.api;

import cn.refinex.common.constants.SystemFeignConstants;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.mail.domain.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 邮件发送 Feign 客户端
 *
 * @author Refinex
 * @since 1.0.0
 */
@Tag(name = "邮件发送 Feign API")
@FeignClient(
        name = SystemFeignConstants.PLATFORM_SERVICE,
        path = "/email",
        contextId = "emailFeignClient"
)
public interface EmailFeignClient {

    @PostMapping("/sendSimpleEmail")
    @Operation(summary = "发送简单邮件", description = "发送纯文本或 HTML 邮件")
    @Parameter(name = "to", description = "收件人邮箱")
    @Parameter(name = "subject", description = "邮件主题")
    @Parameter(name = "content", description = "邮件内容")
    ApiResult<EmailSendResult> sendSimple(
            @RequestParam("to") String to,
            @RequestParam("subject") String subject,
            @RequestParam("content") String content
    );

    @PostMapping("/sendMailByTemplate")
    @Operation(summary = "使用模板发送邮件", description = "使用预定义模板发送邮件")
    @Parameter(name = "templateCode", description = "模板编码")
    @Parameter(name = "email", description = "收件人邮箱")
    @Parameter(name = "variables", description = "模板变量")
    ApiResult<EmailSendResult> sendWithTemplate(
            @RequestParam("templateCode") String templateCode,
            @RequestParam("email") String email,
            @RequestBody Map<String, Object> variables
    );

    @PostMapping("/sendMailAsync")
    @Operation(summary = "异步发送邮件", description = "异步发送邮件，返回发送结果")
    @Parameter(name = "request", description = "发送请求")
    ApiResult<EmailSendResult> sendAsync(@RequestBody EmailSendRequest request);

    @PostMapping("/sendVerifyCode")
    @Operation(summary = "发送验证码", description = "向指定邮箱发送验证码")
    @Parameter(name = "request", description = "验证码请求", required = true)
    ApiResult<VerifyCodeResult> sendVerifyCode(@RequestBody VerifyCodeRequest request);

    @PostMapping("/verifyCode")
    @Operation(summary = "验证验证码", description = "验证指定邮箱的验证码是否正确")
    @Parameter(name = "request", description = "验证码验证请求", required = true)
    ApiResult<Boolean> verifyCode(@RequestBody VerifyCodeValidateRequest request);
}

