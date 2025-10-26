package cn.refinex.platform.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.utils.Fn;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.controller.logger.dto.request.LogOperationCreateRequestDTO;
import cn.refinex.platform.controller.logger.dto.request.LogOperationQueryRequestDTO;
import cn.refinex.platform.controller.logger.dto.request.LogOperationStatisticsRequestDTO;
import cn.refinex.platform.controller.logger.dto.request.LogOperationTrendRequestDTO;
import cn.refinex.platform.controller.logger.dto.response.LogOperationResponseDTO;
import cn.refinex.platform.controller.logger.dto.response.LogOperationStatisticsResponseDTO;
import cn.refinex.platform.controller.logger.dto.response.LogOperationTrendResponseDTO;
import cn.refinex.platform.entity.log.LogOperation;
import cn.refinex.platform.enums.TimeDimension;
import cn.refinex.platform.enums.TrendDimension;
import cn.refinex.platform.repository.log.LogOperationRepository;
import cn.refinex.platform.service.LogOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 操作日志服务实现
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
    @Async
    @Override
    public void saveLogOperationAsync(LogOperationCreateRequestDTO request) {
        try {
            saveLogOperation(request);
        } catch (Exception e) {
            log.error("异步保存操作日志失败", e);
        }
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
     * @param request 统计请求
     * @return 统计信息
     */
    @Override
    public LogOperationStatisticsResponseDTO getStatistics(LogOperationStatisticsRequestDTO request) {
        // 1. 计算时间范围
        TimeRange timeRange = calculateTimeRange(request.getTimeDimension(), request.getStartTime(), request.getEndTime());

        // 2. 获取基础统计（一次查询完成）
        Map<String, Object> basicStatsMap = logOperationRepository.getBasicStatistics(
                timeRange.startTime(),
                timeRange.endTime(),
                request.getApplicationName(),
                request.getOperationModule(),
                request.getUsername()
        );

        LogOperationStatisticsResponseDTO.BasicStatistics basicStats = buildBasicStatistics(
                basicStatsMap,
                timeRange,
                request.getTimeDimension()
        );

        // 3. 按需获取分组统计
        List<LogOperationStatisticsResponseDTO.GroupStatistics> typeGroupStats = null;
        if (Boolean.TRUE.equals(request.getIncludeTypeGroup())) {
            typeGroupStats = getTypeGroupStatistics(timeRange, request);
        }

        List<LogOperationStatisticsResponseDTO.GroupStatistics> moduleGroupStats = null;
        if (Boolean.TRUE.equals(request.getIncludeModuleGroup())) {
            moduleGroupStats = getModuleGroupStatistics(timeRange, request);
        }

        // 4. 按需获取 Top 用户
        List<LogOperationStatisticsResponseDTO.TopUserStatistics> topUsers = null;
        if (Boolean.TRUE.equals(request.getIncludeTopUsers())) {
            topUsers = getTopUserStatistics(timeRange, request);
        }

        return LogOperationStatisticsResponseDTO.builder()
                .basicStats(basicStats)
                .typeGroupStats(typeGroupStats)
                .moduleGroupStats(moduleGroupStats)
                .topUsers(topUsers)
                .build();
    }

    /**
     * 获取操作日志趋势分析
     *
     * @param request 趋势分析请求
     * @return 趋势分析结果
     */
    @Override
    public LogOperationTrendResponseDTO getTrend(LogOperationTrendRequestDTO request) {
        List<Map<String, Object>> trendDataList;

        // 根据趋势维度调用不同的查询方法
        switch (TrendDimension.fromCode(request.getTrendDimension().toLowerCase())) {
            case HOUR -> trendDataList = logOperationRepository.getTrendDataByHour(
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getApplicationName(),
                    request.getOperationModule(),
                    request.getUsername()
            );
            case DAY -> trendDataList = logOperationRepository.getTrendDataByDay(
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getApplicationName(),
                    request.getOperationModule(),
                    request.getUsername()
            );
            case WEEK -> trendDataList = logOperationRepository.getTrendDataByWeek(
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getApplicationName(),
                    request.getOperationModule(),
                    request.getUsername()
            );
            case MONTH -> trendDataList = logOperationRepository.getTrendDataByMonth(
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getApplicationName(),
                    request.getOperationModule(),
                    request.getUsername()
            );
            case YEAR -> throw new UnsupportedOperationException("按年趋势分析暂不支持");
            default -> throw new IllegalArgumentException("不支持的趋势维度: " + request.getTrendDimension());
        }

        // 转换为响应对象
        List<LogOperationTrendResponseDTO.TrendDataPoint> trendData = trendDataList.stream()
                .map(this::buildTrendDataPoint)
                .toList();

        return LogOperationTrendResponseDTO.builder()
                .trendDimension(request.getTrendDimension())
                .trendData(trendData)
                .dataPointCount(trendData.size())
                .build();
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 计算时间范围
     * 支持按小时、天、周、月、年、自定义时间范围
     *
     * @param timeDimension 时间维度
     * @param customStart   自定义开始时间（可选）
     * @param customEnd     自定义结束时间（可选）
     * @return 时间范围对象
     */
    private TimeRange calculateTimeRange(String timeDimension, LocalDateTime customStart, LocalDateTime customEnd) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        return switch (TimeDimension.fromCode(timeDimension.toLowerCase())) {
            case TODAY -> new TimeRange(
                    today.atStartOfDay(),
                    today.atTime(LocalTime.MAX)
            );
            case WEEK -> {
                LocalDate weekStart = today.with(DayOfWeek.MONDAY);
                yield new TimeRange(
                        weekStart.atStartOfDay(),
                        today.atTime(LocalTime.MAX)
                );
            }
            case MONTH -> {
                LocalDate monthStart = today.withDayOfMonth(1);
                yield new TimeRange(
                        monthStart.atStartOfDay(),
                        today.atTime(LocalTime.MAX)
                );
            }
            case YEAR -> {
                LocalDate yearStart = today.withDayOfYear(1);
                yield new TimeRange(
                        yearStart.atStartOfDay(),
                        today.atTime(LocalTime.MAX)
                );
            }
            case CUSTOM -> {
                if (customStart == null || customEnd == null) {
                    throw new IllegalArgumentException("自定义时间维度必须提供开始时间和结束时间");
                }
                yield new TimeRange(customStart, customEnd);
            }
        };
    }

    /**
     * 构建基础统计对象
     *
     * @param statsMap      统计数据映射
     * @param timeRange     时间范围
     * @param timeDimension 时间维度
     * @return 基础统计对象
     */
    private LogOperationStatisticsResponseDTO.BasicStatistics buildBasicStatistics(Map<String, Object> statsMap, TimeRange timeRange, String timeDimension) {
        Long totalCount = Fn.getLong(statsMap.get("total_count"), 0L);
        Long successCount = Fn.getLong(statsMap.get("success_count"), 0L);
        Long failureCount = Fn.getLong(statsMap.get("failure_count"), 0L);
        Double successRate = totalCount > 0 ? Math.round(successCount * 10000.0 / totalCount) / 100.0 : 0.0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);

        return LogOperationStatisticsResponseDTO.BasicStatistics.builder()
                .totalCount(totalCount)
                .successCount(successCount)
                .failureCount(failureCount)
                .successRate(successRate)
                .avgExecutionTime(Fn.getDouble(statsMap.get("avg_execution_time"), 0.0))
                .maxExecutionTime(Fn.getInt(statsMap.get("max_execution_time"), 0))
                .minExecutionTime(Fn.getInt(statsMap.get("min_execution_time"), 0))
                .startTime(timeRange.startTime().format(formatter))
                .endTime(timeRange.endTime().format(formatter))
                .timeDimension(timeDimension)
                .build();
    }

    /**
     * 获取按操作类型分组统计
     *
     * @param timeRange 时间范围
     * @param request   统计请求
     * @return 按操作类型分组统计结果
     */
    private List<LogOperationStatisticsResponseDTO.GroupStatistics> getTypeGroupStatistics(TimeRange timeRange, LogOperationStatisticsRequestDTO request) {
        List<Map<String, Object>> groupData = logOperationRepository.getStatisticsByType(
                timeRange.startTime(),
                timeRange.endTime(),
                request.getApplicationName(),
                request.getOperationModule(),
                request.getUsername()
        );

        Long totalCount = groupData.stream()
                .mapToLong(row -> Fn.getLong(row.get("count"), 0L))
                .sum();

        return groupData.stream()
                .map(row -> {
                    Long count = Fn.getLong(row.get("count"), 0L);
                    Double percentage = totalCount > 0 ? Math.round(count * 10000.0 / totalCount) / 100.0 : 0.0;
                    return LogOperationStatisticsResponseDTO.GroupStatistics.builder()
                            .groupName(Fn.getString(row.get("group_name"), ""))
                            .count(count)
                            .percentage(percentage)
                            .build();
                })
                .toList();
    }

    /**
     * 获取按操作模块分组统计
     *
     * @param timeRange 时间范围
     * @param request   统计请求
     * @return 按操作模块分组统计结果
     */
    private List<LogOperationStatisticsResponseDTO.GroupStatistics> getModuleGroupStatistics(TimeRange timeRange, LogOperationStatisticsRequestDTO request) {
        List<Map<String, Object>> groupData = logOperationRepository.getStatisticsByModule(
                timeRange.startTime(),
                timeRange.endTime(),
                request.getApplicationName(),
                request.getUsername()
        );

        Long totalCount = groupData.stream()
                .mapToLong(row -> Fn.getLong(row.get("count"), 0L))
                .sum();

        return groupData.stream()
                .map(row -> {
                    Long count = Fn.getLong(row.get("count"), 0L);
                    Double percentage = totalCount > 0 ? Math.round(count * 10000.0 / totalCount) / 100.0 : 0.0;
                    return LogOperationStatisticsResponseDTO.GroupStatistics.builder()
                            .groupName(Fn.getString(row.get("group_name"), ""))
                            .count(count)
                            .percentage(percentage)
                            .build();
                })
                .toList();
    }

    /**
     * 获取 Top 用户统计
     *
     * @param timeRange 时间范围
     * @param request   统计请求
     * @return Top 用户统计结果
     */
    private List<LogOperationStatisticsResponseDTO.TopUserStatistics> getTopUserStatistics(TimeRange timeRange, LogOperationStatisticsRequestDTO request) {
        List<Map<String, Object>> topUserData = logOperationRepository.getTopUsers(
                timeRange.startTime(),
                timeRange.endTime(),
                request.getApplicationName(),
                request.getOperationModule(),
                request.getTopUserLimit()
        );

        return topUserData.stream()
                .map(row -> {
                    Long count = Fn.getLong(row.get("count"), 0L);
                    Long successCount = Fn.getLong(row.get("success_count"), 0L);
                    Long failureCount = Fn.getLong(row.get("failure_count"), 0L);
                    Double successRate = count > 0 ? Math.round(successCount * 10000.0 / count) / 100.0 : 0.0;

                    return LogOperationStatisticsResponseDTO.TopUserStatistics.builder()
                            .username(Fn.getString(row.get("username"), ""))
                            .count(count)
                            .successCount(successCount)
                            .failureCount(failureCount)
                            .successRate(successRate)
                            .build();
                })
                .toList();
    }

    /**
     * 构建趋势数据点
     *
     * @param row 数据库查询结果行
     * @return 趋势数据点
     */
    private LogOperationTrendResponseDTO.TrendDataPoint buildTrendDataPoint(Map<String, Object> row) {
        Long totalCount = Fn.getLong(row.get("total_count"), 0L);
        Long successCount = Fn.getLong(row.get("success_count"), 0L);
        Long failureCount = Fn.getLong(row.get("failure_count"), 0L);
        Double successRate = totalCount > 0 ? Math.round(successCount * 10000.0 / totalCount) / 100.0 : 0.0;

        return LogOperationTrendResponseDTO.TrendDataPoint.builder()
                .timePoint(Fn.getString(row.get("time_point"), ""))
                .totalCount(totalCount)
                .successCount(successCount)
                .failureCount(failureCount)
                .successRate(successRate)
                .avgExecutionTime(Fn.getDouble(row.get("avg_execution_time"), 0.0))
                .build();
    }

    /**
     * 时间范围记录
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    private record TimeRange(LocalDateTime startTime, LocalDateTime endTime) {
    }
}
