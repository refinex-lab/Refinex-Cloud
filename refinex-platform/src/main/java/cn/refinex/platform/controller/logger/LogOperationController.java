package cn.refinex.platform.controller.logger;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.platform.controller.logger.dto.request.LogOperationCreateRequestDTO;
import cn.refinex.platform.controller.logger.dto.request.LogOperationQueryRequestDTO;
import cn.refinex.platform.controller.logger.dto.response.LogOperationResponseDTO;
import cn.refinex.platform.controller.logger.dto.response.LogOperationStatisticsDTO;
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
@SaIgnore
@Validated
@RestController
@RequestMapping("/logger")
@RequiredArgsConstructor
@Tag(name = "操作日志管理控制器", description = "提供操作日志的记录、查询和统计功能")
public class LogOperationController {

    private final LogOperationService logOperationService;

    @PostMapping
    @Operation(summary = "保存操作日志", description = "创建新的操作日志记录")
    @Parameter(name = "request", description = "操作日志创建请求", required = true)
    public ApiResult<Boolean> saveLogOperation(@RequestBody @Valid LogOperationCreateRequestDTO request) {
        logOperationService.saveLogOperation(request);
        return ApiResult.success(true);
    }

    @PostMapping("/async")
    @Operation(summary = "异步保存操作日志", description = "异步创建新的操作日志记录")
    @Parameter(name = "request", description = "操作日志创建请求", required = true)
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
    @Operation(summary = "分页查询操作日志", description = "根据条件分页查询操作日志")
    @Parameter(name = "query", description = "查询条件", required = true)
    @Parameter(name = "pageNum", description = "页码", required = true, example = "1")
    @Parameter(name = "pageSize", description = "每页数量", required = true, example = "10")
    public ApiResult<cn.refinex.common.jdbc.page.PageResult<LogOperationResponseDTO>> searchOperationLogs(
            @RequestBody @Valid LogOperationQueryRequestDTO query,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        PageResult<LogOperationResponseDTO> result = logOperationService.pageQuery(query, new PageRequest(pageNum, pageSize));
        return ApiResult.success(result);
    }

    @PostMapping("/statistics")
    @Operation(summary = "获取操作日志统计", description = "根据条件获取操作日志统计信息")
    @Parameter(name = "query", description = "查询条件", required = false)
    public ApiResult<LogOperationStatisticsDTO> getOperationLogStatistics(@RequestBody(required = false) LogOperationQueryRequestDTO query) {
        LogOperationQueryRequestDTO queryRequest = query != null ? query : new LogOperationQueryRequestDTO();
        LogOperationStatisticsDTO statistics = logOperationService.getStatistics(queryRequest);
        return ApiResult.success(statistics);
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "根据用户ID查询操作日志", description = "查询指定用户的操作日志")
    @Parameter(name = "userId", description = "用户ID", required = true)
    @Parameter(name = "pageNum", description = "页码", example = "1")
    @Parameter(name = "pageSize", description = "每页数量", example = "10")
    public ApiResult<PageResult<LogOperationResponseDTO>> getOperationLogsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        PageResult<LogOperationResponseDTO> result = logOperationService.getByUserId(userId, new PageRequest(pageNum, pageSize));
        return ApiResult.success(result);
    }

    @GetMapping("/modules/{operationModule}")
    @Operation(summary = "根据操作模块查询操作日志", description = "查询指定操作模块的操作日志")
    @Parameter(name = "operationModule", description = "操作模块", required = true)
    @Parameter(name = "pageNum", description = "页码", example = "1")
    @Parameter(name = "pageSize", description = "每页数量", example = "10")
    public ApiResult<PageResult<LogOperationResponseDTO>> getOperationLogsByModule(
            @PathVariable String operationModule,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        PageResult<LogOperationResponseDTO> result = logOperationService.getByOperationModule(operationModule, new PageRequest(pageNum, pageSize));
        return ApiResult.success(result);
    }
}
