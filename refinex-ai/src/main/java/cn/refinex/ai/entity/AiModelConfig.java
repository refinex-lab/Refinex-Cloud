package cn.refinex.ai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI模型配置实体类
 * <p>
 * 对应数据库表：ai_model_config
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI模型配置实体")
public class AiModelConfig {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "模型编码,如GPT4,CLAUDE3,QWEN_MAX", example = "QWEN_MAX")
    private String modelCode;

    @Schema(description = "模型版本号,如gpt-4-0125-preview", example = "gpt-4-0125-preview")
    private String modelVersion;

    @Schema(description = "模型显示名称", example = "通义千问Max")
    private String modelName;

    @Schema(description = "供应商:OPENAI,ANTHROPIC,QWEN,ZHIPU,DEEPSEEK", example = "QWEN")
    private String provider;

    @Schema(description = "模型类型:CHAT,IMAGE,VIDEO,EMBEDDING", example = "CHAT")
    private String modelType;

    @Schema(description = "API接口地址", example = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation")
    private String apiEndpoint;

    @Schema(description = "API密钥,加密存储")
    private String apiKey;

    @Schema(description = "API版本", example = "2024-01-01")
    private String apiVersion;

    @Schema(description = "模型能力,JSON格式")
    private String modelCapabilities;

    @Schema(description = "上下文窗口大小", example = "8192")
    private Integer contextWindow;

    @Schema(description = "最大输出token数", example = "2000")
    private Integer maxTokens;

    @Schema(description = "默认温度参数", example = "0.70")
    private Double temperature;

    @Schema(description = "输入定价,每千token价格,单位分", example = "2")
    private Long pricingInput;

    @Schema(description = "输出定价,每千token价格,单位分", example = "6")
    private Long pricingOutput;

    @Schema(description = "Requests Per Minute限流", example = "60")
    private Integer rpmLimit;

    @Schema(description = "Tokens Per Minute限流", example = "100000")
    private Integer tpmLimit;

    @Schema(description = "请求超时时间(秒)", example = "60")
    private Integer timeoutSeconds;

    @Schema(description = "失败重试次数", example = "3")
    private Integer retryTimes;

    @Schema(description = "熔断器阈值", example = "10")
    private Integer circuitBreakerThreshold;

    @Schema(description = "降级备用模型编码", example = "GPT3_5_TURBO")
    private String fallbackModelCode;

    @Schema(description = "健康检查端点", example = "/health")
    private String healthCheckUrl;

    @Schema(description = "上次健康检查时间", example = "2025-11-01 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastHealthCheckTime;

    @Schema(description = "健康状态:0异常,1正常", example = "1")
    private Integer healthStatus;

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

    @Schema(description = "备注说明", example = "通义千问Max模型配置")
    private String remark;

    @Schema(description = "排序字段", example = "0")
    private Integer sort;

    @Schema(description = "状态:1正常,0停用", example = "1")
    private Integer status;

    @Schema(description = "扩展数据（JSON格式）")
    private String extraData;
}

