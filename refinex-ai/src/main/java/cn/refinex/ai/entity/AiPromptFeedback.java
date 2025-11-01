package cn.refinex.ai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 提示词使用反馈实体类
 * <p>
 * 对应数据库表：ai_prompt_feedback
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "提示词使用反馈实体")
public class AiPromptFeedback {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "模板ID", example = "10")
    private Long templateId;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "会话ID", example = "100")
    private Long conversationId;

    @Schema(description = "评分1-5", example = "5")
    private Integer rating;

    @Schema(description = "反馈文本")
    private String feedbackText;

    @Schema(description = "token消耗", example = "500")
    private Integer tokenUsage;

    @Schema(description = "回复质量1-5", example = "5")
    private Integer responseQuality;

    @Schema(description = "创建时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

