package cn.refinex.ai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI消息实体类
 * <p>
 * 对应数据库表：ai_message
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI消息实体")
public class AiMessage {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "对话会话ID", example = "1")
    private Long conversationId;

    @Schema(description = "父消息ID(支持编辑重发)", example = "10")
    private Long parentMessageId;

    @Schema(description = "消息角色:USER,ASSISTANT,SYSTEM", example = "USER")
    private String messageRole;

    @Schema(description = "消息内容")
    private String messageContent;

    @Schema(description = "消息类型:TEXT,IMAGE,VIDEO,FUNCTION", example = "TEXT")
    private String messageType;

    @Schema(description = "是否流式输出:0否,1是", example = "1")
    private Integer isStreaming;

    @Schema(description = "流式结束原因:stop,length,content_filter", example = "stop")
    private String streamFinishReason;

    @Schema(description = "媒体URL数组")
    private String mediaUrls;

    @Schema(description = "输入token数", example = "100")
    private Integer promptTokens;

    @Schema(description = "输出token数", example = "300")
    private Integer completionTokens;

    @Schema(description = "总token数", example = "400")
    private Integer totalTokens;

    @Schema(description = "消息成本,单位分", example = "8")
    private Long messageCost;

    @Schema(description = "响应延迟(毫秒)", example = "1500")
    private Integer latencyMs;

    @Schema(description = "使用的模型", example = "QWEN_MAX")
    private String modelCode;

    @Schema(description = "模型参数")
    private String modelParams;

    @Schema(description = "函数调用信息")
    private String functionCall;

    @Schema(description = "知识库引用")
    private String kbReferences;

    @Schema(description = "消息状态:0发送中,1成功,2失败", example = "1")
    private Integer messageStatus;

    @Schema(description = "审核状态:0待审核,1通过,2拒绝", example = "1")
    private Integer moderationStatus;

    @Schema(description = "审核结果详情")
    private String moderationResult;

    @Schema(description = "审核时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime moderationTime;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "创建时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

