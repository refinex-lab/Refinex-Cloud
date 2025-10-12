package cn.refinex.common.satoken.core.service;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.enums.UserType;
import cn.refinex.common.exception.SystemException;
import cn.refinex.common.exception.code.ResultCode;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.common.utils.spring.SpringUtils;
import cn.refinex.platform.api.PermissionFeignClient;
import cn.refinex.platform.domain.model.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 自定义权限验证接口扩展
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
public class StpInterfaceImpl implements StpInterface {

    /**
     * 获取用户权限列表
     *
     * @param loginId   账号id
     * @param loginType 账号类型
     * @return 用户权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 从 LoginHelper 中获取登录用户
        LoginUser loginUser = LoginHelper.getLoginUser();

        // 如果登录用户信息为空或者登录用户ID与请求参数不一致，从 platform 服务重新获取用户权限
        if (Objects.isNull(loginUser) || !loginUser.getLoginId().equals(loginId)) {
            PermissionFeignClient permissionFeignClient = getPermissionFeignClient();
            if (Objects.nonNull(permissionFeignClient)) {
                // 获取 loginId 组合(用户类型:用户ID)
                List<String> loginIdList = StrUtil.split(loginId.toString(), ":");

                // 调用 platform 服务获取用户权限
                ApiResult<Set<String>> result = permissionFeignClient.getUserMenuPermissions(Convert.toLong(loginIdList.get(1)));
                if (!result.isSuccess()) {
                    log.error("获取用户权限失败，userId: {}, error: {}", loginId, result.getMessage());
                    return List.of();
                }
                return result.getData().stream().toList();
            } else {
                log.error("PermissionFeignClient 未初始化");
                throw new SystemException(ResultCode.INTERNAL_ERROR.getCode(), "PermissionFeignClient 未初始化");
            }
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
        // 从 LoginHelper 中获取登录用户
        LoginUser loginUser = LoginHelper.getLoginUser();

        // 如果登录用户信息为空或者登录用户ID与请求参数不一致，从 platform 服务重新获取用户权限
        if (Objects.isNull(loginUser) || !loginUser.getLoginId().equals(loginId)) {
            PermissionFeignClient permissionFeignClient = getPermissionFeignClient();
            if (Objects.nonNull(permissionFeignClient)) {
                // 获取 loginId 组合(用户类型:用户ID)
                List<String> loginIdList = StrUtil.split(loginId.toString(), ":");

                // 调用 platform 服务获取用户角色
                ApiResult<Set<String>> result = permissionFeignClient.getUserRolePermissions(Convert.toLong(loginIdList.get(1)));
                if (!result.isSuccess()) {
                    log.error("获取用户角色失败，userId: {}, error: {}", loginId, result.getMessage());
                    return List.of();
                }
            } else {
                log.error("PermissionFeignClient 未初始化");
                throw new SystemException(ResultCode.INTERNAL_ERROR.getCode(), "PermissionFeignClient 未初始化");
            }
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

    /**
     * 获取 PermissionFeignClient Bean
     *
     * @return PermissionFeignClient Bean
     */
    public PermissionFeignClient getPermissionFeignClient() {
        try {
            return SpringUtils.getBean(PermissionFeignClient.class);
        } catch (Exception e) {
            log.error("获取 PermissionFeignClient Bean 失败", e);
            return null;
        }
    }
}
