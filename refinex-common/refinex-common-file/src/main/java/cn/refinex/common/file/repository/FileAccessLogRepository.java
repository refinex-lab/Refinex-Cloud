package cn.refinex.common.file.repository;

import cn.refinex.common.file.domain.entity.FileAccessLog;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件访问日志 Repository
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class FileAccessLogRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 插入访问日志
     *
     * @param accessLog 访问日志
     * @return 影响行数
     */
    public int insert(FileAccessLog accessLog) {
        String sql = """
            INSERT INTO file_access_log (
                id, file_id, user_id, access_ip, access_region, user_agent, referer,
                access_result, traffic_bytes, access_time, create_time
            ) VALUES (
                :id, :fileId, :userId, :accessIp, :accessRegion, :userAgent, :referer,
                :accessResult, :trafficBytes, :accessTime, :createTime
            )
            """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", accessLog.getId());
        params.put("fileId", accessLog.getFileId());
        params.put("userId", accessLog.getUserId());
        params.put("accessIp", accessLog.getAccessIp());
        params.put("accessRegion", accessLog.getAccessRegion());
        params.put("userAgent", accessLog.getUserAgent());
        params.put("referer", accessLog.getReferer());
        params.put("accessResult", accessLog.getAccessResult());
        params.put("trafficBytes", accessLog.getTrafficBytes());
        params.put("accessTime", accessLog.getAccessTime());
        params.put("createTime", accessLog.getCreateTime());

        return jdbcManager.insert(sql, params);
    }
}

