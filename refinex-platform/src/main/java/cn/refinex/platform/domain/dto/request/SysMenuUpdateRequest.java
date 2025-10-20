package cn.refinex.platform.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 菜单更新请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "菜单更新请求")
public class SysMenuUpdateRequest {

    @NotNull(message = "主键ID不能为空")
    @Schema(description = "主键ID", example = "1")
    private Long id;

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 100, message = "菜单名称长度不能超过100个字符")
    @Schema(description = "菜单名称", example = "系统管理")
    private String menuName;

    @NotNull(message = "父菜单ID不能为空")
    @Schema(description = "父菜单ID，根为0", example = "0")
    private Long parentId;

    @NotBlank(message = "菜单类型不能为空")
    @Size(max = 10, message = "菜单类型长度不能超过10个字符")
    @Schema(description = "菜单类型：M目录,C菜单,F按钮", example = "M")
    private String menuType;

    @Size(max = 255, message = "路由路径长度不能超过255个字符")
    @Schema(description = "路由路径", example = "/system")
    private String routePath;

    @Size(max = 255, message = "组件路径长度不能超过255个字符")
    @Schema(description = "组件路径", example = "system/index")
    private String componentPath;

    @Size(max = 64, message = "菜单图标长度不能超过64个字符")
    @Schema(description = "菜单图标", example = "setting")
    private String menuIcon;

    @Schema(description = "是否外链：0否,1是", example = "0")
    private Integer isExternal;

    @Schema(description = "是否缓存：0否,1是", example = "0")
    private Integer isCached;

    @Schema(description = "是否可见：0隐藏,1显示", example = "1")
    private Integer isVisible;

    @Schema(description = "排序", example = "0")
    private Integer sort;

    @Schema(description = "状态：0正常,1停用", example = "0")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "扩展数据（JSON）")
    private String extraData;
}


