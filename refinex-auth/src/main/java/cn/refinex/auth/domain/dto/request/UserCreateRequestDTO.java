package cn.refinex.auth.domain.dto.request;

import cn.refinex.auth.enums.RegisterSource;
import cn.refinex.auth.enums.UserRegisterType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 创建用户请求参数
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "创建用户请求参数")
public class UserCreateRequestDTO {

    @NotNull(message = "用户名不能为空")
    @Schema(description = "用户名", example = "refinex", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "昵称(可选，为空则后台随机生成)", example = "Refinex")
    private String nickname;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    @Schema(description = "手机号(可选，用于手机号注册场景)", example = "13800000000")
    private String mobile;

    @Email(message = "邮箱格式错误")
    @Schema(description = "邮箱(可选，用于邮箱注册场景)", example = "refinex@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotNull(message = "密码不能为空")
    @Min(value = 6, message = "密码长度不能小于6位")
    @Max(value = 20, message = "密码长度不能大于20位")
    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    /**
     * 参考 {@link RegisterSource}
     */
    @NotNull(message = "注册来源不能为空")
    @Schema(description = "注册来源", example = "WEB", allowableValues = {"WEB", "ISO", "ANDROID", "H5"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String registerSource;

    /**
     * 参考 {@link UserRegisterType}
     */
    @NotNull(message = "注册类型不能为空")
    @Schema(description = "注册类型", example = "MOBILE", allowableValues = {"MOBILE", "EMAIL"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String registerType;

}
