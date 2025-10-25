package cn.refinex.platform.entity.sys;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色实体类
 * <p>
 * 对应数据库表：sys_role
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "角色实体")
public class SysRole {

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

    @Schema(description = "备注说明", example = "系统管理员角色")
    private String remark;

    @Schema(description = "排序字段", example = "0")
    private Integer sort;

    @Schema(description = "状态：0正常,1停用", example = "0")
    private Integer status;

    @Schema(description = "扩展数据（JSON格式）")
    private String extraData;
}
