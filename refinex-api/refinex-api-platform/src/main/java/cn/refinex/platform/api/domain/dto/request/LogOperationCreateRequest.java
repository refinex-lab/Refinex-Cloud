package cn.refinex.platform.api.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志创建请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Feign API - 操作日志创建请求")
public class LogOperationCreateRequest {

    @Schema(description = "用户 ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "应用名称", example = "refinex-platform")
    private String applicationName;

    @Schema(description = "操作模块", example = "platform")
    private String operationModule;

    @Schema(description = "操作类型", example = "CREATE")
    private String operationType;

    @Schema(description = "操作描述", example = "创建用户")
    private String operationDesc;

    @Schema(description = "请求方法", example = "POST")
    private String requestMethod;

    @Schema(description = "请求路径", example = "/platform/user/create")
    private String requestUrl;

    @Schema(description = "请求参数", example = "{\"username\": \"admin\", \"password\": \"123456\"}")
    private String requestParams;

    @Schema(description = "请求体", example = "{\"username\": \"admin\", \"password\": \"123456\"}")
    private String requestBody;

    @Schema(description = "响应结果", example = "{\"code\": 200, \"message\": \"success\"}")
    private String responseResult;

    @Schema(description = "操作 IP", example = "127.0.0.1")
    private String operationIp;

    @Schema(description = "操作位置", example = "127.0.0.1")
    private String operationLocation;

    @Schema(description = "浏览器", example = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
    private String browser;

    @Schema(description = "操作系统", example = "Windows NT 10.0")
    private String os;

    @Schema(description = "操作状态(0成功,1失败)", example = "200")
    private Integer operationStatus;

    @Schema(description = "错误信息", example = "cause by ...")
    private String errorMessage;

    @Schema(description = "执行时间(毫秒)", example = "1000")
    private Integer executionTime;

    @Schema(description = "创建时间", example = "2025-10-05T10:00:00")
    private LocalDateTime createTime;
}
