package cn.refinex.auth.service.feign;

import cn.refinex.common.constants.SystemFeignConstants;
import cn.refinex.platform.api.facade.UserFacade;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 用户服务 Feign 客户端
 *
 * @author Refinex
 * @since 1.0.0
 */
@FeignClient(
        name = SystemFeignConstants.PLATFORM_SERVICE,
        path = "/user",
        contextId = "userFeignClient"
)
public interface UserService extends UserFacade {
}
