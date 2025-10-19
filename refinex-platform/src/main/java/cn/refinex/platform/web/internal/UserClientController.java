package cn.refinex.platform.web.internal;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.domain.model.LoginUser;
import cn.refinex.platform.api.domain.dto.request.ResetPasswordRequest;
import cn.refinex.platform.api.domain.dto.request.UserCreateRequest;
import cn.refinex.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户服务控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Validated
@SaIgnore // 内部接口，不需要用户令牌
@RestController
@RequiredArgsConstructor
public class UserClientController {

    private final UserService userService;

    /**
     * 注册用户
     *
     * @param request 创建用户请求参数
     * @return 注册结果
     */
    public ApiResult<Boolean> registerUser(UserCreateRequest request) {
        return ApiResult.success(userService.registerUser(request));
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @PostMapping("/user/getLoginUserByUserName")
    public ApiResult<LoginUser> getLoginUserByUserName(String username) {
        return ApiResult.success(userService.getUserInfoByUsername(username));
    }

    /**
     * 根据用户 ID 获取用户信息
     *
     * @param userId 用户 ID
     * @return 用户信息
     */
    public ApiResult<LoginUser> getLoginUserById(Long userId) {
        return ApiResult.success(userService.getUserInfoByUserId(userId));
    }

    /**
     * 根据手机号获取用户信息
     *
     * @param mobile 手机号
     * @return 用户信息
     */
    public ApiResult<LoginUser> getLoginUserByMobile(String mobile) {
        return ApiResult.success(userService.getUserInfoByMobile(mobile));
    }

    /**
     * 根据邮箱获取用户信息
     *
     * @param email 邮箱
     * @return 用户信息
     */
    public ApiResult<LoginUser> getLoginUserByEmail(String email) {
        return ApiResult.success(userService.getUserInfoByEmail(email));
    }

    /**
     * 重置密码
     *
     * @param request 重置密码请求参数
     * @return 重置结果
     */
    public ApiResult<Boolean> resetPassword(ResetPasswordRequest request) {
        return ApiResult.success(userService.resetPassword(request));
    }
}
