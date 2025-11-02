package cn.refinex.ai.controller.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 模型配置更新请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "模型配置更新请求")
public class ModelConfigUpdateRequestDTO {

    @NotBlank(message = "模型版本号不能为空")
    @Size(max = 100, message = "模型版本号长度不能超过100")
    @Schema(description = "模型版本号", example = "deepseek-chat", requiredMode = Schema.RequiredMode.REQUIRED)
    private String modelVersion;

    @NotBlank(message = "模型名称不能为空")
    @Size(max = 100, message = "模型名称长度不能超过100")
    @Schema(description = "模型名称", example = "通义千问Max", requiredMode = Schema.RequiredMode.REQUIRED)
    private String modelName;

    @NotBlank(message = "供应商不能为空")
    @Size(max = 50, message = "供应商长度不能超过50")
    @Schema(description = "供应商", example = "QWEN", requiredMode = Schema.RequiredMode.REQUIRED)
    private String provider;

    @NotBlank(message = "模型类型不能为空")
    @Size(max = 20, message = "模型类型长度不能超过20")
    @Schema(description = "模型类型:CHAT,IMAGE,VIDEO,EMBEDDING", example = "CHAT", requiredMode = Schema.RequiredMode.REQUIRED)
    private String modelType;

    @NotBlank(message = "API接口地址不能为空")
    @Size(max = 500, message = "API接口地址长度不能超过500")
    @Schema(description = "API接口地址", example = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apiEndpoint;

    @Size(max = 500, message = "API密钥长度不能超过500")
    @Schema(description = "API密钥（为空则不更新）")
    private String apiKey;

    @Size(max = 20, message = "API版本长度不能超过20")
    @Schema(description = "API版本", example = "2024-01-01")
    private String apiVersion;

    @Schema(description = "模型能力,JSON格式")
    private String modelCapabilities;

    @Min(value = 1, message = "上下文窗口大小必须大于0")
    @Schema(description = "上下文窗口大小", example = "8192")
    private Integer contextWindow;

    @Min(value = 1, message = "最大输出token数必须大于0")
    @Schema(description = "最大输出token数", example = "2000")
    private Integer maxTokens;

    @DecimalMin(value = "0.0", message = "温度参数不能小于0")
    @DecimalMax(value = "2.0", message = "温度参数不能大于2")
    @Schema(description = "默认温度参数", example = "0.70")
    private Double temperature;

    @Min(value = 0, message = "输入定价不能小于0")
    @Schema(description = "输入定价,每千token价格,单位分", example = "2")
    private Long pricingInput;

    @Min(value = 0, message = "输出定价不能小于0")
    @Schema(description = "输出定价,每千token价格,单位分", example = "6")
    private Long pricingOutput;

    @Min(value = 1, message = "RPM限流必须大于0")
    @Schema(description = "Requests Per Minute限流", example = "60")
    private Integer rpmLimit;

    @Min(value = 1, message = "TPM限流必须大于0")
    @Schema(description = "Tokens Per Minute限流", example = "100000")
    private Integer tpmLimit;

    @Min(value = 1, message = "请求超时时间必须大于0")
    @Schema(description = "请求超时时间(秒)", example = "60")
    private Integer timeoutSeconds;

    @Min(value = 0, message = "失败重试次数不能小于0")
    @Schema(description = "失败重试次数", example = "3")
    private Integer retryTimes;

    @Min(value = 1, message = "熔断器阈值必须大于0")
    @Schema(description = "熔断器阈值", example = "10")
    private Integer circuitBreakerThreshold;

    @Size(max = 50, message = "降级备用模型编码长度不能超过50")
    @Schema(description = "降级备用模型编码", example = "GPT3_5_TURBO")
    private String fallbackModelCode;

    @Size(max = 500, message = "健康检查端点长度不能超过500")
    @Schema(description = "健康检查端点", example = "/health")
    private String healthCheckUrl;

    @NotNull(message = "优先级不能为空")
    @Schema(description = "优先级", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer priority;

    @Size(max = 500, message = "备注说明长度不能超过500")
    @Schema(description = "备注说明")
    private String remark;

    @NotNull(message = "排序字段不能为空")
    @Schema(description = "排序字段", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sort;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态只能为0或1")
    @Max(value = 1, message = "状态只能为0或1")
    @Schema(description = "状态:0正常,1停用", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Schema(description = "扩展数据（JSON格式）")
    private String extraData;
}

