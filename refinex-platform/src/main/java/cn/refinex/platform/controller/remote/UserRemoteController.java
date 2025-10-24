package cn.refinex.platform.controller.remote;

import cn.refinex.api.platform.client.user.UserRemoteService;
import cn.refinex.api.platform.client.user.dto.request.ResetPasswordRequestDTO;
import cn.refinex.api.platform.client.user.dto.request.UserCreateRequestDTO;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.domain.model.LoginUser;
import cn.refinex.platform.service.PermissionService;
import cn.refinex.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 用户服务 OpenFeign 接口实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
public class UserRemoteController implements UserRemoteService {

    private final UserService userService;
    private final PermissionService permissionService;

    /**
     * 注册用户
     *
     * @param request 用户创建请求
     * @return 注册结果
     */
    @Override
    public ApiResult<Boolean> registerUser(UserCreateRequestDTO request) {
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
    public ApiResult<Boolean> resetPassword(ResetPasswordRequestDTO request) {
        return ApiResult.success(userService.resetPassword(request));
    }

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 用户角色列表
     */
    @Override
    public ApiResult<Set<String>> getUserRolePermissions(Long userId) {
        return ApiResult.success(permissionService.getUserRolePermissions(userId));
    }

    /**
     * 获取用户菜单权限列表
     *
     * @param userId 用户ID
     * @return 用户菜单权限列表
     */
    @Override
    public ApiResult<Set<String>> getUserMenuPermissions(Long userId) {
        return ApiResult.success(permissionService.getUserMenuPermissions(userId));
    }
}
