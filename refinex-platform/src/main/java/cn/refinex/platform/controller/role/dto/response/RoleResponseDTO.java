package cn.refinex.platform.controller.role.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色响应 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "角色响应")
public class RoleResponseDTO {

    @Schema(description = "角色ID", example = "1")
    private Long id;

    @Schema(description = "角色编码", example = "ROLE_MANAGER")
    private String roleCode;

    @Schema(description = "角色名称", example = "部门经理")
    private String roleName;

    @Schema(description = "角色类型：0前台角色,1后台角色", example = "1")
    private Integer roleType;

    @Schema(description = "角色类型标签", example = "后台角色")
    private String roleTypeLabel;

    @Schema(description = "数据权限范围：1所有数据权限,2自定义数据权限,3仅本人数据权限", example = "3")
    private Integer dataScope;

    @Schema(description = "数据权限范围标签", example = "仅本人数据权限")
    private String dataScopeLabel;

    @Schema(description = "是否系统内置角色：0否,1是", example = "0")
    private Integer isBuiltin;

    @Schema(description = "是否系统内置角色标签", example = "否")
    private String isBuiltinLabel;

    @Schema(description = "排序值", example = "1")
    private Integer sort;

    @Schema(description = "备注说明", example = "负责部门管理")
    private String remark;

    @Schema(description = "状态：0正常,1停用", example = "0")
    private Integer status;

    @Schema(description = "状态标签", example = "正常")
    private String statusLabel;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间", example = "2025-10-26 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1")
    private Long updateBy;

    @Schema(description = "更新时间", example = "2025-10-26 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

