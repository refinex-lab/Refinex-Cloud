package cn.refinex.platform.client.user;

import cn.refinex.common.constants.FeignConstants;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.client.user.dto.request.CreateUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 用户服务 Feign 客户端
 *
 * @author Refinex
 * @since 1.0.0
 */
@FeignClient(
        name = FeignConstants.PLATFORM_SERVICE,
        path = FeignConstants.PLATFORM_API_PREFIX
)
@Tag(name = "用户服务 Feign API")
public interface UserClient {

    @PostMapping("/user/register")
    @Operation(summary = "注册用户", description = "根据创建用户请求参数注册用户")
    @Parameter(name = "request", description = "创建用户请求参数", required = true)
    ApiResult<Boolean> registerUser(CreateUserRequest request);
}
