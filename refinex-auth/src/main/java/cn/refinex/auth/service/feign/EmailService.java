package cn.refinex.auth.service.feign;

import cn.refinex.common.constants.SystemFeignConstants;
import cn.refinex.platform.api.facade.EmailFacade;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 邮件发送 Feign 客户端
 *
 * @author Refinex
 * @since 1.0.0
 */
@Tag(name = "邮件发送 Feign API")
@FeignClient(
        name = SystemFeignConstants.PLATFORM_SERVICE,
        path = "/email",
        contextId = "emailFeignClient"
)
public interface EmailService extends EmailFacade {
}
