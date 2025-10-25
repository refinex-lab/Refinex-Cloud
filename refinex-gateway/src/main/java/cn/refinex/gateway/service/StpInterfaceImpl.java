package cn.refinex.gateway.service;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.domain.model.LoginUser;
import cn.refinex.common.enums.UserType;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.gateway.client.PlatformUserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 自定义 StpInterface 实现，用于 Sa-Token 权限验证
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final PlatformUserServiceClient platformUserServiceClient;

    /**
     * 获取用户权限列表
     *
     * @param loginId   账号id
     * @param loginType 账号类型
     * @return 用户权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 获取登录用户
        LoginUser loginUser = LoginHelper.getLoginUser();

        // 如果登录用户信息为空或者登录用户登录ID不一致，从 platform 服务重新获取用户权限
        if (Objects.isNull(loginUser) || !loginUser.getLoginId().equals(loginId)) {
            // 获取 loginId 组合(用户类型:用户ID)
            List<String> loginIdList = StrUtil.split(loginId.toString(), ":");
            Long userId = Convert.toLong(loginIdList.get(1));

            // 调用 platform 服务获取用户权限
            ApiResult<Set<String>> result = platformUserServiceClient.getUserMenuPermissions(userId);
            if (!result.isSuccess()) {
                log.error("获取用户权限失败，userId: {}, error: {}", loginId, result.message());
                return List.of();
            }
            return result.data().stream().toList();
        }

        // 处理 APP 用户权限
        UserType userType = UserType.fromCode(loginUser.getUserType());
        if (userType == UserType.APP_USER) {
            // 注意: 移动端权限获取请根据实际业务场景进行调整，这里简单与 PC 用户权限保持一致
            Set<String> menuPermission = loginUser.getMenuPermission();
            return CollectionUtils.isEmpty(menuPermission) ? List.of() : menuPermission.stream().toList();
        }

        // 处理 PC 用户权限
        Set<String> menuPermission = loginUser.getMenuPermission();
        return CollectionUtils.isEmpty(menuPermission) ? List.of() : menuPermission.stream().toList();
    }

    /**
     * 获取用户角色列表
     *
     * @param loginId   账号id
     * @param loginType 账号类型
     * @return 用户角色列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 获取登录用户
        LoginUser loginUser = LoginHelper.getLoginUser();

        // 如果登录用户信息为空或者登录用户登录ID不一致，从 platform 服务重新获取用户角色
        if (Objects.isNull(loginUser) || !loginUser.getLoginId().equals(loginId)) {
            // 获取 loginId 组合(用户类型:用户ID)
            List<String> loginIdList = StrUtil.split(loginId.toString(), ":");
            Long userId = Convert.toLong(loginIdList.get(1));

            // 调用 platform 服务获取用户角色
            ApiResult<Set<String>> result = platformUserServiceClient.getUserRolePermissions(userId);
            if (!result.isSuccess()) {
                log.error("获取用户角色失败，userId: {}, error: {}", loginId, result.message());
                return List.of();
            }
            return result.data().stream().toList();
        }

        // 处理 APP 用户角色
        UserType userType = UserType.fromCode(loginUser.getUserType());
        if (userType == UserType.APP_USER) {
            // 注意: 移动端角色获取请根据实际业务场景进行调整，这里简单与 PC 用户角色保持一致
            Set<String> rolePermission = loginUser.getRolePermission();
            return CollectionUtils.isEmpty(rolePermission) ? List.of() : rolePermission.stream().toList();
        }

        // 处理 PC 用户角色
        Set<String> rolePermission = loginUser.getRolePermission();
        return CollectionUtils.isEmpty(rolePermission) ? List.of() : rolePermission.stream().toList();
    }
}
