package cn.refinex.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Http Interface 客户端前缀常量, 值对应注册到 Nacos 中的服务名
 *
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SystemHttpServiceConstants {

    // 鉴权服务
    public static final String AUTH_SERVICE_NAME = "/refinex-auth";

    // 平台服务
    public static final String PLATFORM_SERVICE_NAME = "/refinex-platform";

    // 知识库服务
    public static final String KB_SERVICE_NAME = "/refinex-kb";

    // AI服务
    public static final String AI_SERVICE_NAME = "/refinex-ai";

}
