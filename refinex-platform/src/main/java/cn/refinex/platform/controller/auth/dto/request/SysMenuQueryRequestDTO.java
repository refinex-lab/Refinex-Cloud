package cn.refinex.platform.controller.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统菜单分页查询条件
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "系统菜单分页查询条件")
public class SysMenuQueryRequestDTO {

    @Schema(description = "菜单名称，支持模糊")
    private String menuName;

    @Schema(description = "菜单类型：M目录,C菜单,F按钮")
    private String menuType;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "是否可见：0隐藏,1显示")
    private Integer isVisible;

    @Schema(description = "状态：1正常,0停用")
    private Integer status;
}


