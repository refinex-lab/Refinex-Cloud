package cn.refinex.platform.controller.role;

import cn.refinex.common.apilog.core.annotation.LogOperation;
import cn.refinex.common.apilog.core.enums.OperateTypeEnum;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.platform.controller.role.dto.request.UserRoleBindRequestDTO;
import cn.refinex.platform.controller.role.dto.response.RoleUserResponseDTO;
import cn.refinex.platform.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 角色用户管理控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@RestController
@RequestMapping("/roles/{roleId}/users")
@RequiredArgsConstructor
@Tag(name = "角色用户管理", description = "角色下的用户绑定、解绑、查询等操作")
public class SysRoleUserController {

    private final SysRoleService roleService;

    @PostMapping
    @LogOperation(operateDesc = "绑定用户到角色", operationType = OperateTypeEnum.CREATE)
    @Operation(summary = "绑定用户到角色", description = "为指定角色绑定一个或多个用户（支持临时授权）")
    @Parameter(name = "roleId", description = "角色 ID", required = true)
    @Parameter(name = "req", description = "用户角色绑定请求", required = true)
    public ApiResult<Void> bindUsers(
            @PathVariable("roleId") Long roleId,
            @Valid @RequestBody UserRoleBindRequestDTO req
    ) {
        Long operatorId = LoginHelper.getUserId();
        roleService.bindUserRoleWithValidity(
                roleId,
                req.getUserIds(),
                req.getValidFrom(),
                req.getValidUntil(),
                operatorId
        );
        return ApiResult.success(HttpStatusCode.CREATED, null);
    }

    @DeleteMapping("/{userId}")
    @LogOperation(operateDesc = "解绑用户角色", operationType = OperateTypeEnum.DELETE)
    @Operation(summary = "解绑用户角色", description = "将用户从指定角色中移除")
    @Parameter(name = "roleId", description = "角色 ID", required = true)
    @Parameter(name = "userId", description = "用户 ID", required = true)
    public ApiResult<Void> unbindUser(
            @PathVariable("roleId") Long roleId,
            @PathVariable("userId") Long userId
    ) {
        roleService.unbindUserRole(roleId, userId);
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @GetMapping
    @Operation(summary = "分页查询角色下的用户", description = "查询指定角色下的所有用户列表")
    @Parameter(name = "roleId", description = "角色 ID", required = true)
    @Parameter(name = "username", description = "用户名，支持模糊查询")
    @Parameter(name = "nickname", description = "昵称，支持模糊查询")
    @Parameter(name = "mobile", description = "手机号，精确查询")
    @Parameter(name = "email", description = "邮箱，精确查询")
    @Parameter(name = "pageNum", description = "页码，从1开始")
    @Parameter(name = "pageSize", description = "每页数量")
    public ApiResult<PageResult<RoleUserResponseDTO>> queryRoleUsers(
            @PathVariable("roleId") Long roleId,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "mobile", required = false) String mobile,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") int pageSize
    ) {
        PageRequest pr = new PageRequest(pageNum, pageSize);
        PageResult<RoleUserResponseDTO> result = roleService.pageQueryRoleUsers(roleId, username, nickname, mobile, email, pr);
        return ApiResult.success(result);
    }
}

