package cn.refinex.platform.domain.entity.sys;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色权限关联实体类
 * <p>
 * 对应数据库表：sys_role_permission
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "角色权限关联实体")
public class SysRolePermission {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "角色ID", example = "1")
    private Long roleId;

    @Schema(description = "权限ID", example = "1")
    private Long permissionId;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
