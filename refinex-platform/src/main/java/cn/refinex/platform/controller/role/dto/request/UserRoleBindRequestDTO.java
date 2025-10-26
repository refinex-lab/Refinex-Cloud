package cn.refinex.platform.controller.role.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户角色绑定请求 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "用户角色绑定请求")
public class UserRoleBindRequestDTO {

    @Schema(description = "用户ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "用户ID列表不能为空")
    private List<Long> userIds;

    @Schema(description = "有效开始时间", example = "2024-01-01T00:00:00")
    private LocalDateTime validFrom;

    @Schema(description = "有效结束时间（临时授权）", example = "2024-12-31T23:59:59")
    private LocalDateTime validUntil;
}

