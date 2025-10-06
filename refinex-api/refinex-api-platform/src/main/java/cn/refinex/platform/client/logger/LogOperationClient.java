package cn.refinex.platform.client.logger;

import cn.refinex.common.constants.FeignConstants;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.client.logger.dto.request.LogOperationCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 操作日志服务 Feign 客户端
 *
 * @author Refinex
 * @since 1.0.0
 */
@FeignClient(
        name = FeignConstants.PLATFORM_SERVICE,
        path = FeignConstants.PLATFORM_API_PREFIX
)
@Tag(name = "操作日志服务 Feign API")
public interface LogOperationClient {

    @PostMapping("/logger/create")
    @Operation(summary = "保存操作日志")
    @Parameter(name = "request", description = "操作日志创建请求")
    ApiResult<Boolean> saveLogOperation(LogOperationCreateRequest request);

    /**
     * 异步保存操作日志
     *
     * @param request 操作日志创建请求
     */
    @Async
    default void saveLogOperationAsync(LogOperationCreateRequest request) {
        saveLogOperation(request);
    }
}
