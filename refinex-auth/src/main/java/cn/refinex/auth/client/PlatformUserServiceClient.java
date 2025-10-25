package cn.refinex.auth.client;

import cn.refinex.auth.domain.dto.request.ResetPasswordRequestDTO;
import cn.refinex.auth.domain.dto.request.UserCreateRequestDTO;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.domain.model.LoginUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import static cn.refinex.common.constants.SystemHttpServiceConstants.PLATFORM_SERVICE_NAME;

/**
 * 用户服务 HTTP Interface
 *
 * @author Refinex
 * @since 1.0.0
 */
@Validated
@HttpExchange(PLATFORM_SERVICE_NAME + "/users")
public interface PlatformUserServiceClient {

    @PostExchange("/users/register")
    ApiResult<Boolean> registerUser(@RequestBody @Valid UserCreateRequestDTO request);

    @PutExchange("/users/reset-password")
    ApiResult<Boolean> resetUserPassword(@RequestBody @Valid ResetPasswordRequestDTO request);

    @GetExchange("/by-email")
    ApiResult<LoginUser> getLoginUserByEmail(@RequestParam("email") @NotBlank(message = "用户邮箱不能为空") String email);

    @GetExchange("/users/by-username")
    ApiResult<LoginUser> getLoginUserByUserName(@RequestParam("username") @NotBlank(message = "用户名不能为空") String username);
}
