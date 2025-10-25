package cn.refinex.platform.controller.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户会话信息 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户会话信息")
public class UserSessionResponseDTO {

    /**
     * Token 值
     */
    @Schema(description = "Token 值")
    private String tokenValue;

    /**
     * 设备类型
     */
    @Schema(description = "设备类型", example = "PC")
    private String deviceType;

    /**
     * 登录时间（时间戳，秒）
     */
    @Schema(description = "登录时间（时间戳，秒）")
    private Long loginTime;

    /**
     * Token 剩余有效期（秒）
     */
    @Schema(description = "Token 剩余有效期（秒）")
    private Long tokenTimeout;

    /**
     * 最后活跃时间（时间戳，秒）
     */
    @Schema(description = "最后活跃时间（时间戳，秒）")
    private Long lastActivityTime;
}

