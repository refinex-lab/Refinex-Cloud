package cn.refinex.platform.web.external;

import cn.refinex.api.platform.client.LogOperationServiceClient;
import cn.refinex.api.platform.domain.dto.request.LogOperationCreateRequest;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.service.LogOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志服务接口实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class LogOperationClientController implements LogOperationServiceClient {

    private final LogOperationService logOperationService;

    /**
     * 保存操作日志
     *
     * @param request 操作日志创建请求
     * @return 保存结果
     */
    @Override
    public ApiResult<Boolean> saveLogOperation(LogOperationCreateRequest request) {
        logOperationService.saveLogOperation(request);
        return ApiResult.success(true);
    }
}
