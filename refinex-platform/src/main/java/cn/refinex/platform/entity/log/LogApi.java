package cn.refinex.platform.entity.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * API调用日志实体类
 * <p>
 * 对应数据库表：log_api
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API调用日志实体")
public class LogApi {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "链路追踪ID", example = "trace-1234567890")
    private String traceId;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "请求方法", example = "POST")
    private String requestMethod;

    @Schema(description = "请求URL", example = "/api/user/login")
    private String requestUrl;

    @Schema(description = "请求参数（JSON格式）")
    private String requestParams;

    @Schema(description = "响应状态码", example = "200")
    private Integer responseStatus;

    @Schema(description = "响应内容（JSON格式）")
    private String responseBody;

    @Schema(description = "执行耗时，单位毫秒", example = "150")
    private Integer executionTime;

    @Schema(description = "客户端IP", example = "192.168.1.100")
    private String clientIp;

    @Schema(description = "用户代理", example = "Mozilla/5.0...")
    private String userAgent;

    @Schema(description = "请求时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
