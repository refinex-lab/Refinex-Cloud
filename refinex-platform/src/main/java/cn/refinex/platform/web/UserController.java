package cn.refinex.platform.web;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.api.UserFeignClient;
import cn.refinex.platform.domain.dto.request.UserCreateRequest;
import cn.refinex.common.domain.model.LoginUser;
import cn.refinex.platform.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户服务控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
public class UserController implements UserFeignClient {

    private final UserServiceImpl userService;

    /**
     * 注册用户
     *
     * @param request 创建用户请求参数
     * @return 注册结果
     */
    @Override
    public ApiResult<Boolean> registerUser(UserCreateRequest request) {
        return ApiResult.success(userService.registerUser(request));
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public ApiResult<LoginUser> getLoginUserByUserName(String username) {
        return ApiResult.success(userService.getUserInfoByUsername(username));
    }

    /**
     * 根据用户 ID 获取用户信息
     *
     * @param userId 用户 ID
     * @return 用户信息
     */
    @Override
    public ApiResult<LoginUser> getLoginUserById(Long userId) {
        return ApiResult.success(userService.getUserInfoByUserId(userId));
    }

    /**
     * 根据手机号获取用户信息
     *
     * @param mobile 手机号
     * @return 用户信息
     */
    @Override
    public ApiResult<LoginUser> getLoginUserByMobile(String mobile) {
        return ApiResult.success(userService.getUserInfoByMobile(mobile));
    }

    /**
     * 根据邮箱获取用户信息
     *
     * @param email 邮箱
     * @return 用户信息
     */
    @Override
    public ApiResult<LoginUser> getLoginUserByEmail(String email) {
        return ApiResult.success(userService.getUserInfoByEmail(email));
    }
}
