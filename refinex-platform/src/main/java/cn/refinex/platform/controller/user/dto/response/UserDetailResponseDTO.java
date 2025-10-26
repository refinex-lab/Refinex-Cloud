package cn.refinex.platform.controller.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户详情响应 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "用户详情响应")
public class UserDetailResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "登录用户名")
    private String username;

    @Schema(description = "脱敏手机号")
    private String mobile;

    @Schema(description = "脱敏邮箱")
    private String email;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "性别：male,female,other")
    private String sex;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "用户状态：0待激活，1正常，2冻结，3注销")
    private Integer userStatus;

    @Schema(description = "用户类型：sys_user后台用户，app_user移动端用户")
    private String userType;

    @Schema(description = "注册来源：web,ios,android,h5")
    private String registerSource;

    @Schema(description = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    @Schema(description = "最后登录IP")
    private String lastLoginIp;

    @Schema(description = "创建人ID")
    private Long createBy;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID")
    private Long updateBy;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "状态：0正常，1停用")
    private Integer status;

    @Schema(description = "备注说明")
    private String remark;

    @Schema(description = "排序字段")
    private Integer sort;

    @Schema(description = "扩展数据")
    private String extraData;
}

