package cn.refinex.platform.web;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.api.domain.dto.request.LogOperationCreateRequest;
import cn.refinex.platform.api.facade.LogOperationFacade;
import cn.refinex.platform.service.LogOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志 Feign API 实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class LogOperationClientController implements LogOperationFacade {

    private final LogOperationService logOperationService;

    /**
     * 保存操作日志
     *
     * @param request 操作日志创建请求
     * @return 操作日志创建结果
     */
    @Override
    public ApiResult<Boolean> saveLogOperation(@RequestBody LogOperationCreateRequest request) {
        logOperationService.saveLogOperation(request);
        return ApiResult.success(true);
    }
}
