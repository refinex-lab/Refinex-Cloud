package cn.refinex.platform.service;

import cn.refinex.common.domain.model.LoginUser;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.platform.controller.user.dto.request.*;
import cn.refinex.platform.controller.user.dto.response.UserDetailResponseDTO;
import cn.refinex.platform.controller.user.dto.response.UserDisableStatusResponseDTO;
import cn.refinex.platform.controller.user.dto.response.UserListResponseDTO;
import cn.refinex.platform.controller.user.dto.response.UserSessionResponseDTO;
import cn.refinex.platform.controller.user.vo.CurrentUserVo;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface UserService {

    /**
     * 根据用户 ID 获取用户名
     *
     * @param userId 用户 ID
     * @return 用户名
     */
    String getUsernameByUserId(Long userId);

    /**
     * 根据用户 ID 列表获取用户名映射
     *
     * @param userIds 用户 ID 列表
     * @return 用户名映射（用户 ID -> 用户名）
     */
    Map<String, Object> getUsernameMap(List<Long> userIds);

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
    Boolean registerUser(UserCreateRequestDTO request);

    /**
     * 封禁账号
     *
     * @param userId  用户 ID
     * @param request 封禁请求
     */
    void disableUser(Long userId, UserDisableRequestDTO request);

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
    UserDisableStatusResponseDTO getUserStatus(Long userId, String service);

    /**
     * 查询用户登录设备列表
     *
     * @param userId 用户 ID
     * @return 会话列表
     */
    List<UserSessionResponseDTO> listUserSessions(Long userId);

    /**
     * 踢人下线
     *
     * @param request 踢人下线请求
     */
    void kickoutUser(UserKickoutRequestDTO request);

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
    Boolean resetPassword(ResetPasswordRequestDTO request);

    /**
     * 初始化超级管理员
     */
    void initSuperAdmin();

    /**
     * 构建安全的当前用户视图
     * @param userId 用户ID
     * @return 当前用户安全视图
     */
    CurrentUserVo buildCurrentUserVo(Long userId);

    /**
     * 根据用户名关键词模糊搜索用户名列表
     *
     * @param keyword 用户名关键词
     * @param limit   返回数量限制
     * @return 用户名列表
     */
    List<String> searchUsernames(String keyword, Integer limit);

    /**
     * 分页查询用户列表
     *
     * @param request 查询条件
     * @return 用户列表分页结果
     */
    PageResult<UserListResponseDTO> listUsers(UserQueryRequestDTO request);

    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    UserDetailResponseDTO getUserDetail(Long userId);

    /**
     * 更新用户信息
     *
     * @param request 更新请求参数
     */
    void updateUser(UserUpdateRequestDTO request);

    /**
     * 更新用户状态
     *
     * @param request 状态更新请求参数
     */
    void updateUserStatus(UserStatusUpdateRequestDTO request);

    /**
     * 管理员重置用户密码
     *
     * @param request 重置密码请求参数
     */
    void adminResetPassword(AdminResetPasswordRequestDTO request);

    /**
     * 删除用户（逻辑删除）
     *
     * @param userId 用户ID
     */
    void deleteUser(Long userId);
}
