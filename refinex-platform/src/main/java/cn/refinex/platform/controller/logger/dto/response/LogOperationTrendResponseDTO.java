package cn.refinex.platform.controller.logger.dto.response;

import cn.refinex.platform.enums.TimeDimension;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 操作日志趋势分析响应
 * <p>
 * 返回时间序列数据，用于前端绘制趋势图表
 * 例如：最近7天每天的操作量、成功率等
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "操作日志趋势分析响应")
public class LogOperationTrendResponseDTO {

    /**
     * see {@link TimeDimension}
     */
    @Schema(description = "趋势维度", example = "day")
    private String trendDimension;

    @Schema(description = "趋势数据点列表")
    private List<TrendDataPoint> trendData;

    @Schema(description = "数据点总数", example = "7")
    private Integer dataPointCount;

    /**
     * 趋势数据点
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "趋势数据点")
    public static class TrendDataPoint {
        @Schema(description = "时间点", example = "2025-10-26")
        private String timePoint;

        @Schema(description = "总操作次数", example = "150")
        private Long totalCount;

        @Schema(description = "成功次数", example = "148")
        private Long successCount;

        @Schema(description = "失败次数", example = "2")
        private Long failureCount;

        @Schema(description = "成功率（百分比）", example = "98.67")
        private Double successRate;

        @Schema(description = "平均执行时间(毫秒)", example = "120.50")
        private Double avgExecutionTime;
    }
}

