package cn.refinex.api.platform.client.user;

import cn.refinex.api.platform.client.user.dto.request.ResetPasswordRequestDTO;
import cn.refinex.api.platform.client.user.dto.request.UserCreateRequestDTO;
import cn.refinex.common.constants.SystemFeignConstants;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.domain.model.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 用户服务 OpenFeign 接口契约
 *
 * @author Refinex
 * @since 1.0.0
 */
@Validated
@FeignClient(name = SystemFeignConstants.PLATFORM_SERVICE, contextId = "userServiceClient")
@Tag(name = "用户服务 OpenFeign 接口契约", description = "定义用户服务相关的 OpenFeign 接口契约")
public interface UserRemoteService {

    @PostMapping("/user/register")
    @Operation(summary = "注册用户", description = "创建新用户账号")
    @Parameter(name = "request", description = "用户创建请求参数", required = true)
    ApiResult<Boolean> registerUser(@RequestBody UserCreateRequestDTO request);

    @GetMapping("/user/by-username")
    @Operation(summary = "根据用户名获取登录用户", description = "根据用户名查询登录用户信息")
    @Parameter(name = "username", description = "用户名", required = true)
    ApiResult<LoginUser> getLoginUserByUserName(@RequestParam("username") @NotBlank(message = "用户名不能为空") String username);

    @GetMapping("/user/{userId}/login-info")
    @Operation(summary = "根据用户ID获取登录用户", description = "根据用户ID查询登录用户信息")
    @Parameter(name = "userId", description = "用户ID", required = true)
    ApiResult<LoginUser> getLoginUserById(@PathVariable("userId") @NotNull(message = "用户ID不能为空") Long userId);

    @GetMapping("/user/by-mobile")
    @Operation(summary = "根据手机号获取登录用户", description = "根据手机号查询登录用户信息")
    @Parameter(name = "mobile", description = "用户手机号", required = true)
    ApiResult<LoginUser> getLoginUserByMobile(@RequestParam("mobile") @NotBlank(message = "用户手机号不能为空") String mobile);

    @GetMapping("/user/by-email")
    @Operation(summary = "根据邮箱获取登录用户", description = "根据邮箱查询登录用户信息")
    @Parameter(name = "email", description = "用户邮箱", required = true)
    ApiResult<LoginUser> getLoginUserByEmail(@RequestParam("email") @NotBlank(message = "用户邮箱不能为空") String email);

    @PutMapping("/user/password/reset")
    @Operation(summary = "重置密码", description = "根据邮箱验证码重置用户密码")
    @Parameter(name = "request", description = "重置密码请求参数", required = true)
    ApiResult<Boolean> resetPassword(@RequestBody ResetPasswordRequestDTO request);

    @GetMapping("/user/{userId}/role-permissions")
    @Operation(summary = "获取用户角色权限列表", description = "根据用户ID获取用户拥有的所有角色权限")
    @Parameter(name = "userId", description = "用户ID", required = true)
    ApiResult<Set<String>> getUserRolePermissions(@PathVariable("userId") Long userId);

    @GetMapping("/user/{userId}/menu-permissions")
    @Operation(summary = "获取用户菜单权限列表", description = "根据用户ID获取用户拥有的所有菜单权限")
    @Parameter(name = "userId", description = "用户ID", required = true)
    ApiResult<Set<String>> getUserMenuPermissions(@PathVariable("userId") Long userId);
}
