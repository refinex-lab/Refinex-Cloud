package cn.refinex.common.domain.model;

import cn.refinex.common.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 登录用户对象
 *
 * @author ruoyi
 * @author Refinex
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@Schema(description = "登录用户对象")
public class LoginUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户状态(0待激活,1正常,2冻结,3注销)")
    private Integer userStatus;

    @Schema(description = "用户唯一标识")
    private String token;

    @Schema(description = "用户账户")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "用户类型(sys_user:后台用户, app_user:移动端用户)")
    private String userType;

    @Schema(description = "登录时间")
    private Long loginTime;

    @Schema(description = "过期时间")
    private Long expireTime;

    @Schema(description = "登录IP地址")
    private String ipaddr;

    @Schema(description = "登录地点")
    private String loginLocation;

    @Schema(description = "浏览器类型")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "客户端唯一标识")
    private String clientKey;

    @Schema(description = "设备类型")
    private String deviceType;

    @Schema(description = "菜单权限列表")
    private Set<String> menuPermission;

    @Schema(description = "角色权限列表")
    private Set<String> rolePermission;

    @Schema(description = "角色列表")
    private List<SysRoleDTO> roles;

    @Schema(description = "当前角色ID")
    private Long roleId;

    /**
     * 获取登录ID，格式为：userType:userId
     *
     * @return 登录ID
     */
    public String getLoginId() {
        if (StringUtils.isBlank(userType)) {
            throw new IllegalArgumentException("用户类型不能为空");
        }
        if (Objects.isNull(userId)) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 拼接用户类型和用户ID，作为登录用户ID
        return userType + ":" + userId;
    }

    /**
     * 校验用户状态是否正常
     *
     * @throws IllegalArgumentException 如果用户状态异常
     */
    public void validateUserStatus() {
        if (Objects.equals(userStatus, UserStatus.PENDING_ACTIVATION.getValue())) {
            throw new IllegalArgumentException("用户状态为待激活，无法登录");
        }
        if (Objects.equals(userStatus, UserStatus.FROZEN.getValue())) {
            throw new IllegalArgumentException("用户状态为冻结，无法登录");
        }
        if (Objects.equals(userStatus, UserStatus.LOGGED_OUT.getValue())) {
            throw new IllegalArgumentException("用户状态为注销，无法登录");
        }
    }

}
