package cn.refinex.kb.client;

import cn.refinex.common.annotation.HttpInterfaceClient;
import cn.refinex.common.domain.ApiResult;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;
import java.util.Map;

import static cn.refinex.common.constants.SystemHttpServiceConstants.PLATFORM_SERVICE_NAME;

/**
 * 平台用户服务 HTTP Interface 客户端
 *
 * @author Refinex
 * @since 1.0.0
 */
@Validated
@HttpInterfaceClient(PLATFORM_SERVICE_NAME)
@HttpExchange("/users")
public interface PlatformUserServiceClient {

    @GetExchange("/{userId}/username")
    ApiResult<String> getUsernameByUserId(@PathVariable("userId") @NotNull(message = "用户ID不能为空") Long userId);

    @GetMapping("/username-map")
    ApiResult<Map<String, Object>> getUsernameMap(@RequestParam("userIds") @NotNull(message = "用户ID列表不能为空") List<Long> userIds);
}
