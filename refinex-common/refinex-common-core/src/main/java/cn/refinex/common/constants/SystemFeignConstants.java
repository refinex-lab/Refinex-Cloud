package cn.refinex.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Feign 客户端常量类
 * <p>
 * reference from <a href="https://github.com/YunaiV/yudao-cloud/blob/master/yudao-framework/yudao-common/src/main/java/cn/iocoder/yudao/framework/common/enums/RpcConstants.java">...</a>
 *
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SystemFeignConstants {

    // 内部接口前缀，内部接口调用时使用，不需要用户令牌
    public static final String INNER_API_PREFIX = "/internal";

    // 统一 OpenFeign API 前缀
    public static final String FEIGN_API_PREFIX = "/feign-api";

    // 鉴权服务 refinex-auth
    public static final String AUTH_SERVICE = "refinex-auth";
    // 鉴权服务 路径前缀
    public static final String AUTH_API_PREFIX = FEIGN_API_PREFIX + "/auth";

    // 平台服务 refinex-platform
    public static final String PLATFORM_SERVICE = "refinex-platform";
    // 平台服务 路径前缀
    public static final String PLATFORM_API_PREFIX = FEIGN_API_PREFIX + "/platform";

    // 知识库服务 refinex-kb
    public static final String KB_SERVICE = "refinex-kb";
    // 知识库服务 路径前缀
    public static final String KB_API_PREFIX = FEIGN_API_PREFIX + "/kb";

    // AI服务 refinex-ai
    public static final String AI_SERVICE = "refinex-ai";
    // AI服务 路径前缀
    public static final String AI_API_PREFIX = FEIGN_API_PREFIX + "/ai";

}
