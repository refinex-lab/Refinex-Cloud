package cn.refinex.platform.service;

import cn.refinex.platform.controller.email.dto.request.EmailVerifyCodeRequestDTO;
import cn.refinex.platform.controller.email.dto.request.EmailVerifyCodeValidateRequestDTO;
import cn.refinex.platform.controller.email.dto.response.EmailVerifyCodeResponseDTO;

/**
 * 邮箱验证码服务
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface EmailVerifyCodeService {

    /**
     * 发送验证码
     *
     * @param request 验证码请求
     * @return 验证码结果
     */
    EmailVerifyCodeResponseDTO sendVerifyCode(EmailVerifyCodeRequestDTO request);

    /**
     * 验证验证码
     *
     * @param request 验证请求
     * @return 是否验证成功
     */
    boolean verifyCode(EmailVerifyCodeValidateRequestDTO request);

    /**
     * 更新过期验证码状态
     *
     * @return 更新数量
     */
    int updateExpiredCodes();
}
