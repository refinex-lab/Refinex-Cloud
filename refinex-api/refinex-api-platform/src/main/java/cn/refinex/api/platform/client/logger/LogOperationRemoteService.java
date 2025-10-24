package cn.refinex.api.platform.client.logger;

import cn.refinex.api.platform.client.logger.dto.request.LogOperationCreateRequestDTO;
import cn.refinex.common.constants.SystemFeignConstants;
import cn.refinex.common.domain.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 操作日志服务 OpenFeign 接口契约
 *
 * @author Refinex
 * @since 1.0.0
 */
@FeignClient(name = SystemFeignConstants.PLATFORM_SERVICE, contextId = "logOperationServiceClient")
@Tag(name = "操作日志服务 OpenFeign 接口契约", description = "定义操作日志服务相关的 OpenFeign 接口契约")
public interface LogOperationRemoteService {

    @PostMapping("/logger")
    @Operation(summary = "保存操作日志", description = "创建新的操作日志记录")
    @Parameter(name = "request", description = "操作日志创建请求", required = true)
    ApiResult<Boolean> saveLogOperation(@RequestBody LogOperationCreateRequestDTO request);

    /**
     * 异步保存操作日志
     * <p>
     * 默认方法，调用同步保存接口，具体异步实现由调用方控制。
     * </p>
     *
     * @param request 操作日志创建请求
     */
    @Async
    default void saveLogOperationAsync(@RequestBody LogOperationCreateRequestDTO request) {
        saveLogOperation(request);
    }
}
