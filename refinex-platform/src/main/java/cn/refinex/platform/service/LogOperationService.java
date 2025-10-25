package cn.refinex.platform.service;

import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.platform.controller.logger.dto.request.LogOperationCreateRequestDTO;
import cn.refinex.platform.controller.logger.dto.request.LogOperationQueryRequestDTO;
import cn.refinex.platform.controller.logger.dto.response.LogOperationResponseDTO;
import cn.refinex.platform.controller.logger.dto.response.LogOperationStatisticsDTO;

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

    /**
     * 根据ID获取操作日志
     *
     * @param id 日志ID
     * @return 操作日志详情
     */
    LogOperationResponseDTO getById(Long id);

    /**
     * 分页查询操作日志
     *
     * @param queryRequest 查询条件
     * @param pageRequest  分页请求
     * @return 分页结果
     */
    PageResult<LogOperationResponseDTO> pageQuery(LogOperationQueryRequestDTO queryRequest, PageRequest pageRequest);

    /**
     * 获取操作日志统计信息
     *
     * @param queryRequest 查询条件
     * @return 统计信息
     */
    LogOperationStatisticsDTO getStatistics(LogOperationQueryRequestDTO queryRequest);

    /**
     * 根据用户ID获取操作日志
     *
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<LogOperationResponseDTO> getByUserId(Long userId, PageRequest pageRequest);

    /**
     * 根据操作模块获取操作日志
     *
     * @param operationModule 操作模块
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<LogOperationResponseDTO> getByOperationModule(String operationModule, PageRequest pageRequest);
}
