package cn.refinex.platform.controller.role.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新角色请求 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "更新角色请求")
public class RoleUpdateRequestDTO {

    @Schema(description = "角色名称", example = "部门经理", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @Schema(description = "角色类型：0前台角色,1后台角色", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "角色类型不能为空")
    private Integer roleType;

    @Schema(description = "数据权限范围：1所有数据权限,2自定义数据权限,3仅本人数据权限", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据权限范围不能为空")
    private Integer dataScope;

    @Schema(description = "排序值（数字越小越靠前）", example = "1")
    private Integer sort;

    @Schema(description = "备注说明", example = "负责部门管理")
    private String remark;

    @Schema(description = "状态：1正常,0停用", example = "1")
    private Integer status;
}

