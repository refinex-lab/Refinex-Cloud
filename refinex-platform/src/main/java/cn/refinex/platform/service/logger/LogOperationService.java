package cn.refinex.platform.service.logger;

import cn.hutool.core.util.StrUtil;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.client.logger.dto.request.LogOperationCreateRequest;
import cn.refinex.platform.domain.entity.log.LogOperation;
import cn.refinex.platform.repository.log.LogOperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogOperationService {

    private final LogOperationRepository logOperationRepository;

    /**
     * 保存操作日志
     *
     * @param request 操作日志创建请求
     */
    public void saveLogOperation(LogOperationCreateRequest request) {
        LogOperation logOperation = BeanConverter.toBean(request, LogOperation.class);
        logOperation.setRequestParams(StrUtil.maxLength(request.getRequestParams(), LogOperation.REQUEST_PARAM_MAX_LENGTH));
        logOperation.setResponseResult(StrUtil.maxLength(request.getResponseResult(), LogOperation.RESPONSE_RESULT_MAX_LENGTH));
        logOperationRepository.saveLogOperation(logOperation);
    }
}
