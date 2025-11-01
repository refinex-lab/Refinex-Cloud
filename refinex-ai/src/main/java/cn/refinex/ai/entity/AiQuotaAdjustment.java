package cn.refinex.ai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI配额调整历史实体类
 * <p>
 * 对应数据库表：ai_quota_adjustment
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI配额调整历史实体")
public class AiQuotaAdjustment {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "配额类型:CHAT,IMAGE,VIDEO", example = "CHAT")
    private String quotaType;

    @Schema(description = "调整类型:GRANT,DEDUCT,RESET,TRANSFER", example = "GRANT")
    private String adjustmentType;

    @Schema(description = "调整数量(正数增加,负数减少)", example = "100")
    private Integer adjustmentAmount;

    @Schema(description = "调整前数量", example = "500")
    private Integer beforeAmount;

    @Schema(description = "调整后数量", example = "600")
    private Integer afterAmount;

    @Schema(description = "调整原因")
    private String reason;

    @Schema(description = "操作人ID", example = "1")
    private Long operatorId;

    @Schema(description = "业务单号", example = "ORDER_2025110114300001")
    private String bizId;

    @Schema(description = "创建时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

