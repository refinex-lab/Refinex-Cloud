package cn.refinex.platform.controller.user.internal;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.domain.model.LoginUser;
import cn.refinex.platform.controller.user.dto.request.ResetPasswordRequestDTO;
import cn.refinex.platform.controller.user.dto.request.UserCreateRequestDTO;
import cn.refinex.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器（内部服务调用）
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@SaIgnore // 内部服务调用
@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户信息、账号状态、会话管理等接口")
public class UserInternalController {

    private final UserService userService;

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
}

