package cn.refinex.platform.domain.dto.request;

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
public class UserKickoutRequest {

    /**
     * 用户 ID
     */
    @NotNull(message = "用户 ID 不能为空")
    @Schema(description = "用户 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    /**
     * 设备类型（可选）
     * <p>
     * 如果指定，则只踢出该设备类型的会话
     * 如果不指定，则踢出所有设备的会话
     * </p>
     */
    @Schema(description = "设备类型（可选）", example = "PC", allowableValues = {"PC", "APP", "H5"})
    private String deviceType;

    /**
     * Token 值（可选）
     * <p>
     * 如果指定，则只踢出该 Token 对应的会话
     * 优先级：tokenValue > deviceType > userId
     * </p>
     */
    @Schema(description = "Token 值（可选）")
    private String tokenValue;

    /**
     * 下线原因（可选）
     */
    @Schema(description = "下线原因（可选）", example = "异常登录")
    private String reason;
}

