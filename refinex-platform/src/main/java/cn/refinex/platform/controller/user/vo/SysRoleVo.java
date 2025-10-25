package cn.refinex.platform.controller.user.vo;

import cn.refinex.common.constants.SystemRoleConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统角色 VO
 *
 * @author Michelle.Chung
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "系统角色 VO")
public class SysRoleVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "角色编码", example = "ROLE_ADMIN")
    private String roleCode;

    @Schema(description = "角色名称", example = "管理员")
    private String roleName;

    @Schema(description = "角色类型：0前台角色,1后台角色", example = "1")
    private Integer roleType;

    @Schema(description = "角色权限范围(1:所有数据权限 2:自定义数据权限 3:仅本人数据权限)", example = "1")
    private Integer dataScope;

    @Schema(description = "是否系统内置角色：0否,1是", example = "1")
    private Integer isBuiltin;

    @Schema(description = "创建时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "备注说明", example = "系统管理员角色")
    private String remark;

    @Schema(description = "排序字段", example = "0")
    private Integer sort;

    @Schema(description = "状态：0正常,1停用", example = "0")
    private Integer status;

    @Schema(description = "扩展数据（JSON格式）")
    private String extraData;

    /**
     * 是否是超级管理员角色
     *
     * @return 是否是超级管理员角色
     */
    public boolean isSuperAdmin() {
        return SystemRoleConstants.SUPER_ADMIN_ID.equals(this.id);
    }
}
