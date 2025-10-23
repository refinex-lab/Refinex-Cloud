package cn.refinex.auth.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.refinex.auth.domain.dto.response.CaptchaCreateResponse;
import cn.refinex.auth.service.CaptchaService;
import cn.refinex.common.apilog.core.annotation.LogOperation;
import cn.refinex.common.apilog.core.enums.OperateTypeEnum;
import cn.refinex.common.domain.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码控制器
 *
 * @author Refinex
 * @since 2025-10-05
 */
@SaIgnore
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
@Tag(name = "验证码管理", description = "验证码生成等接口")
public class CaptchaController {

    private final CaptchaService captchaService;

    @GetMapping
    @LogOperation(operateModule = "验证码管理", operateDesc = "生成验证码图片和唯一标识", operationType = OperateTypeEnum.OTHER)
    @Operation(summary = "生成验证码", description = "生成验证码图片（Base64编码）和唯一标识")
    public ApiResult<CaptchaCreateResponse> generate() {
        CaptchaCreateResponse response = captchaService.generate();
        return ApiResult.success(response);
    }
}

