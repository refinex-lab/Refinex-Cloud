package cn.refinex.platform.controller.logger;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.platform.controller.logger.dto.request.LogOperationCreateRequestDTO;
import cn.refinex.platform.controller.logger.dto.request.LogOperationQueryRequestDTO;
import cn.refinex.platform.controller.logger.dto.request.LogOperationStatisticsRequestDTO;
import cn.refinex.platform.controller.logger.dto.request.LogOperationTrendRequestDTO;
import cn.refinex.platform.controller.logger.dto.response.LogOperationResponseDTO;
import cn.refinex.platform.controller.logger.dto.response.LogOperationStatisticsResponseDTO;
import cn.refinex.platform.controller.logger.dto.response.LogOperationTrendResponseDTO;
import cn.refinex.platform.service.LogOperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志服务控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/logger")
@RequiredArgsConstructor
@Tag(name = "操作日志管理", description = "提供操作日志的记录、查询、统计和趋势分析功能")
public class LogOperationController {

    private final LogOperationService logOperationService;

    @SaIgnore
    @PostMapping
    @Operation(summary = "保存操作日志", description = "创建新的操作日志记录")
    public ApiResult<Boolean> saveLogOperation(@RequestBody @Valid LogOperationCreateRequestDTO request) {
        logOperationService.saveLogOperation(request);
        return ApiResult.success(true);
    }

    @SaIgnore
    @PostMapping("/async")
    @Operation(summary = "异步保存操作日志", description = "异步创建新的操作日志记录（真正的异步）")
    public ApiResult<Boolean> saveLogOperationAsync(@RequestBody @Valid LogOperationCreateRequestDTO request) {
        logOperationService.saveLogOperationAsync(request);
        return ApiResult.success(true);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取操作日志详情", description = "根据ID获取操作日志详细信息")
    @Parameter(name = "id", description = "日志ID", required = true)
    public ApiResult<LogOperationResponseDTO> getOperationLog(@PathVariable Long id) {
        LogOperationResponseDTO logOperation = logOperationService.getById(id);
        return ApiResult.success(logOperation);
    }

    @PostMapping("/search")
    @Operation(summary = "分页查询操作日志", description = "支持按应用名称、用户名、操作模块、操作类型、操作状态、IP、时间范围等多维度查询。")
    public ApiResult<PageResult<LogOperationResponseDTO>> searchOperationLogs(
            @RequestBody(required = false) LogOperationQueryRequestDTO query,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        LogOperationQueryRequestDTO queryRequest = query != null ? query : new LogOperationQueryRequestDTO();
        PageResult<LogOperationResponseDTO> result = logOperationService.pageQuery(queryRequest, new PageRequest(pageNum, pageSize));
        return ApiResult.success(result);
    }

    @PostMapping("/statistics")
    @Operation(
            summary = "获取操作日志统计",
            description = """
                    支持灵活的统计维度：
                    "1. 时间维度：today(今日)/week(本周)/month(本月)/year(本年)/custom(自定义)
                    "2. 分组统计：按操作类型、按操作模块（可选）
                    "3. Top用户统计：最活跃的操作用户（可选）
                    "4. 基础统计：总数、成功/失败数、成功率、执行时间统计
                    """
    )
    public ApiResult<LogOperationStatisticsResponseDTO> getStatistics(@RequestBody @Valid LogOperationStatisticsRequestDTO request) {
        LogOperationStatisticsResponseDTO statistics = logOperationService.getStatistics(request);
        return ApiResult.success(statistics);
    }

    @PostMapping("/statistics/trend")
    @Operation(
            summary = "获取操作日志趋势分析",
            description = """
                    支持按小时、天、周、月等维度分析操作日志趋势，返回时间序列数据用于绘制趋势图表。
                    趋势维度：hour(按小时)/day(按天)/week(按周)/month(按月)
                    """
    )
    public ApiResult<LogOperationTrendResponseDTO> getTrend(@RequestBody @Valid LogOperationTrendRequestDTO request) {
        LogOperationTrendResponseDTO trend = logOperationService.getTrend(request);
        return ApiResult.success(trend);
    }
}
