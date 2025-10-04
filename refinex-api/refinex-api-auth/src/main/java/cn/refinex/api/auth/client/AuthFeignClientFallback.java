package cn.refinex.api.auth.client;

import cn.refinex.api.auth.domain.dto.UserInfoDTO;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.exception.code.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 认证服务 Feign 客户端降级工厂
 * <p>
 * 当认证服务调用失败时，提供降级响应
 * </p>
 *
 * @author Refinex
 * @since 2025-10-04
 */
@Slf4j
@Component
public class AuthFeignClientFallback implements FallbackFactory<AuthFeignClient> {

    @Override
    public AuthFeignClient create(Throwable cause) {
        log.error("AuthFeignClient 调用失败", cause);

        return new AuthFeignClient() {
            @Override
            public ApiResult<UserInfoDTO> getUserInfo() {
                log.error("获取用户信息失败，返回降级响应");
                return ApiResult.failure(ResultCode.INTERNAL_ERROR.getCode(), "认证服务暂时不可用");
            }

            @Override
            public ApiResult<Boolean> checkPermission(String permission) {
                log.error("验证权限失败，返回降级响应");
                return ApiResult.failure(ResultCode.INTERNAL_ERROR.getCode(), "认证服务暂时不可用");
            }
        };
    }
}

