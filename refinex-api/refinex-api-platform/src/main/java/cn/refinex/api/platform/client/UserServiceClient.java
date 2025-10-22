package cn.refinex.api.platform.client;

import cn.refinex.api.platform.domain.dto.request.ResetPasswordRequest;
import cn.refinex.api.platform.domain.dto.request.UserCreateRequest;
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

/**
 * 用户服务 Feign 客户端
 *
 * @author Refinex
 * @since 1.0.0
 */
@Validated
@FeignClient(
        name = SystemFeignConstants.PLATFORM_SERVICE,
        contextId = "userServiceClient",
        path = "/internal/users"
)
@Tag(name = "用户服务 Feign 客户端", description = "提供用户注册、查询等相关的内部 Feign 接口")
public interface UserServiceClient {

    @PostMapping
    @Operation(summary = "注册用户", description = "创建新用户账号")
    @Parameter(name = "request", description = "用户创建请求参数", required = true)
    ApiResult<Boolean> registerUser(@RequestBody UserCreateRequest request);

    @GetMapping("/by-username")
    @Operation(summary = "根据用户名获取登录用户", description = "根据用户名查询登录用户信息")
    @Parameter(name = "username", description = "用户名", required = true)
    ApiResult<LoginUser> getLoginUserByUserName(@RequestParam("username") @NotBlank(message = "用户名不能为空") String username);

    @GetMapping("/{userId}/login-info")
    @Operation(summary = "根据用户ID获取登录用户", description = "根据用户ID查询登录用户信息")
    @Parameter(name = "userId", description = "用户ID", required = true)
    ApiResult<LoginUser> getLoginUserById(@PathVariable("userId") @NotNull(message = "用户ID不能为空") Long userId);

    @GetMapping("/by-mobile")
    @Operation(summary = "根据手机号获取登录用户", description = "根据手机号查询登录用户信息")
    @Parameter(name = "mobile", description = "用户手机号", required = true)
    ApiResult<LoginUser> getLoginUserByMobile(@RequestParam("mobile") @NotBlank(message = "用户手机号不能为空") String mobile);

    @GetMapping("/by-email")
    @Operation(summary = "根据邮箱获取登录用户", description = "根据邮箱查询登录用户信息")
    @Parameter(name = "email", description = "用户邮箱", required = true)
    ApiResult<LoginUser> getLoginUserByEmail(@RequestParam("email") @NotBlank(message = "用户邮箱不能为空") String email);

    @PutMapping("/password/reset")
    @Operation(summary = "重置密码", description = "根据邮箱验证码重置用户密码")
    @Parameter(name = "request", description = "重置密码请求参数", required = true)
    ApiResult<Boolean> resetPassword(@RequestBody ResetPasswordRequest request);
}
