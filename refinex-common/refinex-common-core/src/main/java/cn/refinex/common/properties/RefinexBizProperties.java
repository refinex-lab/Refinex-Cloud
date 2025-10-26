package cn.refinex.common.properties;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 业务相关配置属性
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Validated
@SpringBootConfiguration
@ConfigurationProperties(prefix = "refinex.biz")
public class RefinexBizProperties {

    /**
     * 数据库敏感数据加密密钥
     */
    private String dbSensitiveDataKey;

    /**
     * RSA 私钥（用于解密前端使用 RSA + AES 混合加密的数据）
     */
    private String rsaPrivateKey;

    /**
     * 超级管理员配置
     */
    private SuperAdmin superAdmin = new SuperAdmin();

    /**
     * 超级管理员配置
     */
    @Data
    @Validated
    public static class SuperAdmin {
        /**
         * 用户名
         */
        @NotBlank(message = "用户名不能为空")
        private String username;

        /**
         * 手机号
         */
        @NotBlank(message = "手机号不能为空")
        private String mobile;

        /**
         * 邮箱
         */
        @Email(message = "邮箱格式错误")
        @NotBlank(message = "邮箱不能为空")
        private String email;

        /**
         * 密码
         */
        @NotBlank(message = "密码不能为空")
        private String password;

        /**
         * 昵称
         */
        @NotBlank(message = "昵称不能为空")
        private String nickname;
    }
}
