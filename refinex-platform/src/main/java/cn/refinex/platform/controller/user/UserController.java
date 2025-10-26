package cn.refinex.platform.controller.user;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.domain.model.LoginUser;
import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.platform.controller.user.dto.request.ResetPasswordRequestDTO;
import cn.refinex.platform.controller.user.dto.request.UserCreateRequestDTO;
import cn.refinex.platform.controller.user.dto.request.UserDisableRequestDTO;
import cn.refinex.platform.controller.user.dto.request.UserKickoutRequestDTO;
import cn.refinex.platform.controller.user.dto.response.UserDisableStatusResponseDTO;
import cn.refinex.platform.controller.user.dto.response.UserSessionResponseDTO;
import cn.refinex.platform.controller.user.vo.CurrentUserVo;
import cn.refinex.platform.service.PermissionService;
import cn.refinex.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 用户管理控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户信息、账号状态、会话管理等接口")
public class UserController {

    private final UserService userService;
    private final PermissionService permissionService;

    @PostMapping("/register")
    @Operation(summary = "注册用户", description = "创建新用户账号")
    @Parameter(name = "request", description = "用户创建请求参数", required = true)
    public ApiResult<Boolean> registerUser(@RequestBody UserCreateRequestDTO request) {
        return ApiResult.success(userService.registerUser(request));
    }

    @GetMapping("/by-username")
    @Operation(summary = "根据用户名获取登录用户", description = "根据用户名查询登录用户信息")
    @Parameter(name = "username", description = "用户名", required = true)
    public ApiResult<LoginUser> getLoginUserByUserName(@RequestParam("username") @NotBlank(message = "用户名不能为空") String username) {
        return ApiResult.success(userService.getUserInfoByUsername(username));
    }

    @GetMapping("/{userId}/login-info")
    @Operation(summary = "根据用户ID获取登录用户", description = "根据用户ID查询登录用户信息")
    @Parameter(name = "userId", description = "用户ID", required = true)
    public ApiResult<LoginUser> getLoginUserById(@PathVariable("userId") @NotNull(message = "用户ID不能为空") Long userId) {
        return ApiResult.success(userService.getUserInfoByUserId(userId));
    }

    @GetMapping("/by-mobile")
    @Operation(summary = "根据手机号获取登录用户", description = "根据手机号查询登录用户信息")
    @Parameter(name = "mobile", description = "用户手机号", required = true)
    public ApiResult<LoginUser> getLoginUserByMobile(@RequestParam("mobile") @NotBlank(message = "用户手机号不能为空") String mobile) {
        return ApiResult.success(userService.getUserInfoByMobile(mobile));
    }

    @GetMapping("/by-email")
    @Operation(summary = "根据邮箱获取登录用户", description = "根据邮箱查询登录用户信息")
    @Parameter(name = "email", description = "用户邮箱", required = true)
    public ApiResult<LoginUser> getLoginUserByEmail(@RequestParam("email") @NotBlank(message = "用户邮箱不能为空") String email) {
        return ApiResult.success(userService.getUserInfoByEmail(email));
    }

