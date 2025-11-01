package cn.refinex.ai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 生成任务实体类
 * <p>
 * 对应数据库表：ai_generation_task
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "生成任务实体")
public class AiGenerationTask {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "任务唯一标识,UUID格式", example = "550e8400-e29b-41d4-a716-446655440000")
    private String taskGuid;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "任务类型:IMAGE,VIDEO", example = "IMAGE")
    private String taskType;

    @Schema(description = "任务队列名称", example = "default")
    private String queueName;

    @Schema(description = "优先级0-9,数字越大优先级越高", example = "5")
    private Integer priority;

    @Schema(description = "模型编码", example = "STABLE_DIFFUSION")
    private String modelCode;

    @Schema(description = "提示词")
    private String prompt;

    @Schema(description = "反向提示词")
    private String negativePrompt;

    @Schema(description = "任务参数")
    private String taskParams;

    @Schema(description = "任务状态:0排队中,1生成中,2已完成,3失败,4已取消", example = "2")
    private Integer taskStatus;

    @Schema(description = "进度百分比0-100", example = "100")
    private Integer progress;

    @Schema(description = "重试次数", example = "0")
    private Integer retryCount;

    @Schema(description = "最大重试次数", example = "3")
    private Integer maxRetry;

    @Schema(description = "生成结果URL数组")
    private String resultUrls;

    @Schema(description = "任务成本,单位分", example = "50")
    private Long taskCost;

    @Schema(description = "回调地址")
    private String callbackUrl;

    @Schema(description = "回调状态:0未回调,1成功,2失败", example = "1")
    private Integer callbackStatus;

    @Schema(description = "预估耗时(秒)", example = "30")
    private Integer estimatedDuration;

    @Schema(description = "实际耗时(秒)", example = "28")
    private Integer actualDuration;

    @Schema(description = "开始时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "完成时间", example = "2025-11-01 14:30:28")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completeTime;

    @Schema(description = "失败原因")
    private String failReason;

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
}

