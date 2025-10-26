package cn.refinex.platform.controller.logger.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志响应对象
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "操作日志响应")
public class LogOperationResponseDTO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "应用名称")
    private String applicationName;

    @Schema(description = "操作模块")
    private String operationModule;

    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "操作描述")
    private String operationDesc;

    @Schema(description = "请求方法")
    private String requestMethod;

    @Schema(description = "请求路径")
    private String requestUrl;

    @Schema(description = "请求参数")
    private String requestParams;

    @Schema(description = "请求体")
    private String requestBody;

    @Schema(description = "响应结果")
    private String responseResult;

    @Schema(description = "操作 IP")
    private String operationIp;

    @Schema(description = "操作地点")
    private String operationLocation;

    @Schema(description = "浏览器信息")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "操作状态(0成功,1失败)")
    private Integer operationStatus;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "执行耗时(毫秒)")
    private Integer executionTime;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}