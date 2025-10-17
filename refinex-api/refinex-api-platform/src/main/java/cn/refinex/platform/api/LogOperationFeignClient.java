package cn.refinex.platform.api;

import cn.refinex.common.constants.SystemFeignConstants;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.domain.dto.request.LogOperationCreateRequest;
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
@Tag(name = "操作日志服务 Feign API")
@FeignClient(
        name = SystemFeignConstants.PLATFORM_SERVICE,
        path = "/logger",
        contextId = "logOperationFeignClient"
)
public interface LogOperationFeignClient {

    @PostMapping("/logger/create")
    @Operation(summary = "保存操作日志")
    @Parameter(name = "request", description = "操作日志创建请求")
    ApiResult<Boolean> saveLogOperation(@RequestBody LogOperationCreateRequest request);

    /**
     * 异步保存操作日志
     *
     * @param request 操作日志创建请求
     */
    @Async
    default void saveLogOperationAsync(@RequestBody LogOperationCreateRequest request) {
        saveLogOperation(request);
    }
}
