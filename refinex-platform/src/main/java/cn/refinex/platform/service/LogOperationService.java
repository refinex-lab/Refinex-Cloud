package cn.refinex.platform.service;

import cn.refinex.api.platform.client.logger.dto.request.LogOperationCreateRequestDTO;

/**
 * 日志操作服务
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface LogOperationService {

    /**
     * 保存操作日志
     *
     * @param request 操作日志创建请求
     */
    void saveLogOperation(LogOperationCreateRequestDTO request);
}
