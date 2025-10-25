package cn.refinex.platform.service;

import cn.refinex.platform.controller.logger.dto.request.LogOperationCreateRequestDTO;

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

    /**
     * 异步保存操作日志
     *
     * @param request 操作日志创建请求
     */
    void saveLogOperationAsync(LogOperationCreateRequestDTO request);
}
