package cn.refinex.platform.controller.email;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.refinex.platform.controller.email.dto.request.EmailSendRequestDTO;
import cn.refinex.platform.controller.email.dto.request.EmailVerifyCodeRequestDTO;
import cn.refinex.platform.controller.email.dto.request.EmailVerifyCodeValidateRequestDTO;
import cn.refinex.platform.controller.email.dto.response.EmailSendResponseDTO;
import cn.refinex.platform.controller.email.dto.response.EmailVerifyCodeResponseDTO;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邮件服务控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@SaIgnore // 内部接口，不需要用户令牌
@RestController
@RequestMapping("/emails")
@RequiredArgsConstructor
@Tag(name = "邮件管理", description = "邮件服务相关接口")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/sent-code-simple")
    @Operation(summary = "发送简单邮件", description = "发送纯文本或 HTML 邮件")
    @Parameter(name = "request", description = "简单邮件发送请求", required = true)
    public ApiResult<EmailSendResponseDTO> sendSimple(@RequestBody EmailSendRequestDTO request) {
        EmailSendResponseDTO result = emailService.sendSimple(request.getRecipientEmail(), request.getSubject(), request.getContent());
        return ApiResult.success(result);
    }

    @PostMapping("/sent-code-template")
    @Operation(summary = "使用模板发送邮件", description = "使用预定义模板发送邮件")
    @Parameter(name = "request", description = "模板邮件发送请求", required = true)
    public ApiResult<EmailSendResponseDTO> sendWithTemplate(@RequestBody EmailSendRequestDTO request) {
        EmailSendResponseDTO result = emailService.sendWithTemplate(request.getTemplateCode(), request.getRecipientEmail(), request.getTemplateVariables());
        return ApiResult.success(result);
    }

    @PostMapping("/async-sent-code")
    @Operation(summary = "异步发送邮件", description = "异步发送邮件，立即返回")
    @Parameter(name = "request", description = "异步邮件发送请求", required = true)
    public ApiResult<EmailSendResponseDTO> sendAsync(@RequestBody EmailSendRequestDTO request) {
        EmailSendResponseDTO result = emailService.sendAsync(request);
        return ApiResult.success(result);
    }

    @PostMapping("/sent-code")
    @Operation(summary = "发送验证码", description = "向指定邮箱发送验证码")
    @Parameter(name = "request", description = "验证码发送请求", required = true)
    public ApiResult<EmailVerifyCodeResponseDTO> sendVerifyCode(@RequestBody EmailVerifyCodeRequestDTO request) {
        EmailVerifyCodeResponseDTO result = emailService.sendVerifyCode(request);
        return ApiResult.success(result);
    }

    @PostMapping("/verify-code")
    @Operation(summary = "验证验证码", description = "验证指定邮箱的验证码是否正确")
    @Parameter(name = "request", description = "验证码验证请求", required = true)
    public ApiResult<Boolean> verifyCode(@RequestBody EmailVerifyCodeValidateRequestDTO request) {
        boolean success = emailService.verifyCode(request);
        return ApiResult.success(success);
    }
}
