package cn.refinex.platform.controller.email;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.mail.domain.dto.VerifyCodeRequest;
import cn.refinex.common.mail.domain.dto.VerifyCodeResult;
import cn.refinex.common.mail.domain.dto.VerifyCodeValidateRequest;
import cn.refinex.platform.client.mail.EmailVerifyCodeClient;
import cn.refinex.platform.service.mail.PlatformVerifyCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 邮箱验证码 Feign API 实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class EmailVerifyCodeController implements EmailVerifyCodeClient {

    private final PlatformVerifyCodeService verifyCodeService;

    /**
     * 发送验证码
     *
     * @param request 验证码请求
     * @return 验证码结果
     */
    @Override
    public ApiResult<VerifyCodeResult> sendVerifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        VerifyCodeResult result = verifyCodeService.sendVerifyCode(request);
        return ApiResult.success(result);
    }

    /**
     * 验证验证码
     *
     * @param request 验证请求
     * @return 是否验证成功
     */
    @Override
    public ApiResult<Boolean> verifyCode(@Valid @RequestBody VerifyCodeValidateRequest request) {
        boolean success = verifyCodeService.verifyCode(request);
        return ApiResult.success(success);
    }
}

