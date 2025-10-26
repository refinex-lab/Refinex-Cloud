package cn.refinex.platform.controller.logger.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志查询请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "操作日志查询请求")
public class LogOperationQueryRequestDTO {

    @Schema(description = "用户名，支持模糊查询", example = "admin")
    private String username;

    /**
     * see {@link cn.refinex.platform.enums.ApplicationName}
     */
    @Schema(description = "应用名称", example = "refinex-platform")
    private String applicationName;

    @Schema(description = "操作模块", example = "user")
    private String operationModule;

    /**
     * see {@link cn.refinex.common.apilog.core.enums.OperateTypeEnum}
     */
    @Schema(description = "操作类型", example = "CREATE")
    private String operationType;

    @Schema(description = "请求方法", example = "GET", allowableValues = {"GET", "POST", "PUT", "DELETE", "PATCH"})
    private String requestMethod;

    @Schema(description = "操作状态(0成功,1失败)", example = "0")
    private Integer operationStatus;

    @Schema(description = "操作 IP", example = "127.0.0.1")
    private String operationIp;

    @Schema(description = "开始时间", example = "2025-10-01T00:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2025-10-31T23:59:59")
    private LocalDateTime endTime;
}