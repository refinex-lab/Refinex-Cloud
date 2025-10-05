package cn.refinex.common.file.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件访问日志实体
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
public class FileAccessLog {

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 文件 ID
     */
    private Long fileId;

    /**
     * 访问者 ID
     */
    private Long userId;

    /**
     * 访问 IP
     */
    private String accessIp;

    /**
     * 访问地区
     */
    private String accessRegion;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 来源页面
     */
    private String referer;

    /**
     * 访问结果（0成功/1鉴权失败/2文件不存在）
     */
    private Integer accessResult;

    /**
     * 流量消耗字节数
     */
    private Long trafficBytes;

    /**
     * 访问时间
     */
    private LocalDateTime accessTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

