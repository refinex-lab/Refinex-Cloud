package cn.refinex.platform.controller.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户查询请求 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "用户查询请求")
public class UserQueryRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名（模糊搜索）")
    private String username;

    @Schema(description = "手机号（模糊搜索）")
    private String mobile;

    @Schema(description = "邮箱（模糊搜索）")
    private String email;

    @Schema(description = "昵称（模糊搜索）")
    private String nickname;

    @Schema(description = "用户状态：0待激活，1正常，2冻结，3注销")
    private Integer userStatus;

    @Schema(description = "用户类型：sys_user后台用户，app_user移动端用户")
    private String userType;

    @Schema(description = "注册来源：web,ios,android,h5")
    private String registerSource;

    @Schema(description = "状态：0正常，1停用")
    private Integer status;

    @Schema(description = "当前页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;
}

