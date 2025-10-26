package cn.refinex.platform.controller.logger;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.platform.controller.logger.dto.request.LogLoginQueryRequestDTO;
import cn.refinex.platform.controller.logger.dto.response.LogLoginResponseDTO;
import cn.refinex.platform.controller.logger.dto.response.LogLoginStatisticsResponseDTO;
import cn.refinex.platform.service.LogLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 登录日志控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/logger/login")
@RequiredArgsConstructor
@Tag(name = "登录日志管理", description = "提供登录日志的查询、统计和分析功能")
public class LogLoginController {

    private final LogLoginService logLoginService;

    @GetMapping("/{id}")
    @Operation(summary = "获取登录日志详情", description = "根据ID获取登录日志详细信息")
    @Parameter(name = "id", description = "日志ID", required = true)
    public ApiResult<LogLoginResponseDTO> getLoginLog(@PathVariable Long id) {
        LogLoginResponseDTO logLogin = logLoginService.getById(id);
        return ApiResult.success(logLogin);
    }

    @PostMapping("/search")
    @Operation(summary = "分页查询登录日志", description = "支持按用户名、登录方式、登录状态、IP、设备类型、时间范围等多维度查询")
    public ApiResult<PageResult<LogLoginResponseDTO>> searchLoginLogs(
            @RequestBody(required = false) LogLoginQueryRequestDTO query,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        LogLoginQueryRequestDTO queryRequest = query != null ? query : new LogLoginQueryRequestDTO();
        PageResult<LogLoginResponseDTO> result = logLoginService.pageQuery(queryRequest, new PageRequest(pageNum, pageSize));
        return ApiResult.success(result);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取登录日志统计", description = "获取登录日志的基础统计信息，包括总数、成功/失败数、成功率、独立用户数、独立IP数")
    public ApiResult<LogLoginStatisticsResponseDTO> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String loginType,
            @RequestParam(required = false) String deviceType) {

        LogLoginStatisticsResponseDTO statistics = logLoginService.getStatistics(startTime, endTime, username, loginType, deviceType);
        return ApiResult.success(statistics);
    }

    @GetMapping("/statistics/by-login-type")
    @Operation(summary = "按登录方式分组统计", description = "统计各登录方式的使用情况")
    public ApiResult<List<Map<String, Object>>> getStatisticsByLoginType(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String deviceType) {

        List<Map<String, Object>> statistics = logLoginService.getStatisticsByLoginType(startTime, endTime, username, deviceType);
        return ApiResult.success(statistics);
    }

    @GetMapping("/statistics/by-device-type")
    @Operation(summary = "按设备类型分组统计", description = "统计各设备类型的登录情况")
    public ApiResult<List<Map<String, Object>>> getStatisticsByDeviceType(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String loginType) {

        List<Map<String, Object>> statistics = logLoginService.getStatisticsByDeviceType(startTime, endTime, username, loginType);
        return ApiResult.success(statistics);
    }

    @GetMapping("/statistics/trend")
    @Operation(summary = "获取登录日志趋势数据", description = "获取按天统计的登录日志趋势数据，用于绘制趋势图表")
    public ApiResult<List<Map<String, Object>>> getTrendData(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String loginType,
            @RequestParam(required = false) String deviceType) {

        List<Map<String, Object>> trendData = logLoginService.getTrendDataByDay(startTime, endTime, username, loginType, deviceType);
        return ApiResult.success(trendData);
    }
}

