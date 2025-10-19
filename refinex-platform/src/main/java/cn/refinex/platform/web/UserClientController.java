package cn.refinex.platform.web;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.domain.model.LoginUser;
import cn.refinex.platform.api.domain.dto.request.ResetPasswordRequest;
import cn.refinex.platform.api.domain.dto.request.UserCreateRequest;
import cn.refinex.platform.api.facade.UserFacade;
import cn.refinex.platform.service.UserService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户服务控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
public class UserClientController implements UserFacade {

    private final UserService userService;

    /**
     * 注册用户
     *
     * @param request 创建用户请求参数
     * @return 注册结果
     */
    @Override
    public ApiResult<Boolean> registerUser(@RequestBody UserCreateRequest request) {
        return ApiResult.success(userService.registerUser(request));
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public ApiResult<LoginUser> getLoginUserByUserName(@RequestParam("username") @NotBlank(message = "用户名不能为空") String username) {
        return ApiResult.success(userService.getUserInfoByUsername(username));
    }

    /**
     * 根据用户 ID 获取用户信息
     *
     * @param userId 用户 ID
     * @return 用户信息
     */
    @Override
    public ApiResult<LoginUser> getLoginUserById(@RequestParam("userId") @NotBlank(message = "用户ID不能为空") Long userId) {
        return ApiResult.success(userService.getUserInfoByUserId(userId));
    }

    /**
     * 根据手机号获取用户信息
     *
     * @param mobile 手机号
     * @return 用户信息
     */
    @Override
    public ApiResult<LoginUser> getLoginUserByMobile(@RequestParam("mobile") @NotBlank(message = "用户手机号不能为空") String mobile) {
        return ApiResult.success(userService.getUserInfoByMobile(mobile));
    }

    /**
     * 根据邮箱获取用户信息
     *
     * @param email 邮箱
     * @return 用户信息
     */
    @Override
    public ApiResult<LoginUser> getLoginUserByEmail(@RequestParam("email") @NotBlank(message = "用户邮箱不能为空") String email) {
        return ApiResult.success(userService.getUserInfoByEmail(email));
    }

    /**
     * 重置密码
     *
     * @param request 重置密码请求参数
     * @return 重置结果
     */
    @Override
    public ApiResult<Boolean> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ApiResult.success(userService.resetPassword(request));
    }
}
