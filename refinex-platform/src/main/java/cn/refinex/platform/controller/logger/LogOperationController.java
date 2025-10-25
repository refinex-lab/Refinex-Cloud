package cn.refinex.platform.controller.logger;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.controller.logger.dto.request.LogOperationCreateRequestDTO;
import cn.refinex.platform.service.LogOperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@Tag(name = "操作日志服务控制器", description = "提供操作日志记录功能")
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
}
