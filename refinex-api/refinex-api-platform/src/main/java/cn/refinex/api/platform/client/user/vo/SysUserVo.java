package cn.refinex.api.platform.client.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统用户 VO
 *
 * @author Michelle.Chung
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "系统用户 VO")
public class SysUserVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "用户状态(0待激活,1正常,2冻结,3注销)")
    private Integer userStatus;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "用户类型(sys_user:后台用户, app_user:移动端用户)")
    private String userType;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "性别(male:男, female:女, other:其他)")
    private String sex;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "最后登录 IP")
    private String lastLoginIp;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "角色列表")
    private List<SysRoleVo> roles;

    @Schema(description = "角色 ID 列表")
    private Long[] roleIds;

    @Schema(description = "当前角色 ID")
    private Long roleId;
}
