package cn.refinex.auth.service.impl;

import cn.refinex.auth.domain.dto.response.CaptchaGenerateResponse;
import cn.refinex.auth.enums.CaptchaTypeEnum;
import cn.refinex.auth.properties.CaptchaProperties;
import cn.refinex.auth.service.CaptchaService;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.redis.RedisService;
import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static cn.refinex.auth.constants.AuthErrorMessageConstants.*;

/**
 * 验证码服务实现类
 *
 * @author Refinex
 * @since 2025-10-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final CaptchaProperties captchaProperties;
    private final RedisService redisService;

    /**
     * 生成验证码
     *
     * @return 验证码生成响应
     */
    @Override
    public CaptchaGenerateResponse generate() {
        try {
            // 生成验证码对象
            Captcha captcha = createCaptcha();

            // 生成唯一标识
            String uuid = UUID.randomUUID().toString();

            // 获取验证码文本
            String code = captcha.text();
            if (Boolean.FALSE.equals(captchaProperties.getCaseSensitive())) {
                code = code.toLowerCase();
            }

            // 存储到 Redis
            String redisKey = captchaProperties.getRedisKeyPrefix() + uuid;
            redisService.string().set(redisKey, code, captchaProperties.getExpireSeconds(), TimeUnit.SECONDS);

            // 生成 Base64 图片
            String image = captcha.toBase64();

            log.debug("生成验证码成功，uuid={}, code={}", uuid, code);

            return CaptchaGenerateResponse.builder()
                    .uuid(uuid)
                    .image(image)
                    .expireSeconds(captchaProperties.getExpireSeconds())
                    .build();

        } catch (Exception e) {
            log.error("生成验证码失败", e);
            throw new BusinessException("生成验证码失败", e);
        }
    }

    /**
     * 验证验证码
     *
     * @param uuid 验证码唯一标识
     * @param code 用户输入的验证码文本
     * @throws BusinessException 验证失败时抛出异常
     */
    @Override
    public void verify(String uuid, String code) {
        // 检查参数
        if (!StringUtils.hasText(uuid) || !StringUtils.hasText(code)) {
            throw new BusinessException(CAPTCHA_REQUIRED);
        }

        // 从 Redis 中获取验证码
        String redisKey = captchaProperties.getRedisKeyPrefix() + uuid;
        String correctCode = redisService.string().get(redisKey, String.class);

        // 验证码不存在或已过期
        if (!StringUtils.hasText(correctCode)) {
            log.warn("验证码已过期或不存在，uuid={}", uuid);
            throw new BusinessException(CAPTCHA_EXPIRED);
        }

        // 比对验证码（忽略大小写）
        String inputCode = Boolean.TRUE.equals(captchaProperties.getCaseSensitive()) ? code : code.toLowerCase();
        if (!correctCode.equals(inputCode)) {
            log.warn("验证码不正确，uuid={}, input={}, correct={}", uuid, inputCode, correctCode);
            throw new BusinessException(CAPTCHA_INVALID);
        }

        // 验证成功，删除验证码（一次性使用）
        redisService.delete(redisKey);
        log.debug("验证码验证成功，uuid={}", uuid);
    }

    /**
     * 创建验证码对象
     *
     * @return 验证码对象
     */
    private Captcha createCaptcha() throws IOException, FontFormatException {
        // 获取配置
        int width = captchaProperties.getWidth();
        int height = captchaProperties.getHeight();
        int length = captchaProperties.getLength();
        String type = captchaProperties.getType();

        // 根据类型创建验证码
        CaptchaTypeEnum typeEnum = CaptchaTypeEnum.fromCode(type);
        Captcha captcha;

        switch (typeEnum) {
            case GIF:
                captcha = new GifCaptcha(width, height, length);
                break;
            case ARITHMETIC:
                ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha(width, height);
                // 几位数运算
                arithmeticCaptcha.setLen(length);
                captcha = arithmeticCaptcha;
                break;
            case CHINESE:
                captcha = new ChineseCaptcha(width, height, length);
                break;
            case CHINESE_GIF:
                captcha = new ChineseGifCaptcha(width, height, length);
                break;
            case SPEC:
            default:
                captcha = new SpecCaptcha(width, height, length);
                break;
        }

        // 设置字符类型（仅对 SpecCaptcha 和 GifCaptcha 有效）
        if (captcha instanceof SpecCaptcha || captcha instanceof GifCaptcha) {
            captcha.setCharType(captchaProperties.getCharType());
        }

        // 设置字体
        captcha.setFont(captchaProperties.getFont());

        return captcha;
    }
}

