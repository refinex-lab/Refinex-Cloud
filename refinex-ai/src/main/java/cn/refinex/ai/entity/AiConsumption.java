package cn.refinex.ai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI消费记录实体类
 * <p>
 * 对应数据库表：ai_consumption
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI消费记录实体")
public class AiConsumption {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "会话ID", example = "10")
    private Long conversationId;

    @Schema(description = "消息ID", example = "100")
    private Long messageId;

    @Schema(description = "请求ID(用于追踪)", example = "req_550e8400e29b41d4")
    private String requestId;

    @Schema(description = "模型编码", example = "QWEN_MAX")
    private String modelCode;

    @Schema(description = "消费类型:CHAT,IMAGE,VIDEO", example = "CHAT")
    private String consumptionType;

    @Schema(description = "输入token数", example = "100")
    private Integer promptTokens;

    @Schema(description = "输出token数", example = "300")
    private Integer completionTokens;

    @Schema(description = "总token数", example = "400")
    private Integer totalTokens;

    @Schema(description = "单价,每千token价格,单位分", example = "2")
    private Long unitPrice;

    @Schema(description = "总成本,单位分", example = "8")
    private Long totalCost;

    @Schema(description = "是否异常消费:0否,1是", example = "0")
    private Integer isAbnormal;

    @Schema(description = "异常原因")
    private String abnormalReason;

    @Schema(description = "响应时间(毫秒)", example = "1500")
    private Integer responseTimeMs;

    @Schema(description = "消费时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime consumptionTime;

    @Schema(description = "创建时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

