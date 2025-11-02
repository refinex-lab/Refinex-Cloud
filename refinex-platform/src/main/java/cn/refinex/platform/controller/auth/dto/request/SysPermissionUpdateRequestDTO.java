package cn.refinex.platform.controller.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 权限更新请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "权限更新请求")
public class SysPermissionUpdateRequestDTO {

    @NotNull(message = "主键ID不能为空")
    @Schema(description = "主键ID", example = "1")
    private Long id;

    @NotBlank(message = "权限编码不能为空")
    @Size(max = 128, message = "权限编码长度不能超过128个字符")
    @Schema(description = "权限编码", example = "system:user:create")
    private String permissionCode;

    @NotBlank(message = "权限名称不能为空")
    @Size(max = 128, message = "权限名称长度不能超过128个字符")
    @Schema(description = "权限名称", example = "创建用户")
    private String permissionName;

    @NotBlank(message = "权限类型不能为空")
    @Size(max = 16, message = "权限类型长度不能超过16个字符")
    @Schema(description = "权限类型：menu,button,api", example = "api")
    private String permissionType;

    @NotNull(message = "父权限ID不能为空")
    @Schema(description = "父权限ID，根为0", example = "0")
    private Long parentId;

    @Size(max = 64, message = "模块名称长度不能超过64个字符")
    @Schema(description = "模块名称", example = "system")
    private String moduleName;

    @Size(max = 255, message = "资源路径或API路径长度不能超过255个字符")
    @Schema(description = "资源路径或API路径", example = "/api/system/user")
    private String resourcePath;

    @Size(max = 16, message = "HTTP方法长度不能超过16个字符")
    @Schema(description = "HTTP方法：GET,POST,PUT,DELETE,*", example = "POST")
    private String httpMethod;

    @Schema(description = "排序", example = "0")
    private Integer sort;

    @Schema(description = "状态：1正常,0停用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "扩展数据（JSON）")
    private String extraData;
}


