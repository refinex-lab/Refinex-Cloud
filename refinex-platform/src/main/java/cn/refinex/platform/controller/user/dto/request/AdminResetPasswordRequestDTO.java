package cn.refinex.platform.controller.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员重置用户密码请求 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "管理员重置用户密码请求")
public class AdminResetPasswordRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private Long userId;

    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$", message = "密码至少8位，包含大小写字母和数字")
    @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "P@ssw0rd")
    private String newPassword;

    @Schema(description = "重置原因")
    private String reason;
}

