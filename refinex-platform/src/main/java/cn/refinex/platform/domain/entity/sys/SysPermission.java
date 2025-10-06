package cn.refinex.platform.domain.entity.sys;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 权限资源实体类
 * <p>
 * 对应数据库表：sys_permission
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "权限资源实体")
public class SysPermission {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "权限编码", example = "content:create")
    private String permissionCode;

    @Schema(description = "权限名称", example = "创建内容")
    private String permissionName;

    @Schema(description = "权限类型：menu菜单,button按钮,api接口", example = "api")
    private String permissionType;

    @Schema(description = "父权限ID，根权限为0", example = "0")
    private Long parentId;

    @Schema(description = "模块名称", example = "content")
    private String moduleName;

    @Schema(description = "资源路径或API路径", example = "/api/content/create")
    private String resourcePath;

    @Schema(description = "HTTP方法：GET,POST,PUT,DELETE,*", example = "POST")
    private String httpMethod;

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

    @Schema(description = "备注说明", example = "内容创建权限")
    private String remark;

    @Schema(description = "排序字段", example = "0")
    private Integer sort;

    @Schema(description = "状态：0正常,1停用", example = "0")
    private Integer status;

    @Schema(description = "扩展数据（JSON格式）")
    private String extraData;
}
