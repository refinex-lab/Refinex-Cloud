package cn.refinex.platform.service;

import cn.refinex.common.domain.model.LoginUser;
import cn.refinex.platform.domain.dto.request.ResetPasswordRequest;
import cn.refinex.platform.domain.dto.request.UserCreateRequest;
import cn.refinex.platform.domain.dto.request.UserDisableRequest;
import cn.refinex.platform.domain.dto.request.UserKickoutRequest;
import cn.refinex.platform.domain.dto.response.UserDisableStatusResponse;
import cn.refinex.platform.domain.model.UserSessionDTO;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface UserService {

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    LoginUser getUserInfoByUsername(String username);

    /**
     * 根据用户 ID 获取用户信息
     *
     * @param userId 用户 ID
     * @return 用户信息
     */
    LoginUser getUserInfoByUserId(Long userId);

    /**
     * 根据手机号获取用户信息
     *
     * @param mobile 手机号
     * @return 用户信息
     */
    LoginUser getUserInfoByMobile(String mobile);

    /**
     * 根据邮箱获取用户信息
     *
     * @param email 邮箱
     * @return 用户信息
     */
    LoginUser getUserInfoByEmail(String email);

    /**
     * 注册用户
     *
     * @param request 创建用户请求参数
     * @return 注册结果
     */
    Boolean registerUser(UserCreateRequest request);

    /**
     * 封禁账号
     *
     * @param request 封禁请求
     */
    void disableUser(UserDisableRequest request);

    /**
     * 解封账号
     *
     * @param userId  用户 ID
     * @param service 服务类型（可选）
     */
    void untieUser(Long userId, String service);

    /**
     * 查询封禁状态
     *
     * @param userId  用户 ID
     * @param service 服务类型（可选）
     * @return 封禁状态
     */
    UserDisableStatusResponse getUserStatus(Long userId, String service);

    /**
     * 查询用户登录设备列表
     *
     * @param userId 用户 ID
     * @return 会话列表
     */
    List<UserSessionDTO> listUserSessions(Long userId);

    /**
     * 踢人下线
     *
     * @param request 踢人下线请求
     */
    void kickoutUser(UserKickoutRequest request);

    /**
     * 踢出用户所有设备
     *
     * @param userId 用户 ID
     */
    void kickoutAll(Long userId);

     /**
      * 重置密码
      *
      * @param request 重置密码请求参数
      * @return 重置结果
      */
    Boolean resetPassword(ResetPasswordRequest request);
}
