package cn.refinex.platform.web;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.mail.domain.dto.*;
import cn.refinex.platform.api.facade.EmailFacade;
import cn.refinex.platform.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
public class EmailClientController implements EmailFacade {

    private final EmailService emailService;

    @Override
    public ApiResult<EmailSendResult> sendSimple(@RequestParam("to") String to,
                                                 @RequestParam("subject") String subject,
                                                 @RequestParam("content") String content) {
        EmailSendResult result = emailService.sendSimple(to, subject, content);
        return ApiResult.success(result);
    }

    @Override
    public ApiResult<EmailSendResult> sendWithTemplate(@RequestParam("templateCode") String templateCode,
                                                       @RequestParam("email") String email,
                                                       @RequestBody Map<String, Object> variables) {
        EmailSendResult result = emailService.sendWithTemplate(templateCode, email, variables);
        return ApiResult.success(result);
    }

    @Override
    public ApiResult<EmailSendResult> sendAsync(@RequestBody EmailSendRequest request) {
        EmailSendResult result = emailService.sendAsync(request);
        return ApiResult.success(result);
    }

    @Override
    public ApiResult<VerifyCodeResult> sendVerifyCode(@RequestBody VerifyCodeRequest request) {
        VerifyCodeResult result = emailService.sendVerifyCode(request);
        return ApiResult.success(result);
    }

    @Override
    public ApiResult<Boolean> verifyCode(@RequestBody VerifyCodeValidateRequest request) {
        boolean success = emailService.verifyCode(request);
        return ApiResult.success(success);
    }
}
