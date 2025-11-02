package cn.refinex.platform.controller.role;

import cn.refinex.common.apilog.core.annotation.LogOperation;
import cn.refinex.common.apilog.core.enums.OperateTypeEnum;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.platform.controller.role.dto.converter.RoleConverter;
import cn.refinex.platform.controller.role.dto.request.RoleCreateRequestDTO;
import cn.refinex.platform.controller.role.dto.request.RoleUpdateRequestDTO;
import cn.refinex.platform.controller.role.dto.response.RoleResponseDTO;
import cn.refinex.platform.entity.sys.SysRole;
import cn.refinex.platform.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统角色管理控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "系统角色管理", description = "角色的增删改查、状态管理等接口")
public class SysRoleController {

    private final SysRoleService roleService;
    private final RoleConverter roleConverter;

    @PostMapping
    @LogOperation(operateDesc = "创建角色", operationType = OperateTypeEnum.CREATE)
    @Operation(summary = "创建角色", description = "创建新的角色")
    @Parameter(name = "req", description = "角色创建请求", required = true)
    public ApiResult<Long> createRole(@Valid @RequestBody RoleCreateRequestDTO req) {
        Long operatorId = LoginHelper.getUserId();
        Long roleId = roleService.createRole(req, operatorId);
        return ApiResult.success(HttpStatusCode.CREATED, roleId);
    }

    @PutMapping("/{id}")
    @LogOperation(operateDesc = "更新角色", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "更新角色", description = "更新指定角色的信息")
    @Parameter(name = "id", description = "角色 ID", required = true)
    @Parameter(name = "req", description = "角色更新请求", required = true)
    public ApiResult<Boolean> updateRole(@PathVariable("id") Long id, @Valid @RequestBody RoleUpdateRequestDTO req) {
        Long operatorId = LoginHelper.getUserId();
        boolean success = roleService.updateRole(id, req, operatorId);
        return ApiResult.success(success);
    }

    @PatchMapping("/{id}/status")
    @LogOperation(operateDesc = "更新角色状态", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "更新角色状态", description = "更新指定角色的状态（启用/停用）")
    @Parameter(name = "id", description = "角色 ID", required = true)
    @Parameter(name = "status", description = "状态：1正常,0停用", required = true)
    public ApiResult<Boolean> updateRoleStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") Integer status
    ) {
        Long operatorId = LoginHelper.getUserId();
        boolean success = roleService.updateRoleStatus(id, status, operatorId);
        return ApiResult.success(success);
    }

    @DeleteMapping("/{id}")
    @LogOperation(operateDesc = "删除角色", operationType = OperateTypeEnum.DELETE)
    @Operation(summary = "删除角色", description = "删除指定的角色")
    @Parameter(name = "id", description = "角色 ID", required = true)
    public ApiResult<Void> deleteRole(@PathVariable("id") Long id) {
        Long operatorId = LoginHelper.getUserId();
        roleService.deleteRole(id, operatorId);
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取角色详情", description = "根据 ID 获取角色详细信息")
    @Parameter(name = "id", description = "角色 ID", required = true)
    public ApiResult<RoleResponseDTO> getRole(@PathVariable("id") Long id) {
        SysRole role = roleService.getRoleById(id);
        return ApiResult.success(roleConverter.toResponseDTO(role));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "根据编码获取角色", description = "根据角色编码获取详细信息")
    @Parameter(name = "code", description = "角色编码", required = true)
    public ApiResult<RoleResponseDTO> getRoleByCode(@PathVariable("code") String code) {
        SysRole role = roleService.getRoleByCode(code);
        return ApiResult.success(roleConverter.toResponseDTO(role));
    }

    @GetMapping
    @Operation(summary = "分页查询角色", description = "根据条件分页查询角色")
    @Parameter(name = "roleCode", description = "角色编码，支持模糊查询")
    @Parameter(name = "roleName", description = "角色名称，支持模糊查询")
    @Parameter(name = "roleType", description = "角色类型：0前台角色,1后台角色")
    @Parameter(name = "status", description = "状态：1正常,0停用")
    @Parameter(name = "orderBy", description = "排序字段，如：sort, create_time")
    @Parameter(name = "orderDirection", description = "排序方向：ASC 或 DESC")
    @Parameter(name = "pageNum", description = "页码，从1开始")
    @Parameter(name = "pageSize", description = "每页数量")
    public ApiResult<PageResult<RoleResponseDTO>> searchRoles(
            @RequestParam(value = "roleCode", required = false) String roleCode,
            @RequestParam(value = "roleName", required = false) String roleName,
            @RequestParam(value = "roleType", required = false) Integer roleType,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "orderDirection", required = false) String orderDirection,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") int pageSize
    ) {
        PageRequest pr = new PageRequest(pageNum, pageSize, orderBy, orderDirection);
        PageResult<SysRole> pageResult = roleService.pageQueryRoles(roleCode, roleName, roleType, status, pr);

        // 转换为响应 DTO
        List<RoleResponseDTO> dtoList = roleConverter.toResponseDTOList(pageResult.getRecords());
        PageResult<RoleResponseDTO> dtoPageResult = new PageResult<>(
                dtoList,
                pageResult.getTotal(),
                pageResult.getPageNum(),
                pageResult.getPageSize()
        );

        return ApiResult.success(dtoPageResult);
    }

    @GetMapping("/enabled")
    @Operation(summary = "获取所有启用的角色", description = "查询所有状态为启用的角色")
    public ApiResult<List<RoleResponseDTO>> listEnabledRoles() {
        List<SysRole> roles = roleService.listEnabledRoles();
        return ApiResult.success(roleConverter.toResponseDTOList(roles));
    }

    @GetMapping("/max-sort")
    @Operation(summary = "获取角色最大排序值", description = "获取当前角色的最大排序值，用于新增时自动计算排序")
    public ApiResult<Integer> getMaxRoleSort() {
        Integer maxSort = roleService.getMaxRoleSort();
        return ApiResult.success(maxSort);
    }
}

