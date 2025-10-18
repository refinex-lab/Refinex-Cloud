package cn.refinex.platform.api;

import cn.refinex.common.constants.SystemFeignConstants;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.domain.model.LoginUser;
import cn.refinex.platform.domain.dto.request.ResetPasswordRequest;
import cn.refinex.platform.domain.dto.request.UserCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务 Feign 客户端
 *
 * @author Refinex
 * @since 1.0.0
 */
@Validated
@Tag(name = "用户服务 Feign API")
@FeignClient(
        name = SystemFeignConstants.PLATFORM_SERVICE,
        path = "/user",
        contextId = "userFeignClient"
)
public interface UserFeignClient {

    @PostMapping("/registerUser")
    @Operation(summary = "注册用户", description = "根据创建用户请求参数注册用户")
    @Parameter(name = "request", description = "创建用户请求参数", required = true)
    ApiResult<Boolean> registerUser(@RequestBody UserCreateRequest request);

    @PostMapping("/getLoginUserByUserName")
    @Operation(summary = "根据用户名获取登录用户", description = "根据用户名获取登录用户")
    @Parameter(name = "username", description = "用户名", required = true)
    ApiResult<LoginUser> getLoginUserByUserName(@RequestParam("username") @NotBlank(message = "用户名不能为空") String username);

    @PostMapping("/getLoginUserById")
    @Operation(summary = "根据用户ID获取登录用户", description = "根据用户ID获取登录用户")
    @Parameter(name = "userId", description = "用户ID", required = true)
    ApiResult<LoginUser> getLoginUserById(@RequestParam("userId") @NotBlank(message = "用户ID不能为空") Long userId);

    @PostMapping("/getLoginUserByMobile")
    @Operation(summary = "根据用户手机号获取登录用户", description = "根据用户手机号获取登录用户")
    @Parameter(name = "mobile", description = "用户手机号", required = true)
    ApiResult<LoginUser> getLoginUserByMobile(@RequestParam("mobile") @NotBlank(message = "用户手机号不能为空") String mobile);

    @PostMapping("/getLoginUserByEmail")
    @Operation(summary = "根据用户邮箱获取登录用户", description = "根据用户邮箱获取登录用户")
    @Parameter(name = "email", description = "用户邮箱", required = true)
    ApiResult<LoginUser> getLoginUserByEmail(@RequestParam("email") @NotBlank(message = "用户邮箱不能为空") String email);

    @PostMapping("/resetPassword")
    @Operation(summary = "重置密码", description = "根据重置密码请求参数重置密码")
    @Parameter(name = "request", description = "重置密码请求参数", required = true)
    ApiResult<Boolean> resetPassword(@RequestBody ResetPasswordRequest request);
}
