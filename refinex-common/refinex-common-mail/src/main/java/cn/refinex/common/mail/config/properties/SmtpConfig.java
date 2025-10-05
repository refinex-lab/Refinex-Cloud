package cn.refinex.common.mail.config.properties;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * SMTP 配置类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
public class SmtpConfig {

    /**
     * 配置 ID（唯一标识）
     */
    private String configId;

    /**
     * SMTP 服务器地址
     */
    private String host;

    /**
     * SMTP 端口
     */
    private Integer port = 587;

    /**
     * 发件人邮箱账号
     */
    private String username;

    /**
     * 发件人邮箱密码/授权码
     */
    private String password;

    /**
     * 发件人邮箱地址
     */
    private String from;

    /**
     * 发件人显示名称
     */
    private String fromName;

    /**
     * 协议（默认 smtp）
     */
    private String protocol = "smtp";

    /**
     * 默认编码（默认 UTF-8）
     */
    private String defaultEncoding = "UTF-8";

    /**
     * JavaMail 额外属性
     */
    private Map<String, String> properties = new HashMap<>();
}

