package cn.refinex.auth.controller;

import cn.refinex.api.auth.domain.dto.CaptchaGenerateResponse;
import cn.refinex.auth.service.CaptchaService;
import cn.refinex.common.domain.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码控制器
 *
 * @author Refinex
 * @since 2025-10-05
 */
@Slf4j
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
@Tag(name = "验证码管理", description = "验证码生成、刷新")
public class CaptchaController {

    private final CaptchaService captchaService;

    /**
     * 生成验证码
     * <p>
     * 说明：
     * 1. 生成验证码图片和唯一标识（UUID）
     * 2. 验证码存储到 Redis，设置过期时间
     * 3. 返回 UUID 和 Base64 图片给前端
     * 4. 前端在登录时携带 UUID 和用户输入的验证码文本
     * </p>
     *
     * @return 验证码生成响应
     */
    @GetMapping("/generate")
    @Operation(summary = "生成验证码", description = "生成验证码图片和唯一标识")
    public ApiResult<CaptchaGenerateResponse> generate() {
        CaptchaGenerateResponse response = captchaService.generate();
        return ApiResult.success(response);
    }
}

