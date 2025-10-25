package cn.refinex.platform.controller.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 踢人下线请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "踢人下线请求")
public class UserKickoutRequestDTO {

    @NotNull(message = "用户 ID 不能为空")
    @Schema(description = "用户 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "设备类型（可选, 默认为空表示踢出所有设备的会话）", example = "PC", allowableValues = {"PC", "APP", "H5"})
    private String deviceType;

    @Schema(description = "Token 值（可选, 如果指定则只踢出该 Token 对应的会话；优先级：tokenValue > deviceType > userId）", example = "123456")
    private String tokenValue;

    @Schema(description = "下线原因（可选）", example = "异常登录")
    private String reason;
}

