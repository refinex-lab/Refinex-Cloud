package cn.refinex.platform.controller.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户更新请求 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "用户更新请求")
public class UserUpdateRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private Long userId;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String mobile;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "性别：male,female,other")
    private String sex;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "备注说明")
    private String remark;
}

