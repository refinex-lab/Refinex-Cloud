package cn.refinex.platform.controller.user.dto.request;

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
public class DisableRequest {

    /**
     * 用户 ID
     */
    @NotNull(message = "用户 ID 不能为空")
    @Schema(description = "用户 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    /**
     * 封禁时长（秒）
     * <p>
     * -1 表示永久封禁
     * </p>
     */
    @NotNull(message = "封禁时长不能为空")
    @Schema(description = "封禁时长（秒），-1 表示永久封禁", example = "86400", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long seconds;

    /**
     * 服务类型（可选）
     * <p>
     * 如果指定，则只封禁该服务
     * 如果不指定，则封禁整个账号
     * </p>
     */
    @Schema(description = "服务类型（可选）", example = "comment", allowableValues = {"comment", "post", "login", "message"})
    private String service;

    /**
     * 封禁原因
     */
    @Schema(description = "封禁原因", example = "违规发布不当内容")
    private String reason;

    /**
     * 是否同时踢人下线
     */
    @Schema(description = "是否同时踢人下线", example = "true")
    private Boolean kickout = true;
}

