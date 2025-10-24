package cn.refinex.api.platform.client.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 重置密码请求类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "重置密码请求类")
public class ResetPasswordRequestDTO {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "123456")
    private String newPassword;

    @NotBlank(message = "确认密码不能为空")
    @Schema(description = "确认密码", example = "123456")
    private String confirmPassword;

    @NotBlank(message = "邮箱验证码不能为空")
    @Schema(description = "邮箱验证码", example = "123456")
    private String emailCode;
}
