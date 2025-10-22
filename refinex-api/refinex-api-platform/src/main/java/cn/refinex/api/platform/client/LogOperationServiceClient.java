package cn.refinex.api.platform.client;

import cn.refinex.api.platform.domain.dto.request.LogOperationCreateRequest;
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
 * 操作日志服务 Feign 客户端
 *
 * @author Refinex
 * @since 1.0.0
 */
@FeignClient(
        name = SystemFeignConstants.PLATFORM_SERVICE,
        contextId = "logOperationServiceClient",
        path = "/internal/operation-logs"
)
@Tag(name = "操作日志服务 Feign 客户端", description = "提供操作日志记录相关的内部 Feign 接口")
public interface LogOperationServiceClient {

    @PostMapping
    @Operation(summary = "保存操作日志", description = "创建新的操作日志记录")
    @Parameter(name = "request", description = "操作日志创建请求", required = true)
    ApiResult<Boolean> saveLogOperation(@RequestBody LogOperationCreateRequest request);

    /**
     * 异步保存操作日志
     * <p>
     * 默认方法，调用同步保存接口，具体异步实现由调用方控制。
     * </p>
     *
     * @param request 操作日志创建请求
     */
    @Async
    default void saveLogOperationAsync(@RequestBody LogOperationCreateRequest request) {
        saveLogOperation(request);
    }
}
