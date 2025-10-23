package cn.refinex.auth.service;

import cn.refinex.auth.domain.dto.response.CaptchaCreateResponse;
import cn.refinex.common.exception.BusinessException;

/**
 * 验证码服务
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface CaptchaService {

    /**
     * 生成验证码
     *
     * @return 验证码生成响应
     */
    CaptchaCreateResponse generate();

    /**
     * 验证验证码
     *
     * @param uuid 验证码唯一标识
     * @param code 用户输入的验证码文本
     * @throws BusinessException 验证失败时抛出异常
     */
    void verify(String uuid, String code);
}
