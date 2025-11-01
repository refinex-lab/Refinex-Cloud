package cn.refinex.ai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * AI模型使用统计实体类
 * <p>
 * 对应数据库表：ai_model_statistics
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI模型使用统计实体")
public class AiModelStatistics {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "模型编码", example = "QWEN_MAX")
    private String modelCode;

    @Schema(description = "统计日期", example = "2025-11-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate statDate;

    @Schema(description = "总请求数", example = "1000")
    private Long totalRequests;

    @Schema(description = "成功请求数", example = "980")
    private Long successRequests;

    @Schema(description = "失败请求数", example = "20")
    private Long failedRequests;

    @Schema(description = "总token消耗", example = "500000")
    private Long totalTokens;

    @Schema(description = "总成本(分)", example = "10000")
    private Long totalCost;

    @Schema(description = "平均响应时间(毫秒)", example = "1500")
    private Integer avgResponseTimeMs;

    @Schema(description = "P95响应时间", example = "2500")
    private Integer p95ResponseTimeMs;

    @Schema(description = "P99响应时间", example = "3500")
    private Integer p99ResponseTimeMs;

    @Schema(description = "独立用户数", example = "150")
    private Integer uniqueUsers;

    @Schema(description = "创建时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

