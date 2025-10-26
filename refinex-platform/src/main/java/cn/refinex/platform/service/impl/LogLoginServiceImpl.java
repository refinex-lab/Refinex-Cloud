package cn.refinex.platform.service.impl;

import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.utils.Fn;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.controller.logger.dto.request.LogLoginQueryRequestDTO;
import cn.refinex.platform.controller.logger.dto.response.LogLoginResponseDTO;
import cn.refinex.platform.controller.logger.dto.response.LogLoginStatisticsResponseDTO;
import cn.refinex.platform.entity.log.LogLogin;
import cn.refinex.platform.repository.log.LogLoginRepository;
import cn.refinex.platform.service.LogLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 登录日志服务实现类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogLoginServiceImpl implements LogLoginService {

    private final LogLoginRepository logLoginRepository;

    /**
     * 保存登录日志
     *
     * @param logLogin 登录日志
     */
    @Override
    public void saveLogLogin(LogLogin logLogin) {
        logLoginRepository.saveLogLogin(logLogin);
    }

    /**
     * 异步保存登录日志
     *
     * @param logLogin 登录日志
     */
    @Async
    @Override
    public void saveLogLoginAsync(LogLogin logLogin) {
        try {
            logLoginRepository.saveLogLogin(logLogin);
        } catch (Exception e) {
            log.error("异步保存登录日志失败", e);
        }
    }

    /**
     * 根据ID获取登录日志
     *
     * @param id 日志ID
     * @return 登录日志详情
     */
    @Override
    public LogLoginResponseDTO getById(Long id) {
        LogLogin logLogin = logLoginRepository.getById(id);
        return BeanConverter.toBean(logLogin, LogLoginResponseDTO.class);
    }

    /**
     * 分页查询登录日志
     *
     * @param queryRequest 查询条件
     * @param pageRequest  分页请求
     * @return 分页结果
     */
    @Override
    public PageResult<LogLoginResponseDTO> pageQuery(LogLoginQueryRequestDTO queryRequest, PageRequest pageRequest) {
        PageResult<LogLogin> pageResult = logLoginRepository.pageQuery(
                queryRequest.getUsername(),
                queryRequest.getLoginType(),
                queryRequest.getLoginStatus(),
                queryRequest.getLoginIp(),
                queryRequest.getDeviceType(),
                queryRequest.getStartTime(),
                queryRequest.getEndTime(),
                pageRequest
        );

        // 转换为 DTO 列表
        List<LogLoginResponseDTO> dtoList = BeanConverter.copyToList(pageResult.getRecords(), LogLoginResponseDTO.class);
        return new PageResult<>(
                dtoList,
                pageResult.getTotal(),
                pageResult.getPageNum(),
                pageResult.getPageSize()
        );
    }

    /**
     * 获取登录日志统计信息
     *
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param username   用户名（可选）
     * @param loginType  登录方式（可选）
     * @param deviceType 设备类型（可选）
     * @return 统计信息
     */
    @Override
    public LogLoginStatisticsResponseDTO getStatistics(LocalDateTime startTime, LocalDateTime endTime, String username, String loginType, String deviceType) {
        Map<String, Object> statistics = logLoginRepository.getBasicStatistics(startTime, endTime, username, loginType, deviceType);

        Long totalCount = Fn.getLong(statistics.get("total_count"), 0L);
        Long successCount = Fn.getLong(statistics.get("success_count"), 0L);
        Long failureCount = Fn.getLong(statistics.get("failure_count"), 0L);
        Long uniqueUserCount = Fn.getLong(statistics.get("unique_user_count"), 0L);
        Long uniqueIpCount = Fn.getLong(statistics.get("unique_ip_count"), 0L);

        // 计算成功率
        double successRate = totalCount > 0 ? (successCount * 100.0 / totalCount) : 0.0;

        return LogLoginStatisticsResponseDTO.builder()
                .totalCount(totalCount)
                .successCount(successCount)
                .failureCount(failureCount)
                .successRate(Math.round(successRate * 100.0) / 100.0) // 保留两位小数
                .uniqueUserCount(uniqueUserCount)
                .uniqueIpCount(uniqueIpCount)
                .build();
    }

    /**
     * 按登录方式分组统计
     *
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param username   用户名（可选）
     * @param deviceType 设备类型（可选）
     * @return 分组统计结果
     */
    @Override
    public List<Map<String, Object>> getStatisticsByLoginType(LocalDateTime startTime, LocalDateTime endTime, String username, String deviceType) {
        return logLoginRepository.getStatisticsByLoginType(startTime, endTime, username, deviceType);
    }

    /**
     * 按设备类型分组统计
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param username  用户名（可选）
     * @param loginType 登录方式（可选）
     * @return 分组统计结果
     */
    @Override
    public List<Map<String, Object>> getStatisticsByDeviceType(LocalDateTime startTime, LocalDateTime endTime, String username, String loginType) {
        return logLoginRepository.getStatisticsByDeviceType(startTime, endTime, username, loginType);
    }

    /**
     * 获取趋势数据（按天）
     *
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param username   用户名（可选）
     * @param loginType  登录方式（可选）
     * @param deviceType 设备类型（可选）
     * @return 趋势数据点列表
     */
    @Override
    public List<Map<String, Object>> getTrendDataByDay(LocalDateTime startTime, LocalDateTime endTime, String username, String loginType, String deviceType) {
        return logLoginRepository.getTrendDataByDay(startTime, endTime, username, loginType, deviceType);
    }
}

