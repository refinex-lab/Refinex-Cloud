package cn.refinex.gateway.client;

import cn.refinex.common.domain.ApiResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Set;

import static cn.refinex.common.constants.SystemHttpServiceConstants.PLATFORM_SERVICE_NAME;

/**
 * 用户服务 HTTP Interface
 *
 * @author Refinex
 * @since 1.0.0
 */
@HttpExchange(PLATFORM_SERVICE_NAME + "/users")
public interface PlatformUserServiceClient {

    @GetExchange("/{userId}/role-permissions")
    ApiResult<Set<String>> getUserRolePermissions(@PathVariable("userId") Long userId);

    @GetExchange("/{userId}/menu-permissions")
    ApiResult<Set<String>> getUserMenuPermissions(@PathVariable("userId") Long userId);
}
