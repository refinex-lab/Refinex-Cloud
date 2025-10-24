package cn.refinex.platform.controller.remote;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.refinex.api.platform.client.logger.LogOperationRemoteService;
import cn.refinex.api.platform.client.logger.dto.request.LogOperationCreateRequestDTO;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.service.LogOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志服务 OpenFeign 接口实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@SaIgnore
@RestController
@RequiredArgsConstructor
public class LogOperationRemoteController implements LogOperationRemoteService {

    private final LogOperationService logOperationService;

    /**
     * 保存操作日志
     *
     * @param request 操作日志创建请求
     * @return 保存结果
     */
    @Override
    public ApiResult<Boolean> saveLogOperation(LogOperationCreateRequestDTO request) {
        logOperationService.saveLogOperation(request);
        return ApiResult.success(true);
    }
}
