package cn.refinex.platform.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 角色分配权限请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "角色分配权限请求")
public class AssignRolePermissionsRequest {

    @NotEmpty(message = "权限ID列表不能为空")
    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;
}


