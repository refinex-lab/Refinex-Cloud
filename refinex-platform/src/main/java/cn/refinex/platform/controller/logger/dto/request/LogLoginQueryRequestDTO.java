package cn.refinex.platform.controller.logger.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 登录日志查询请求DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录日志查询请求DTO")
public class LogLoginQueryRequestDTO {

    @Schema(description = "用户名（模糊查询）", example = "admin")
    private String username;

    @Schema(description = "登录方式", example = "PASSWORD")
    private String loginType;

    @Schema(description = "登录状态：0成功,1失败", example = "0")
    private Integer loginStatus;

    @Schema(description = "登录IP", example = "192.168.1.100")
    private String loginIp;

    @Schema(description = "设备类型", example = "PC")
    private String deviceType;

    @Schema(description = "开始时间", example = "2025-10-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2025-10-31 23:59:59")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}

