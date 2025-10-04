package cn.refinex.api.auth.client;

import cn.refinex.api.auth.domain.dto.UserInfoDTO;
import cn.refinex.common.constants.FeignClientConstants;
import cn.refinex.common.domain.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 认证服务 Feign 客户端
 *
 * @author Refinex
 * @since 2025-10-04
 */
@FeignClient(
        name = FeignClientConstants.AUTH_SERVICE,
        path = FeignClientConstants.AUTH_API_PREFIX,
        fallbackFactory = AuthFeignClientFallback.class
)
public interface AuthFeignClient {

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/userinfo")
    ApiResult<UserInfoDTO> getUserInfo();

    /**
     * 验证用户权限
     *
     * @param permission 权限码
     * @return 是否拥有权限
     */
    @GetMapping("/check-permission")
    ApiResult<Boolean> checkPermission(@RequestParam("permission") String permission);
}

