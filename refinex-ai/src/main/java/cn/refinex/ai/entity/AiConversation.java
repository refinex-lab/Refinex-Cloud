package cn.refinex.ai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI对话会话实体类
 * <p>
 * 对应数据库表：ai_conversation
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI对话会话实体")
public class AiConversation {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "全局唯一标识,UUID格式", example = "550e8400-e29b-41d4-a716-446655440000")
    private String conversationGuid;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "会话标题", example = "关于Spring AI的讨论")
    private String conversationTitle;

    @Schema(description = "会话类型:GENERAL,KB_QA,IMAGE_GEN,VIDEO_GEN", example = "GENERAL")
    private String conversationType;

    @Schema(description = "使用的模型编码", example = "QWEN_MAX")
    private String modelCode;

    @Schema(description = "关联的知识库空间ID", example = "10")
    private Long kbSpaceId;

    @Schema(description = "系统提示词")
    private String systemPrompt;

    @Schema(description = "上下文策略:SLIDING_WINDOW,SUMMARIZE,TRUNCATE", example = "SLIDING_WINDOW")
    private String contextStrategy;

    @Schema(description = "最大上下文消息数", example = "20")
    private Integer maxContextMessages;

    @Schema(description = "温度参数快照", example = "0.70")
    private BigDecimal temperature;

    @Schema(description = "单次请求最大token数", example = "2000")
    private Integer maxTokensPerRequest;

    @Schema(description = "会话状态:0进行中,1已结束,2已删除", example = "0")
    private Integer conversationStatus;

    @Schema(description = "是否置顶:0否,1是", example = "0")
    private Integer isPinned;

    @Schema(description = "分享令牌")
    private String shareToken;

    @Schema(description = "分享过期时间", example = "2025-11-08 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime shareExpireTime;

    @Schema(description = "消息数量", example = "15")
    private Integer messageCount;

    @Schema(description = "总消耗token数", example = "5000")
    private Long totalTokens;

    @Schema(description = "总消耗金额,单位分", example = "100")
    private Long totalCost;

    @Schema(description = "最后消息时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageTime;

    @Schema(description = "会话过期时间", example = "2025-12-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

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

    @Schema(description = "排序字段", example = "0")
    private Integer sort;

    @Schema(description = "状态:1正常,0停用", example = "1")
    private Integer status;

    @Schema(description = "扩展数据（JSON格式）")
    private String extraData;
}

