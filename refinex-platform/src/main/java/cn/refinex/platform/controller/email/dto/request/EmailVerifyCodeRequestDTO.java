package cn.refinex.platform.controller.email.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码发送请求 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerifyCodeRequestDTO {

    /**
     * 邮箱地址（必填）
     */
    @NotBlank(message = "邮箱地址不能为空")
    @Email(message = "邮箱地址格式不正确")
    private String email;

    /**
     * 验证码类型（必填）
     * <p>
     * REGISTER - 注册验证码
     * LOGIN - 登录验证码
     * RESET_PASSWORD - 重置密码验证码
     * CHANGE_EMAIL - 更换邮箱验证码
     */
    @NotBlank(message = "验证码类型不能为空")
    private String codeType;

    /**
     * 验证场景补充说明
     */
    private String verifyScene;

    /**
     * 客户端 IP
     */
    private String clientIp;

    /**
     * 模板编码（不指定则使用默认模板）
     */
    private String templateCode;

    /**
     * SMTP 配置 ID（不指定则使用默认配置）
     */
    private String smtpConfigId;
}

