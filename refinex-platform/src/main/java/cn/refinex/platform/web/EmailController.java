package cn.refinex.platform.web;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.mail.domain.dto.*;
import cn.refinex.platform.api.EmailFeignClient;
import cn.refinex.platform.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 邮箱 Feign API 实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
public class EmailController implements EmailFeignClient {

    private final EmailService emailService;

    @Override
    public ApiResult<EmailSendResult> sendSimple(String to, String subject, String content) {
        EmailSendResult result = emailService.sendSimple(to, subject, content);
        return ApiResult.success(result);
    }

    @Override
    public ApiResult<EmailSendResult> sendWithTemplate(String templateCode, String email, Map<String, Object> variables) {
        EmailSendResult result = emailService.sendWithTemplate(templateCode, email, variables);
        return ApiResult.success(result);
    }

    @Override
    public ApiResult<EmailSendResult> sendAsync(EmailSendRequest request) {
        EmailSendResult result = emailService.sendAsync(request);
        return ApiResult.success(result);
    }

    @Override
    public ApiResult<VerifyCodeResult> sendVerifyCode(VerifyCodeRequest request) {
        VerifyCodeResult result = emailService.sendVerifyCode(request);
        return ApiResult.success(result);
    }

    @Override
    public ApiResult<Boolean> verifyCode(VerifyCodeValidateRequest request) {
        boolean success = emailService.verifyCode(request);
        return ApiResult.success(success);
    }
}