    @PutMapping("/reset-password")
    @Operation(summary = "重置密码", description = "根据邮箱验证码重置用户密码")
    @Parameter(name = "request", description = "重置密码请求参数", required = true)
    public ApiResult<Boolean> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        return ApiResult.success(userService.resetPassword(request));
    }

    @GetMapping("/{userId}/role-permissions")
    @Operation(summary = "获取用户角色权限列表", description = "根据用户ID获取用户拥有的所有角色权限")
    @Parameter(name = "userId", description = "用户ID", required = true)
    public ApiResult<Set<String>> getUserRolePermissions(@PathVariable("userId") Long userId) {
        return ApiResult.success(permissionService.getUserRolePermissions(userId));
    }

    @GetMapping("/{userId}/menu-permissions")
    @Operation(summary = "获取用户菜单权限列表", description = "根据用户ID获取用户拥有的所有菜单权限")
    @Parameter(name = "userId", description = "用户ID", required = true)
    public ApiResult<Set<String>> getUserMenuPermissions(@PathVariable("userId") Long userId) {
        return ApiResult.success(permissionService.getUserMenuPermissions(userId));
    }

    @GetMapping("/current")
    @Operation(summary = "获取当前登录用户信息", description = "获取当前登录用户的详细信息")
    public ApiResult<CurrentUserVo> getCurrentUser() {
        Long userId = LoginHelper.getUserId();
        if (Objects.isNull(userId)) {
            throw new BusinessException(HttpStatusCode.UNAUTHORIZED, "用户未登录");
        }
        CurrentUserVo currentUser = userService.buildCurrentUserVo(userId);
        return ApiResult.success(currentUser);
    }

    @PostMapping("/{userId}/disable")
    @Operation(summary = "封禁用户账号", description = "封禁指定用户账号，支持全局封禁和分类封禁")
    @Parameter(name = "userId", description = "用户 ID", required = true)
    @Parameter(name = "request", description = "封禁请求", required = true)
    public ApiResult<Void> disableUser(@PathVariable Long userId, @Valid @RequestBody UserDisableRequestDTO request) {
        userService.disableUser(userId, request);
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @DeleteMapping("/{userId}/disable")
    @Operation(summary = "解封用户账号", description = "解封指定用户账号")
    @Parameter(name = "userId", description = "用户 ID", required = true)
    @Parameter(name = "service", description = "服务类型", required = false)
    public ApiResult<Void> enableUser(
            @PathVariable Long userId,
            @RequestParam(required = false) String service
    ) {
        userService.untieUser(userId, service);
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @GetMapping("/{userId}/disable-status")
    @Operation(summary = "查询用户封禁状态", description = "查询指定用户的封禁状态")
    @Parameter(name = "userId", description = "用户 ID", required = true)
    @Parameter(name = "service", description = "服务类型")
    public ApiResult<UserDisableStatusResponseDTO> getDisableStatus(
            @PathVariable Long userId,
            @RequestParam(required = false) String service
    ) {
        UserDisableStatusResponseDTO status = userService.getUserStatus(userId, service);
        return ApiResult.success(status);
    }

    @GetMapping("/{userId}/sessions")
    @Operation(summary = "查询用户登录会话列表", description = "查询指定用户的所有登录设备和会话信息")
    @Parameter(name = "userId", description = "用户 ID", required = true)
    public ApiResult<List<UserSessionResponseDTO>> listUserSessions(@PathVariable Long userId) {
        List<UserSessionResponseDTO> sessions = userService.listUserSessions(userId);
        return ApiResult.success(sessions);
    }

    @DeleteMapping("/sessions")
    @Operation(summary = "踢除用户会话", description = "将指定用户会话踢下线，支持按设备类型或 Token 踢出")
    @Parameter(name = "request", description = "踢除请求", required = true)
    public ApiResult<Void> kickoutSession(@Valid @RequestBody UserKickoutRequestDTO request) {
        userService.kickoutUser(request);
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @DeleteMapping("/{userId}/sessions")
    @Operation(summary = "踢除用户所有会话", description = "将指定用户的所有设备踢下线")
    @Parameter(name = "userId", description = "用户 ID", required = true)
    public ApiResult<Void> kickoutAllSessions(@PathVariable Long userId) {
        userService.kickoutAll(userId);
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @GetMapping("/search-usernames")
    @Operation(summary = "模糊搜索用户名", description = "根据关键词模糊搜索用户名列表，用于自动完成输入")
    @Parameter(name = "keyword", description = "用户名关键词", required = true)
    @Parameter(name = "limit", description = "返回数量限制（默认10，最大50）")
    public ApiResult<List<String>> searchUsernames(
            @RequestParam("keyword") @NotBlank(message = "搜索关键词不能为空") String keyword,
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        List<String> usernames = userService.searchUsernames(keyword, limit);
        return ApiResult.success(usernames);
    }
}

