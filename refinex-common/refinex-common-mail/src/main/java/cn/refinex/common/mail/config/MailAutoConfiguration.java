package cn.refinex.common.mail.config;

import cn.refinex.common.mail.config.properties.MailProperties;
import cn.refinex.common.mail.config.properties.SmtpConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 邮件自动配置类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(MailProperties.class)
@ConditionalOnProperty(prefix = "refinex.mail", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MailAutoConfiguration {

    private final MailProperties mailProperties;

    /**
     * 配置多个 JavaMailSender Bean
     * <p>
     * 使用 Map 存储多个 JavaMailSender 实例，key 为 configId
     *
     * @return JavaMailSender Map
     */
    @Bean
    public Map<String, JavaMailSender> javaMailSenderMap() {
        Map<String, JavaMailSender> senderMap = new HashMap<>();

        for (SmtpConfig smtpConfig : mailProperties.getSmtpConfigs()) {
            JavaMailSenderImpl mailSender = createMailSender(smtpConfig);
            senderMap.put(smtpConfig.getConfigId(), mailSender);
            log.info("邮件模块：已加载 SMTP 配置 [{}] - {}:{}", 
                    smtpConfig.getConfigId(), 
                    smtpConfig.getHost(), 
                    smtpConfig.getPort());
        }

        if (senderMap.isEmpty()) {
            log.warn("邮件模块：未配置任何 SMTP 服务器");
        } else {
            log.info("邮件模块：共加载 {} 个 SMTP 配置，默认配置：{}", 
                    senderMap.size(), 
                    mailProperties.getDefaultSmtp());
        }

        return senderMap;
    }

    /**
     * 创建 JavaMailSender 实例
     *
     * @param smtpConfig SMTP 配置
     * @return JavaMailSender
     */
    private JavaMailSenderImpl createMailSender(SmtpConfig smtpConfig) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // 基础配置
        mailSender.setHost(smtpConfig.getHost());
        mailSender.setPort(smtpConfig.getPort());
        mailSender.setUsername(smtpConfig.getUsername());
        mailSender.setPassword(smtpConfig.getPassword());
        mailSender.setProtocol(smtpConfig.getProtocol());
        mailSender.setDefaultEncoding(smtpConfig.getDefaultEncoding());

        // JavaMail 属性
        Properties properties = new Properties();
        if (smtpConfig.getProperties() != null) {
            properties.putAll(smtpConfig.getProperties());
        }
        mailSender.setJavaMailProperties(properties);

        return mailSender;
    }

    /**
     * 配置 Thymeleaf 模板引擎, 用于渲染邮件模板
     *
     * @return TemplateEngine
     */
    @Bean
    public TemplateEngine emailTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        templateEngine.addTemplateResolver(textTemplateResolver());
        log.info("邮件模块：Thymeleaf 模板引擎已初始化");
        return templateEngine;
    }

    /**
     * HTML 模板解析器
     *
     * @return ITemplateResolver
     */
    private ITemplateResolver htmlTemplateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/mail/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(true);
        resolver.setOrder(1);
        resolver.setCheckExistence(true);
        return resolver;
    }

    /**
     * 文本模板解析器
     *
     * @return ITemplateResolver
     */
    private ITemplateResolver textTemplateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/mail/");
        resolver.setSuffix(".txt");
        resolver.setTemplateMode(TemplateMode.TEXT);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(true);
        resolver.setOrder(2);
        resolver.setCheckExistence(true);
        return resolver;
    }
}

