package cn.refinex.platform.domain.entity.sys;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 菜单实体类
 * <p>
 * 对应数据库表：sys_menu
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "菜单实体")
public class SysMenu {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "菜单名称", example = "系统管理")
    private String menuName;

    @Schema(description = "父菜单ID，根菜单为0", example = "0")
    private Long parentId;

    @Schema(description = "菜单类型：M目录,C菜单,F按钮", example = "M")
    private String menuType;

    @Schema(description = "路由路径", example = "/system")
    private String routePath;

    @Schema(description = "组件路径", example = "system/index")
    private String componentPath;

    @Schema(description = "菜单图标", example = "el-icon-setting")
    private String menuIcon;

    @Schema(description = "是否外链：0否,1是", example = "0")
    private Integer isExternal;

    @Schema(description = "是否缓存：0否,1是", example = "0")
    private Integer isCached;

    @Schema(description = "是否可见：0隐藏,1显示", example = "1")
    private Integer isVisible;

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

    @Schema(description = "备注说明", example = "系统管理菜单")
    private String remark;

    @Schema(description = "排序字段", example = "0")
    private Integer sort;

    @Schema(description = "状态：0正常,1停用", example = "0")
    private Integer status;

    @Schema(description = "扩展数据（JSON格式）")
    private String extraData;
}
