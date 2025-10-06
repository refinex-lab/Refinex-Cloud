package cn.refinex.platform.domain.entity.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 * <p>
 * 对应数据库表：log_operation
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "操作日志实体")
public class LogOperation {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "操作人ID", example = "1")
    private Long userId;

    @Schema(description = "操作人用户名", example = "admin")
    private String username;

    @Schema(description = "操作模块", example = "用户管理")
    private String operationModule;

    @Schema(description = "操作类型：CREATE,UPDATE,DELETE,PUBLISH", example = "CREATE")
    private String operationType;

    @Schema(description = "操作描述", example = "创建用户")
    private String operationDesc;

    @Schema(description = "请求方法", example = "POST")
    private String requestMethod;

    @Schema(description = "请求URL", example = "/api/user/create")
    private String requestUrl;

    @Schema(description = "请求参数（JSON格式）")
    private String requestParams;

    @Schema(description = "响应结果（JSON格式）")
    private String responseResult;

    @Schema(description = "操作IP", example = "192.168.1.100")
    private String operationIp;

    @Schema(description = "操作地点", example = "中国|华东|上海市|电信")
    private String operationLocation;

    @Schema(description = "浏览器信息", example = "Chrome 120")
    private String browser;

    @Schema(description = "操作系统", example = "Windows 10")
    private String os;

    @Schema(description = "操作状态：0成功,1失败", example = "0")
    private Integer operationStatus;

    @Schema(description = "错误信息", example = "用户名已存在")
    private String errorMessage;

    @Schema(description = "执行耗时，单位毫秒", example = "150")
    private Integer executionTime;

    @Schema(description = "创建时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
