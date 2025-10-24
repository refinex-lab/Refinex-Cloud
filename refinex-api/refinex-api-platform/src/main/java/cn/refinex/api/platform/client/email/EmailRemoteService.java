package cn.refinex.api.platform.client.email;

import cn.refinex.api.platform.client.email.dto.request.EmailSendRequestDTO;
import cn.refinex.api.platform.client.email.dto.request.EmailVerifyCodeRequestDTO;
import cn.refinex.api.platform.client.email.dto.request.EmailVerifyCodeValidateRequestDTO;
import cn.refinex.api.platform.client.email.dto.response.EmailSendResponseDTO;
import cn.refinex.api.platform.client.email.dto.response.EmailVerifyCodeResponseDTO;
import cn.refinex.common.constants.SystemFeignConstants;
import cn.refinex.common.domain.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 邮件服务 OpenFeign 接口契约
 *
 * @author Refinex
 * @since 1.0.0
 */
@FeignClient(name = SystemFeignConstants.PLATFORM_SERVICE, contextId = "emailServiceClient")
@Tag(name = "邮件服务 OpenFeign 接口契约", description = "定义邮件服务相关的 OpenFeign 接口契约")
public interface EmailRemoteService {

    @PostMapping("/email/sent-code-simple")
    @Operation(summary = "发送简单邮件", description = "发送纯文本或 HTML 邮件")
    @Parameter(name = "request", description = "简单邮件发送请求", required = true)
    ApiResult<EmailSendResponseDTO> sendSimple(@RequestBody EmailSendRequestDTO request);

    @PostMapping("/email/sent-code-template")
    @Operation(summary = "使用模板发送邮件", description = "使用预定义模板发送邮件")
    @Parameter(name = "request", description = "模板邮件发送请求", required = true)
    ApiResult<EmailSendResponseDTO> sendWithTemplate(@RequestBody EmailSendRequestDTO request);

    @PostMapping("/email/async-sent-code")
    @Operation(summary = "异步发送邮件", description = "异步发送邮件，立即返回")
    @Parameter(name = "request", description = "异步邮件发送请求", required = true)
    ApiResult<EmailSendResponseDTO> sendAsync(@RequestBody EmailSendRequestDTO request);

    @PostMapping("/email/sent-code")
    @Operation(summary = "发送验证码", description = "向指定邮箱发送验证码")
    @Parameter(name = "request", description = "验证码发送请求", required = true)
    ApiResult<EmailVerifyCodeResponseDTO> sendVerifyCode(@RequestBody EmailVerifyCodeRequestDTO request);

    @PostMapping("/email/verify-code")
    @Operation(summary = "验证验证码", description = "验证指定邮箱的验证码是否正确")
    @Parameter(name = "request", description = "验证码验证请求", required = true)
    ApiResult<Boolean> verifyCode(@RequestBody EmailVerifyCodeValidateRequestDTO request);
}

