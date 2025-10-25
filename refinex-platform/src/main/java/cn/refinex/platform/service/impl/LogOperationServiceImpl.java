package cn.refinex.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.controller.logger.dto.request.LogOperationCreateRequestDTO;
import cn.refinex.platform.controller.logger.dto.request.LogOperationQueryRequestDTO;
import cn.refinex.platform.controller.logger.dto.response.LogOperationResponseDTO;
import cn.refinex.platform.controller.logger.dto.response.LogOperationStatisticsDTO;
import cn.refinex.platform.entity.log.LogOperation;
import cn.refinex.platform.repository.log.LogOperationRepository;
import cn.refinex.platform.service.LogOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 操作日志服务
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogOperationServiceImpl implements LogOperationService {

    private final LogOperationRepository logOperationRepository;

    /**
     * 保存操作日志
     *
     * @param request 操作日志创建请求
     */
    @Override
    public void saveLogOperation(LogOperationCreateRequestDTO request) {
        LogOperation logOperation = BeanConverter.toBean(request, LogOperation.class);
        logOperation.setRequestParams(StrUtil.maxLength(request.getRequestParams(), LogOperation.REQUEST_PARAM_MAX_LENGTH));
        logOperation.setResponseResult(StrUtil.maxLength(request.getResponseResult(), LogOperation.RESPONSE_RESULT_MAX_LENGTH));
        logOperationRepository.saveLogOperation(logOperation);
    }

    /**
     * 异步保存操作日志
     *
     * @param request 操作日志创建请求
     */
    @Override
    public void saveLogOperationAsync(LogOperationCreateRequestDTO request) {
        saveLogOperation(request);
    }

    /**
     * 根据ID获取操作日志
     *
     * @param id 日志ID
     * @return 操作日志详情
     */
    @Override
    public LogOperationResponseDTO getById(Long id) {
        LogOperation logOperation = logOperationRepository.getById(id);
        if (logOperation == null) {
            return null;
        }
        return BeanConverter.toBean(logOperation, LogOperationResponseDTO.class);
    }

    /**
     * 分页查询操作日志
     *
     * @param queryRequest 查询条件
     * @param pageRequest  分页请求
     * @return 分页结果
     */
    @Override
    public PageResult<LogOperationResponseDTO> pageQuery(LogOperationQueryRequestDTO queryRequest, PageRequest pageRequest) {
        PageResult<LogOperation> pageResult = logOperationRepository.pageQuery(queryRequest, pageRequest);
        List<LogOperationResponseDTO> responseList = BeanConverter.copyToList(pageResult.getRecords(), LogOperationResponseDTO.class);
        return new PageResult<>(responseList, pageResult.getTotal(), pageResult.getPageNum(), pageResult.getPageSize());
    }

    /**
     * 获取操作日志统计信息
     *
     * @param queryRequest 查询条件
     * @return 统计信息
     */
    @Override
    public LogOperationStatisticsDTO getStatistics(LogOperationQueryRequestDTO queryRequest) {
        LogOperationStatisticsDTO statistics = logOperationRepository.getStatistics(queryRequest);

        // 计算时间维度的统计数据
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        // 今日统计
        LogOperationQueryRequestDTO todayQuery = BeanConverter.toBean(queryRequest, LogOperationQueryRequestDTO.class);
        todayQuery.setStartTime(today.atStartOfDay());
        todayQuery.setEndTime(today.atTime(LocalTime.MAX));
        Long todayCount = logOperationRepository.countByCondition(todayQuery);

        // 本周统计
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1L);
        LogOperationQueryRequestDTO weekQuery = BeanConverter.toBean(queryRequest, LogOperationQueryRequestDTO.class);
        weekQuery.setStartTime(weekStart.atStartOfDay());
        weekQuery.setEndTime(today.atTime(LocalTime.MAX));
        Long weekCount = logOperationRepository.countByCondition(weekQuery);

        // 本月统计
        LocalDate monthStart = today.withDayOfMonth(1);
        LogOperationQueryRequestDTO monthQuery = BeanConverter.toBean(queryRequest, LogOperationQueryRequestDTO.class);
        monthQuery.setStartTime(monthStart.atStartOfDay());
        monthQuery.setEndTime(today.atTime(LocalTime.MAX));
        Long monthCount = logOperationRepository.countByCondition(monthQuery);

        return LogOperationStatisticsDTO.builder()
                .totalCount(statistics.getTotalCount())
                .successCount(statistics.getSuccessCount())
                .failureCount(statistics.getFailureCount())
                .successRate(statistics.getSuccessRate())
                .avgExecutionTime(statistics.getAvgExecutionTime())
                .maxExecutionTime(statistics.getMaxExecutionTime())
                .minExecutionTime(statistics.getMinExecutionTime())
                .todayCount(todayCount)
                .weekCount(weekCount)
                .monthCount(monthCount)
                .build();
    }

    /**
     * 根据用户ID获取操作日志
     *
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    @Override
    public PageResult<LogOperationResponseDTO> getByUserId(Long userId, PageRequest pageRequest) {
        LogOperationQueryRequestDTO queryRequest = LogOperationQueryRequestDTO.builder()
                .userId(userId)
                .build();
        return pageQuery(queryRequest, pageRequest);
    }

    /**
     * 根据操作模块获取操作日志
     *
     * @param operationModule 操作模块
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    @Override
    public PageResult<LogOperationResponseDTO> getByOperationModule(String operationModule, PageRequest pageRequest) {
        LogOperationQueryRequestDTO queryRequest = LogOperationQueryRequestDTO.builder()
                .operationModule(operationModule)
                .build();
        return pageQuery(queryRequest, pageRequest);
    }
}
