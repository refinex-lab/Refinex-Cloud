package cn.refinex.auth.client.permission;

import cn.refinex.common.constants.FeignConstants;
import cn.refinex.common.domain.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
        name = FeignConstants.AUTH_SERVICE,
        path = FeignConstants.AUTH_API_PREFIX
)
@Tag(name = "认证服务 Feign API", description = "认证管理相关接口")
public interface PermissionClient {

    @GetMapping("/check-permission")
    @Operation(summary = "验证用户权限")
    @Parameter(name = "permission", description = "权限码", example = "ROLE_ADMIN", required = true)
    ApiResult<Boolean> checkPermission(@RequestParam("permission") String permission);
}

