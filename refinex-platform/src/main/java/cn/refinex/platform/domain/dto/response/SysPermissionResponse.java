package cn.refinex.platform.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统权限 响应对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统权限响应")
public class SysPermissionResponse {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "权限编码")
    private String permissionCode;

    @Schema(description = "权限名称")
    private String permissionName;

    @Schema(description = "权限类型：menu菜单,button按钮,api接口")
    private String permissionType;

    @Schema(description = "父权限ID")
    private Long parentId;

    @Schema(description = "模块名称")
    private String moduleName;

    @Schema(description = "资源路径或API路径")
    private String resourcePath;

    @Schema(description = "HTTP方法：GET,POST,PUT,DELETE,*")
    private String httpMethod;

    @Schema(description = "状态：0正常,1停用")
    private Integer status;

    @Schema(description = "排序字段")
    private Integer sort;

    @Schema(description = "备注说明")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}


