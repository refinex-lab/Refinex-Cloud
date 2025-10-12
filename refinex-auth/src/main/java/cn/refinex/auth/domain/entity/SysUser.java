package cn.refinex.auth.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * <p>
 * 对应数据库表：sys_user
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUser {

    /**
     * 用户 ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（BCrypt 加密）
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像 URL
     */
    private String avatar;

    /**
     * 用户状态：0待激活,1正常,2冻结,3注销
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录 IP
     */
    private String lastLoginIp;
}

