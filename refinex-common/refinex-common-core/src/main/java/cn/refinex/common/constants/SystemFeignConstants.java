package cn.refinex.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Feign 客户端常量类, 值对应注册到 Nacos 中的服务名
 *
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SystemFeignConstants {

    // 鉴权服务
    public static final String AUTH_SERVICE = "refinex-auth";

    // 平台服务
    public static final String PLATFORM_SERVICE = "refinex-platform";

    // 知识库服务
    public static final String KB_SERVICE = "refinex-kb";

    // AI服务
    public static final String AI_SERVICE = "refinex-ai";

}
