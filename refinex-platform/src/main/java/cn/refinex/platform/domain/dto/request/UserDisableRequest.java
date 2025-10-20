package cn.refinex.platform.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 封禁请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "封禁请求")
public class UserDisableRequest {

    @NotNull(message = "用户 ID 不能为空")
    @Schema(description = "用户 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotNull(message = "封禁时长不能为空")
    @Schema(description = "封禁时长（秒），-1 表示永久封禁", example = "86400", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long seconds;

    @Schema(description = "服务类型（可选, 默认为空表示封禁整个账号）", example = "comment", allowableValues = {"comment", "post", "login", "message"})
    private String service;

    @Schema(description = "封禁原因", example = "违规发布不当内容")
    private String reason;

    @Schema(description = "是否同时踢人下线", example = "true")
    private Boolean kickout = true;
}

