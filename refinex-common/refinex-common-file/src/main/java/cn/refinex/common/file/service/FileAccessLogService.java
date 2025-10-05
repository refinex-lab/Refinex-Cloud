package cn.refinex.common.file.service;

import cn.refinex.common.file.domain.entity.FileAccessLog;
import cn.refinex.common.file.repository.FileAccessLogRepository;
import cn.refinex.common.utils.algorithm.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 文件访问日志服务
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileAccessLogService {

    private final FileAccessLogRepository accessLogRepository;
    private final SnowflakeIdGenerator idGenerator;

    /**
     * 记录文件访问日志（异步）
     *
     * @param fileId        文件 ID
     * @param userId        用户 ID
     * @param accessIp      访问 IP
     * @param userAgent     用户代理
     * @param referer       来源页面
     * @param accessResult  访问结果（0成功/1鉴权失败/2文件不存在）
     * @param trafficBytes  流量消耗字节数
     */
    @Async
    public void logAccess(Long fileId, Long userId, String accessIp, String userAgent, String referer, Integer accessResult, Long trafficBytes) {
        try {
            FileAccessLog accessLog = new FileAccessLog();
            accessLog.setId(idGenerator.nextId());
            accessLog.setFileId(fileId);
            accessLog.setUserId(userId);
            accessLog.setAccessIp(accessIp);
            accessLog.setUserAgent(userAgent);
            accessLog.setReferer(referer);
            accessLog.setAccessResult(accessResult);
            accessLog.setTrafficBytes(trafficBytes);
            accessLog.setAccessTime(LocalDateTime.now());
            accessLog.setCreateTime(LocalDateTime.now());

            accessLogRepository.insert(accessLog);
            log.debug("文件访问日志已记录，fileId={}, userId={}, result={}", fileId, userId, accessResult);

        } catch (Exception e) {
            log.error("文件访问日志记录失败", e);
            // 不抛出异常，避免影响主流程
        }
    }
}

