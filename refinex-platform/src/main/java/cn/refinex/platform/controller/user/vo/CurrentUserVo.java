package cn.refinex.platform.controller.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 当前登录用户对外返回的安全视图对象（不包含敏感字段）
 * 
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "当前登录用户信息（安全字段）")
public class CurrentUserVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "性别(male:男,female:女,other:其他)")
    private String sex;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "脱敏邮箱")
    private String email;

    @Schema(description = "脱敏手机号")
    private String mobile;

    @Schema(description = "用户状态(0待激活,1正常,2冻结,3注销)")
    private Integer userStatus;

    @Schema(description = "用户类型(sys_user:后台用户, app_user:移动端用户)")
    private String userType;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "最后登录IP")
    private String lastLoginIp;

    @Schema(description = "角色编码列表")
    private List<String> roles;

    @Schema(description = "权限编码列表")
    private List<String> permissions;
}


