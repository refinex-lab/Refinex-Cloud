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

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "验证码唯一标识", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String captchaUuid;

    @Schema(description = "验证码文本", example = "8u6h")
    private String captchaCode;

    /**
     * 客户端 ID（可选，前端传递）
     * <p>
     * 可选值：web_admin、mobile_app
     * 如果不传，则默认是 web_admin
     * </p>
     */
    @Schema(description = "客户端 ID", example = "web_admin", allowableValues = {"web_admin", "mobile_app"})
    private String clientId = "web_admin";

    /**
     * 设备类型（可选，前端传递）
     * <p>
     * 可选值：PC、APP、H5
     * 如果不传，则通过 User-Agent 自动识别
     * </p>
     */
    @Schema(description = "设备类型", example = "PC", allowableValues = {"PC", "APP", "H5"})
    private String deviceType;

    /**
     * 是否记住我（默认 false）
     * <p>
     * true=长期有效（7天），false=短期有效（2小时）
     * </p>
     */
    @Schema(description = "是否记住我", example = "false")
    private Boolean rememberMe = false;
}

