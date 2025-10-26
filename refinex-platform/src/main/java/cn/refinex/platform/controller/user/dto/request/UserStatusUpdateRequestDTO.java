package cn.refinex.platform.controller.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户状态更新请求 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "用户状态更新请求")
public class UserStatusUpdateRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private Long userId;

    @NotNull(message = "用户状态不能为空")
    @Schema(description = "用户状态：0待激活，1正常，2冻结，3注销", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer userStatus;

    @Schema(description = "状态变更原因")
    private String reason;
}

