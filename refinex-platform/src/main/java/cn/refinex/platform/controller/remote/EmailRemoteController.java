package cn.refinex.platform.controller.remote;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.refinex.api.platform.client.email.EmailRemoteService;
import cn.refinex.api.platform.client.email.dto.request.EmailSendRequestDTO;
import cn.refinex.api.platform.client.email.dto.request.EmailVerifyCodeRequestDTO;
import cn.refinex.api.platform.client.email.dto.request.EmailVerifyCodeValidateRequestDTO;
import cn.refinex.api.platform.client.email.dto.response.EmailSendResponseDTO;
import cn.refinex.api.platform.client.email.dto.response.EmailVerifyCodeResponseDTO;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邮件服务 OpenFeign 接口实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@SaIgnore // 内部接口，不需要用户令牌
@RestController
@RequiredArgsConstructor
public class EmailRemoteController implements EmailRemoteService {

    private final EmailService emailService;

    /**
     * 发送简单邮件
     *
     * @param request 邮件发送请求
     * @return 发送结果
     */
    @Override
    public ApiResult<EmailSendResponseDTO> sendSimple(EmailSendRequestDTO request) {
        EmailSendResponseDTO result = emailService.sendSimple(request.getRecipientEmail(), request.getSubject(), request.getContent());
        return ApiResult.success(result);
    }

    /**
     * 发送带模板的邮件
     *
     * @param request 邮件发送请求
     * @return 发送结果
     */
    @Override
    public ApiResult<EmailSendResponseDTO> sendWithTemplate(EmailSendRequestDTO request) {
        EmailSendResponseDTO result = emailService.sendWithTemplate(request.getTemplateCode(), request.getRecipientEmail(), request.getTemplateVariables());
        return ApiResult.success(result);
    }

    /**
     * 异步发送邮件
     *
     * @param request 邮件发送请求
     * @return 发送结果
     */
    @Override
    public ApiResult<EmailSendResponseDTO> sendAsync(EmailSendRequestDTO request) {
        EmailSendResponseDTO result = emailService.sendAsync(request);
        return ApiResult.success(result);
    }

    /**
     * 发送验证码邮件
     *
     * @param request 验证码发送请求
     * @return 验证码结果
     */
    @Override
    public ApiResult<EmailVerifyCodeResponseDTO> sendVerifyCode(EmailVerifyCodeRequestDTO request) {
        EmailVerifyCodeResponseDTO result = emailService.sendVerifyCode(request);
        return ApiResult.success(result);
    }

    /**
     * 验证验证码
     *
     * @param request 验证码验证请求
     * @return 验证结果
     */
    @Override
    public ApiResult<Boolean> verifyCode(EmailVerifyCodeValidateRequestDTO request) {
        boolean success = emailService.verifyCode(request);
        return ApiResult.success(success);
    }
}
