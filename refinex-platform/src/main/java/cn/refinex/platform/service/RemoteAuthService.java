package cn.refinex.platform.service;

import cn.refinex.api.auth.client.AuthFeignClient;
import cn.refinex.api.auth.domain.dto.UserInfoDTO;
import cn.refinex.common.domain.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 远程认证服务
 *
 * @author Refinex
 * @since 2025-10-04
 */
@Slf4j
@Service
public class RemoteAuthService {

    @Autowired
    private AuthFeignClient authFeignClient;

    /**
     * 获取当前用户信息（通过 Feign 调用）
     *
     * @return 用户信息
     */
    public UserInfoDTO getCurrentUserInfo() {
        try {
            ApiResult<UserInfoDTO> result = authFeignClient.getUserInfo();

            if ("200".equals(result.getCode())) {
                return result.getData();
            } else {
                log.error("获取用户信息失败: {}", result.getMessage());
                return null;
            }
        } catch (Exception e) {
            log.error("调用认证服务失败", e);
            return null;
        }
    }

    /**
     * 验证用户权限（通过 Feign 调用）
     *
     * @param permission 权限码
     * @return 是否拥有权限
     */
    public boolean checkPermission(String permission) {
        try {
            ApiResult<Boolean> result = authFeignClient.checkPermission(permission);

            if ("200".equals(result.getCode())) {
                return Boolean.TRUE.equals(result.getData());
            } else {
                log.error("验证权限失败: {}", result.getMessage());
                return false;
            }
        } catch (Exception e) {
            log.error("调用认证服务失败", e);
            return false;
        }
    }
}

