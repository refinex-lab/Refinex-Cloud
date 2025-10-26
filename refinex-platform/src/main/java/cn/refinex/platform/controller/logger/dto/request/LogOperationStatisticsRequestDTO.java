package cn.refinex.platform.controller.logger.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志统计请求
 * <p>
 * 统计维度：
 * 1. 时间维度：today(今日)/week(本周)/month(本月)/year(本年)/custom(自定义)
 * 2. 分组维度：按操作类型、按操作模块、Top用户
 * 3. 过滤条件：操作模块、用户名
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "操作日志统计请求")
public class LogOperationStatisticsRequestDTO {

    /**
     * see {@link cn.refinex.platform.enums.TimeDimension}
     */
    @NotBlank(message = "时间维度不能为空")
    @Schema(description = "时间维度：today(今日)/week(本周)/month(本月)/year(本年)/custom(自定义)",
            example = "today",
            allowableValues = {"today", "week", "month", "year", "custom"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String timeDimension;

    @Schema(description = "自定义开始时间（timeDimension=custom 时必填）", example = "2025-10-01T00:00:00")
    private LocalDateTime startTime;

    @Schema(description = "自定义结束时间（timeDimension=custom 时必填）", example = "2025-10-31T23:59:59")
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

    @Schema(description = "是否包含按类型分组统计", example = "true", defaultValue = "false")
    @Builder.Default
    private Boolean includeTypeGroup = false;

    @Schema(description = "是否包含按模块分组统计", example = "true", defaultValue = "false")
    @Builder.Default
    private Boolean includeModuleGroup = false;

    @Schema(description = "是否包含Top操作用户", example = "true", defaultValue = "false")
    @Builder.Default
    private Boolean includeTopUsers = false;

    @Schema(description = "Top用户数量", example = "10", defaultValue = "10")
    @Builder.Default
    private Integer topUserLimit = 10;
}

