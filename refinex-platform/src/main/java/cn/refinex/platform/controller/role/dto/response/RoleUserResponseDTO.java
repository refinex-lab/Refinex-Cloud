package cn.refinex.platform.controller.role.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色用户响应 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "角色用户响应")
public class RoleUserResponseDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号（脱敏）")
    private String mobile;

    @Schema(description = "邮箱（脱敏）")
    private String email;

    @Schema(description = "用户状态")
    private Integer userStatus;

    @Schema(description = "用户状态标签")
    private String userStatusLabel;

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "有效开始时间")
    private LocalDateTime validFrom;

    @Schema(description = "有效结束时间（临时授权）")
    private LocalDateTime validUntil;

    @Schema(description = "是否临时授权")
    private Boolean isTemporary;

    @Schema(description = "绑定时间")
    private LocalDateTime bindTime;
}

