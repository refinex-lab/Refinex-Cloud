package cn.refinex.platform.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@Schema(description = "角色 DTO")
public class SysRoleDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "角色 ID")
    private Long roleId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色权限字符串")
    private String roleCode;

    /**
     * 这里的数据范围由于暂时不考虑接入多租户的 B 端组织结构模式，暂时提供部分类型，完整类型后期考虑根据需要集成:
     * 1. 所有数据权限
     * 2. 自定义数据权限
     * 3. 本部门数据权限
     * 4. 本部门及以下数据权限
     * 5. 仅本人数据权限
     * 6. 部门及以下或本人数据权限
     */
    @Schema(description = "数据范围(1:所有数据权限 2:自定义数据权限 3:仅本人数据权限)")
    private String dataScope;
}
