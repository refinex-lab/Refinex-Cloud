package cn.refinex.platform.entity.sys;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "用户实体")
public class SysUser {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "登录用户名", example = "admin")
    private String username;

    @Schema(description = "脱敏手机号", example = "138****1234")
    private String mobile;

    @Schema(description = "脱敏邮箱", example = "abc***@gmail.com")
    private String email;

    @Schema(description = "BCrypt加密后的密码哈希")
    private String password;

    @Schema(description = "用户昵称", example = "管理员")
    private String nickname;

    @Schema(description = "性别：male,female,other", example = "male")
    private String sex;

    @Schema(description = "用户类型(sys_user:后台用户, app_user:移动端用户)")
    private String userType;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "用户状态：0待激活,1正常,2冻结,3注销", example = "1")
    private Integer userStatus;

    @Schema(description = "注册来源：web,ios,android,h5", example = "web")
    private String registerSource;

    @Schema(description = "最后登录时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    @Schema(description = "最后登录IP", example = "192.168.1.100")
    private String lastLoginIp;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1")
    private Long updateBy;

    @Schema(description = "更新时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除标记：0未删除,1已删除", example = "0")
    private Integer deleted;

    @Schema(description = "乐观锁版本号", example = "0")
    private Integer version;

    @Schema(description = "备注说明", example = "系统管理员账号")
    private String remark;

    @Schema(description = "排序字段", example = "0")
    private Integer sort;

    @Schema(description = "状态：0正常,1停用", example = "0")
    private Integer status;

    @Schema(description = "扩展数据（JSON格式）")
    private String extraData;
}

