package cn.refinex.ai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户AI配额实体类
 * <p>
 * 对应数据库表：ai_quota
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户AI配额实体")
public class AiQuota {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "会员等级ID", example = "2")
    private Long levelId;

    @Schema(description = "对话总配额", example = "1000")
    private Integer chatQuotaTotal;

    @Schema(description = "对话已使用配额", example = "250")
    private Integer chatQuotaUsed;

    @Schema(description = "图像生成总配额", example = "100")
    private Integer imageQuotaTotal;

    @Schema(description = "图像生成已使用配额", example = "25")
    private Integer imageQuotaUsed;

    @Schema(description = "视频生成总配额", example = "50")
    private Integer videoQuotaTotal;

    @Schema(description = "视频生成已使用配额", example = "10")
    private Integer videoQuotaUsed;

    @Schema(description = "预警阈值百分比", example = "80")
    private Integer warningThreshold;

    @Schema(description = "是否已发送预警:0否,1是", example = "0")
    private Integer isWarned;

    @Schema(description = "上次预警时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastWarningTime;

    @Schema(description = "是否冻结:0否,1是", example = "0")
    private Integer isFrozen;

    @Schema(description = "冻结原因")
    private String freezeReason;

    @Schema(description = "重置周期:DAILY,MONTHLY", example = "MONTHLY")
    private String resetCycle;

    @Schema(description = "上次重置时间", example = "2025-11-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastResetTime;

    @Schema(description = "下次重置时间", example = "2025-12-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextResetTime;

    @Schema(description = "创建时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

