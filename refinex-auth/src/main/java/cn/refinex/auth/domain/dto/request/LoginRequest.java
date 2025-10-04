package cn.refinex.auth.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "登录请求")
public class LoginRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    /**
     * 验证码唯一标识
     */
    @Schema(description = "验证码唯一标识", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String captchaUuid;

    /**
     * 验证码文本
     */
    @Schema(description = "验证码文本", example = "8u6h")
    private String captchaCode;
}

