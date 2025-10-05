package cn.refinex.auth.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 登录日志实体类
 * <p>
 * 对应数据库表：log_login
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogLogin {

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 登录方式：PASSWORD, VERIFY_CODE, WECHAT
     */
    private String loginType;

    /**
     * 登录 IP
     */
    private String loginIp;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器信息
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 设备类型：PC, Mobile, Tablet
     */
    private String deviceType;

    /**
     * 登录状态：0 成功, 1 失败
     */
    private Integer loginStatus;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 登录时间
     */
    private LocalDateTime createTime;
}

