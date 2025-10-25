package cn.refinex.auth.client;

import cn.refinex.auth.domain.dto.request.EmailVerifyCodeRequestDTO;
import cn.refinex.auth.domain.dto.request.EmailVerifyCodeValidateRequestDTO;
import cn.refinex.auth.domain.dto.response.EmailVerifyCodeResponseDTO;
import cn.refinex.common.annotation.HttpInterfaceClient;
import cn.refinex.common.domain.ApiResult;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import static cn.refinex.common.constants.SystemHttpServiceConstants.PLATFORM_SERVICE_NAME;

/**
 * 平台邮箱服务 HTTP Interface 客户端
 *
 * @author Refinex
 * @since 1.0.0
 */
@Validated
@HttpInterfaceClient(PLATFORM_SERVICE_NAME)
@HttpExchange("/emails")
public interface PlatformEmailServiceClient {

    @PostExchange("/sent-code")
    ApiResult<EmailVerifyCodeResponseDTO> sendEmailVerifyCode(@RequestBody @Valid EmailVerifyCodeRequestDTO request);

    @PostExchange("/verify-code")
    ApiResult<Boolean> verifyEmailCode(@RequestBody @Valid EmailVerifyCodeValidateRequestDTO request);
}
