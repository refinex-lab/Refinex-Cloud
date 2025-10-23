package cn.refinex.auth.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 记录登录日志请求 DTO
 * <p>
 * 用于承载异步记录登录日志所需的参数，避免方法参数过多
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "记录登录日志请求")
public class RecordLoginLogRequest {

    @Schema(description = "用户 ID（失败时可能为 null）")
    private Long userId;

    @Schema(description = "用户名（失败时可能为 null）")
    private String username;

    @Schema(description = "登录类型（对应 LoginType 枚举的 code 值）")
    private Integer loginType;

    @Schema(description = "登录 IP")
    private String loginIp;

    @Schema(description = "User-Agent 字符串")
    private String userAgent;

    @Schema(description = "设备类型（PC、APP、H5）")
    private String deviceType;

    @Schema(description = "登录状态（0 成功, 1 失败）")
    private Integer loginStatus;

    @Schema(description = "失败原因（成功时为 null）")
    private String failReason;
}

