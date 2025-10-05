package cn.refinex.platform.controller.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新用户请求
 *
 * @author Refinex
 * @since 2025-10-04
 */
@Data
@Schema(description = "更新用户请求")
public class UserUpdateRequest {

    @Size(max = 50, message = "昵称长度不能超过50")
    @Schema(description = "昵称")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "备注")
    private String remark;
}

