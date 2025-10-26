package cn.refinex.platform.service;

import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.platform.controller.logger.dto.request.LogOperationCreateRequestDTO;
import cn.refinex.platform.controller.logger.dto.request.LogOperationQueryRequestDTO;
import cn.refinex.platform.controller.logger.dto.request.LogOperationStatisticsRequestDTO;
import cn.refinex.platform.controller.logger.dto.request.LogOperationTrendRequestDTO;
import cn.refinex.platform.controller.logger.dto.response.LogOperationResponseDTO;
import cn.refinex.platform.controller.logger.dto.response.LogOperationStatisticsResponseDTO;
import cn.refinex.platform.controller.logger.dto.response.LogOperationTrendResponseDTO;

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
     * <p>
     * 支持灵活的时间维度和分组统计
     *
     * @param request 统计请求
     * @return 统计信息
     */
    LogOperationStatisticsResponseDTO getStatistics(LogOperationStatisticsRequestDTO request);

    /**
     * 获取操作日志趋势分析
     * <p>
     * 支持按小时、天、周、月等维度分析
     *
     * @param request 趋势分析请求
     * @return 趋势分析结果
     */
    LogOperationTrendResponseDTO getTrend(LogOperationTrendRequestDTO request);
}
