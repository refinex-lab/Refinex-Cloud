package cn.refinex.platform.controller.logger.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录日志统计响应DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录日志统计响应DTO")
public class LogLoginStatisticsResponseDTO {

    @Schema(description = "总登录次数", example = "1000")
    private Long totalCount;

    @Schema(description = "成功次数", example = "950")
    private Long successCount;

    @Schema(description = "失败次数", example = "50")
    private Long failureCount;

    @Schema(description = "成功率（百分比）", example = "95.0")
    private Double successRate;

    @Schema(description = "独立用户数", example = "100")
    private Long uniqueUserCount;

    @Schema(description = "独立IP数", example = "80")
    private Long uniqueIpCount;
}

