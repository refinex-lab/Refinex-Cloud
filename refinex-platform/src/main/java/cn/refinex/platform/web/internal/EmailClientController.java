package cn.refinex.platform.web.internal;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.refinex.api.platform.client.EmailServiceClient;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.mail.domain.dto.*;
import cn.refinex.platform.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邮件服务内部接口实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@SaIgnore // 内部接口，不需要用户令牌
@RestController
@RequiredArgsConstructor
public class EmailClientController implements EmailServiceClient {

    private final EmailService emailService;

    /**
     * 发送简单邮件
     *
     * @param request 邮件发送请求
     * @return 发送结果
     */
    @Override
    public ApiResult<EmailSendResult> sendSimple(EmailSendRequest request) {
        EmailSendResult result = emailService.sendSimple(request.getRecipientEmail(), request.getSubject(), request.getContent());
        return ApiResult.success(result);
    }

    /**
     * 发送带模板的邮件
     *
     * @param request 邮件发送请求
     * @return 发送结果
     */
    @Override
    public ApiResult<EmailSendResult> sendWithTemplate(EmailSendRequest request) {
        EmailSendResult result = emailService.sendWithTemplate(request.getTemplateCode(), request.getRecipientEmail(), request.getTemplateVariables());
        return ApiResult.success(result);
    }

    /**
     * 异步发送邮件
     *
     * @param request 邮件发送请求
     * @return 发送结果
     */
    @Override
    public ApiResult<EmailSendResult> sendAsync(EmailSendRequest request) {
        EmailSendResult result = emailService.sendAsync(request);
        return ApiResult.success(result);
    }

    /**
     * 发送验证码邮件
     *
     * @param request 验证码发送请求
     * @return 验证码结果
     */
    @Override
    public ApiResult<VerifyCodeResult> sendVerifyCode(VerifyCodeRequest request) {
        VerifyCodeResult result = emailService.sendVerifyCode(request);
        return ApiResult.success(result);
    }

    /**
     * 验证验证码
     *
     * @param request 验证码验证请求
     * @return 验证结果
     */
    @Override
    public ApiResult<Boolean> verifyCode(VerifyCodeValidateRequest request) {
        boolean success = emailService.verifyCode(request);
        return ApiResult.success(success);
    }
}
