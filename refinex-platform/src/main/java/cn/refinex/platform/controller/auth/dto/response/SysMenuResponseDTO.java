package cn.refinex.platform.controller.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统菜单 响应对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统菜单响应")
public class SysMenuResponseDTO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "菜单类型：M目录,C菜单,F按钮")
    private String menuType;

    @Schema(description = "路由路径")
    private String routePath;

    @Schema(description = "组件路径")
    private String componentPath;

    @Schema(description = "菜单图标")
    private String menuIcon;

    @Schema(description = "是否外链：0否,1是")
    private Integer isExternal;

    @Schema(description = "是否缓存：0否,1是")
    private Integer isCached;

    @Schema(description = "是否可见：0隐藏,1显示")
    private Integer isVisible;

    @Schema(description = "状态：1正常,0停用")
    private Integer status;

    @Schema(description = "排序字段")
    private Integer sort;

    @Schema(description = "扩展数据（JSON格式）")
    private String extraData;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}


