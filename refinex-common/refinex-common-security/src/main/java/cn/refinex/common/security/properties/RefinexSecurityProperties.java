package cn.refinex.common.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Refinex 安全配置属性
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "refinex.security")
public class RefinexSecurityProperties {

    /**
     * 是否启用安全认证，默认 true
     */
    private Boolean enabled = true;

    /**
     * 白名单路径（支持 Ant 风格通配符）
     */
    private List<String> whitelistPaths = new ArrayList<>();

    /**
     * 权限缓存配置
     */
    private CacheConfig cache = new CacheConfig();

    /**
     * Token 配置
     */
    private TokenConfig token = new TokenConfig();

    /**
     * 缓存配置
     */
    @Data
    public static class CacheConfig {
        /**
         * 是否启用权限缓存，默认 true
         */
        private Boolean enabled = true;

        /**
         * 缓存过期时间（秒），默认 1800（30分钟）
         */
        private Long ttl = 1800L;
    }

    /**
     * Token 配置
     */
    @Data
    public static class TokenConfig {
        /**
         * Token 有效期（秒），默认 7200（2小时）
         */
        private Long timeout = 7200L;

        /**
         * Token 临时有效期（秒），默认 1800（30分钟）
         */
        private Long activityTimeout = 1800L;

        /**
         * 是否允许同一账号并发登录，默认 true
         */
        private Boolean isConcurrent = true;

        /**
         * 是否共用一个 Token，默认 false
         */
        private Boolean isShare = false;
    }
}

