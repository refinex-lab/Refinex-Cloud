package cn.refinex.platform.controller.email.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码校验请求 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerifyCodeValidateRequestDTO {

    /**
     * 邮箱地址（必填）
     */
    @NotBlank(message = "邮箱地址不能为空")
    @Email(message = "邮箱地址格式不正确")
    private String email;

    /**
     * 验证码（必填）
     */
    @NotBlank(message = "验证码不能为空")
    private String verifyCode;

    /**
     * 验证码类型（必填）
     */
    @NotBlank(message = "验证码类型不能为空")
    private String codeType;

    /**
     * 是否删除验证码（校验成功后删除，默认 true）
     */
    @Builder.Default
    private Boolean deleteAfterValidate = true;
}

