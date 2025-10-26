package cn.refinex.platform.controller.logger.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 操作日志统计响应
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "操作日志统计响应")
public class LogOperationStatisticsResponseDTO {

    @Schema(description = "基础统计")
    private BasicStatistics basicStats;

    @Schema(description = "按操作类型分组统计")
    private List<GroupStatistics> typeGroupStats;

    @Schema(description = "按操作模块分组统计")
    private List<GroupStatistics> moduleGroupStats;

    @Schema(description = "Top 操作用户")
    private List<TopUserStatistics> topUsers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "基础统计")
    public static class BasicStatistics {
        @Schema(description = "总操作次数", example = "1000")
        private Long totalCount;

        @Schema(description = "成功操作次数", example = "985")
        private Long successCount;

        @Schema(description = "失败操作次数", example = "15")
        private Long failureCount;

        @Schema(description = "成功率（百分比）", example = "98.50")
        private Double successRate;

        @Schema(description = "平均执行时间(毫秒)", example = "125.50")
        private Double avgExecutionTime;

        @Schema(description = "最大执行时间(毫秒)", example = "5000")
        private Integer maxExecutionTime;

        @Schema(description = "最小执行时间(毫秒)", example = "10")
        private Integer minExecutionTime;

        @Schema(description = "统计时间范围开始", example = "2025-10-26 00:00:00")
        private String startTime;

        @Schema(description = "统计时间范围结束", example = "2025-10-26 23:59:59")
        private String endTime;

        @Schema(description = "时间维度", example = "today")
        private String timeDimension;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "分组统计")
    public static class GroupStatistics {
        @Schema(description = "分组名称", example = "CREATE")
        private String groupName;

        @Schema(description = "操作次数", example = "250")
        private Long count;

        @Schema(description = "占比（百分比）", example = "25.50")
        private Double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Top 用户统计")
    public static class TopUserStatistics {
        @Schema(description = "用户名", example = "admin")
        private String username;

        @Schema(description = "操作次数", example = "500")
        private Long count;

        @Schema(description = "成功次数", example = "495")
        private Long successCount;

        @Schema(description = "失败次数", example = "5")
        private Long failureCount;

        @Schema(description = "成功率（百分比）", example = "99.00")
        private Double successRate;
    }
}

