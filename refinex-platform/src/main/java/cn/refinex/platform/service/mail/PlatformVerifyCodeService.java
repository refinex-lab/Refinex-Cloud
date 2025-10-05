package cn.refinex.platform.service.mail;

import cn.refinex.common.mail.domain.dto.VerifyCodeRequest;
import cn.refinex.common.mail.domain.dto.VerifyCodeResult;
import cn.refinex.common.mail.domain.dto.VerifyCodeValidateRequest;
import cn.refinex.common.mail.service.VerifyCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Platform 验证码服务
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformVerifyCodeService {

    private final VerifyCodeService verifyCodeService;

    /**
     * 发送验证码
     *
     * @param request 验证码请求
     * @return 验证码结果
     */
    public VerifyCodeResult sendVerifyCode(VerifyCodeRequest request) {
        log.info("发送验证码: email={}, codeType={}", request.getEmail(), request.getCodeType());
        return verifyCodeService.sendVerifyCode(request);
    }

    /**
     * 验证验证码
     *
     * @param request 验证请求
     * @return 是否验证成功
     */
    public boolean verifyCode(VerifyCodeValidateRequest request) {
        log.info("验证验证码: email={}, codeType={}", request.getEmail(), request.getCodeType());
        return verifyCodeService.verifyCode(request);
    }
}

