package cn.refinex.platform.controller.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统权限分页查询条件
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "系统权限分页查询条件")
public class SysPermissionQueryRequestDTO {

    @Schema(description = "权限编码，支持模糊")
    private String permissionCode;

    @Schema(description = "权限名称，支持模糊")
    private String permissionName;

    @Schema(description = "权限类型：menu,button,api")
    private String permissionType;

    @Schema(description = "所属模块名称，支持模糊")
    private String moduleName;

    @Schema(description = "父权限ID")
    private Long parentId;

    @Schema(description = "状态：0正常,1停用")
    private Integer status;
}


