package cn.refinex.platform.controller.email;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.mail.domain.dto.EmailSendRequest;
import cn.refinex.common.mail.domain.dto.EmailSendResult;
import cn.refinex.platform.client.mail.EmailSendClient;
import cn.refinex.platform.service.mail.PlatformEmailService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 邮件发送 Feign API 实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class EmailSendController implements EmailSendClient {

    private final PlatformEmailService emailService;

    /**
     * 发送简单邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     * @return 发送结果
     */
    @Override
    public ApiResult<EmailSendResult> sendSimple(
            @Parameter(description = "收件人邮箱") @RequestParam String to,
            @Parameter(description = "邮件主题") @RequestParam String subject,
            @Parameter(description = "邮件内容") @RequestParam String content) {
        EmailSendResult result = emailService.sendSimple(to, subject, content);
        return ApiResult.success(result);
    }

    /**
     * 使用模板发送邮件
     *
     * @param templateCode 模板编码
     * @param email        收件人邮箱
     * @param variables    模板变量
     * @return 发送结果
     */
    @Override
    public ApiResult<EmailSendResult> sendWithTemplate(
            @Parameter(description = "模板编码") @RequestParam String templateCode,
            @Parameter(description = "收件人邮箱") @RequestParam String email,
            @Parameter(description = "模板变量") @RequestBody Map<String, Object> variables) {
        EmailSendResult result = emailService.sendWithTemplate(templateCode, email, variables);
        return ApiResult.success(result);
    }

    /**
     * 异步发送邮件
     *
     * @param request 发送请求
     * @return 发送结果
     */
    @Override
    public ApiResult<EmailSendResult> sendAsync(@Valid @RequestBody EmailSendRequest request) {
        EmailSendResult result = emailService.sendAsync(request);
        return ApiResult.success(result);
    }
}

