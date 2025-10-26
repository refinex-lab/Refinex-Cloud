package cn.refinex.platform.controller.logger.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志趋势分析请求
 * <p>
 * 支持按小时、天、周、月等维度分析操作日志趋势
 * 用于前端绘制趋势图表
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "操作日志趋势分析请求")
public class LogOperationTrendRequestDTO {

    /**
     * see {@link cn.refinex.platform.enums.TrendDimension}
     */
    @NotBlank(message = "趋势维度不能为空")
    @Schema(description = "趋势维度：hour(按小时)/day(按天)/week(按周)/month(按月)",
            example = "day",
            allowableValues = {"hour", "day", "week", "month"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String trendDimension;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间", example = "2025-10-01T00:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间", example = "2025-10-31T23:59:59", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime endTime;

    /**
     * see {@link cn.refinex.platform.enums.ApplicationName}
     */
    @Schema(description = "应用名称（可选，用于过滤特定应用）", example = "refinex-platform")
    private String applicationName;

    @Schema(description = "操作模块（可选，用于过滤特定模块）", example = "user")
    private String operationModule;

    @Schema(description = "用户名（可选，用于过滤特定用户）", example = "admin")
    private String username;
}

