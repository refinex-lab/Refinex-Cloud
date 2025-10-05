package cn.refinex.common.mail.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 邮箱验证码实体
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
public class EmailVerifyCode {

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 验证码
     */
    private String verifyCode;

    /**
     * 业务类型（REGISTER、LOGIN、RESET_PASSWORD、CHANGE_EMAIL）
     */
    private String codeType;

    /**
     * 验证场景补充说明
     */
    private String verifyScene;

    /**
     * 是否已使用（0否、1是）
     */
    private Integer isUsed;

    /**
     * 使用时间
     */
    private LocalDateTime useTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 客户端 IP
     */
    private String clientIp;

    /**
     * 状态（0未使用、1已使用、2已过期、3已失效）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

