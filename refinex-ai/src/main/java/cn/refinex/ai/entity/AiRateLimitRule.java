package cn.refinex.ai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI流控规则配置实体类
 * <p>
 * 对应数据库表：ai_rate_limit_rule
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI流控规则配置实体")
public class AiRateLimitRule {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "规则名称", example = "普通用户每分钟限流")
    private String ruleName;

    @Schema(description = "规则类型:USER,MODEL,GLOBAL", example = "USER")
    private String ruleType;

    @Schema(description = "目标ID(用户ID或模型ID)", example = "1")
    private Long targetId;

    @Schema(description = "模型编码", example = "QWEN_MAX")
    private String modelCode;

    @Schema(description = "限流周期:SECOND,MINUTE,HOUR,DAY", example = "MINUTE")
    private String limitPeriod;

    @Schema(description = "周期内限制次数", example = "60")
    private Integer limitCount;

    @Schema(description = "是否启用:0否,1是", example = "1")
    private Integer isEnabled;

    @Schema(description = "优先级", example = "100")
    private Integer priority;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1")
    private Long updateBy;

    @Schema(description = "更新时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除标记:0未删除,1已删除", example = "0")
    private Integer deleted;

    @Schema(description = "乐观锁版本号", example = "0")
    private Integer version;

    @Schema(description = "备注说明")
    private String remark;

    @Schema(description = "状态:0正常,1停用", example = "0")
    private Integer status;
}

