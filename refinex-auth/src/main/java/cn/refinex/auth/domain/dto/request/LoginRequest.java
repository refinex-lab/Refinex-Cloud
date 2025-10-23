package cn.refinex.auth.domain.dto.request;

import cn.refinex.common.validation.annotation.ConditionalValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 登录请求 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "登录请求")
@ConditionalValidation(
        condition = "loginType == 1",
        field = "username",
        type = ConditionalValidation.ValidationType.NOT_BLANK,
        message = "登录类型为密码登录时，用户名不能为空"
)
@ConditionalValidation(
        condition = "loginType == 2",
        field = "email",
        type = ConditionalValidation.ValidationType.NOT_BLANK,
        message = "登录类型为邮箱登录时，邮箱不能为空"
)
public class LoginRequest {

    @NotNull(message = "登录类型不能为空")
    @Schema(description = "登录类型(1=密码登录, 2=邮箱登录)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer loginType;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "验证码唯一标识", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String captchaUuid;

    @Schema(description = "验证码文本", example = "8u6h")
    private String captchaCode;

    @Schema(description = "客户端 ID（可选，前端传递）", defaultValue = "web_admin", example = "web_admin", allowableValues = {"web_admin", "mobile_app"})
    private String clientId = "web_admin";

    @Schema(description = "设备类型（可选，前端传递）", example = "PC", allowableValues = {"PC", "APP", "H5"})
    private String deviceType;

    @Schema(description = "是否记住我（默认 false）", example = "false")
    private Boolean rememberMe = false;
}

