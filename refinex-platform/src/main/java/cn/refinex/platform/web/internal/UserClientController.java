package cn.refinex.platform.web.internal;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.refinex.api.platform.client.UserServiceClient;
import cn.refinex.api.platform.domain.dto.request.ResetPasswordRequest;
import cn.refinex.api.platform.domain.dto.request.UserCreateRequest;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.domain.model.LoginUser;
import cn.refinex.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户服务内部接口实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@Validated
@SaIgnore // 内部接口，不需要用户令牌
@RestController
@RequiredArgsConstructor
public class UserClientController implements UserServiceClient {

    private final UserService userService;

    /**
     * 注册用户
     *
     * @param request 用户创建请求
     * @return 注册结果
     */
    @Override
    public ApiResult<Boolean> registerUser(UserCreateRequest request) {
        return ApiResult.success(userService.registerUser(request));
    }

    /**
     * 根据用户名获取登录用户信息
     *
     * @param username 用户名
     * @return 登录用户信息
     */
    @Override
    public ApiResult<LoginUser> getLoginUserByUserName(String username) {
        return ApiResult.success(userService.getUserInfoByUsername(username));
    }

    /**
     * 根据用户ID获取登录用户信息
     *
     * @param userId 用户ID
     * @return 登录用户信息
     */
    @Override
    public ApiResult<LoginUser> getLoginUserById(Long userId) {
        return ApiResult.success(userService.getUserInfoByUserId(userId));
    }

    /**
     * 根据手机号获取登录用户信息
     *
     * @param mobile 手机号
     * @return 登录用户信息
     */
    @Override
    public ApiResult<LoginUser> getLoginUserByMobile(String mobile) {
        return ApiResult.success(userService.getUserInfoByMobile(mobile));
    }

    /**
     * 根据邮箱获取登录用户信息
     *
     * @param email 邮箱
     * @return 登录用户信息
     */
    @Override
    public ApiResult<LoginUser> getLoginUserByEmail(String email) {
        return ApiResult.success(userService.getUserInfoByEmail(email));
    }

    /**
     * 重置密码
     *
     * @param request 重置密码请求
     * @return 重置结果
     */
    @Override
    public ApiResult<Boolean> resetPassword(ResetPasswordRequest request) {
        return ApiResult.success(userService.resetPassword(request));
    }
}
