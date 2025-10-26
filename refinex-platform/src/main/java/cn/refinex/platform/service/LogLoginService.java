package cn.refinex.platform.service;

import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.platform.controller.logger.dto.request.LogLoginQueryRequestDTO;
import cn.refinex.platform.controller.logger.dto.response.LogLoginResponseDTO;
import cn.refinex.platform.controller.logger.dto.response.LogLoginStatisticsResponseDTO;
import cn.refinex.platform.entity.log.LogLogin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 登录日志服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface LogLoginService {

    /**
     * 保存登录日志
     *
     * @param logLogin 登录日志
     */
    void saveLogLogin(LogLogin logLogin);

    /**
     * 异步保存登录日志
     *
     * @param logLogin 登录日志
     */
    void saveLogLoginAsync(LogLogin logLogin);

    /**
     * 根据ID获取登录日志
     *
     * @param id 日志ID
     * @return 登录日志详情
     */
    LogLoginResponseDTO getById(Long id);

    /**
     * 分页查询登录日志
     *
     * @param queryRequest 查询条件
     * @param pageRequest  分页请求
     * @return 分页结果
     */
    PageResult<LogLoginResponseDTO> pageQuery(LogLoginQueryRequestDTO queryRequest, PageRequest pageRequest);

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
    LogLoginStatisticsResponseDTO getStatistics(LocalDateTime startTime, LocalDateTime endTime, String username, String loginType, String deviceType);

    /**
     * 按登录方式分组统计
     *
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param username   用户名（可选）
     * @param deviceType 设备类型（可选）
     * @return 分组统计结果
     */
    List<Map<String, Object>> getStatisticsByLoginType(LocalDateTime startTime, LocalDateTime endTime, String username, String deviceType);

    /**
     * 按设备类型分组统计
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param username  用户名（可选）
     * @param loginType 登录方式（可选）
     * @return 分组统计结果
     */
    List<Map<String, Object>> getStatisticsByDeviceType(LocalDateTime startTime, LocalDateTime endTime, String username, String loginType);

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
    List<Map<String, Object>> getTrendDataByDay(LocalDateTime startTime, LocalDateTime endTime, String username, String loginType, String deviceType);
}

